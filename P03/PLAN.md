# P03 Plan – Wallet Under Concurrency

Project 3 extends the same wallet. The goal: **many people act on the same money or the same stock at the same time, and the result is always correct.**

## What we already have (from P02)

- 11 entities, JWT auth, roles, Swagger, SSE notifications.
- Transfers already lock both wallets (`PESSIMISTIC_WRITE`, fixed order, no deadlocks).
- Emails and webhooks already run with `@Async`.

So P03 adds **two new workflows** and **proves** the concurrency with tests.

## 0. First: Bank-Grade PDF Statements (before P03 features)

Today's statement is a landscape table with a short summary. A real bank statement has these, which we add:

| Add | Detail |
| --- | --- |
| **Header on every page** | logo, "Almotawaj Digital Wallet", address, statement title, generated date |
| **Account box** | holder name, IBAN, account type, currency (BHD), statement period, statement number |
| **Balance summary** | opening balance → total credits → total debits → closing balance |
| **Proper columns** | Date · Description · Reference · Debit · Credit · Balance (debit and credit in separate columns) |
| **Opening / closing rows** | "Balance brought forward" first row, "Closing balance" last row |
| **Readable table** | portrait A4, zebra rows, right-aligned amounts, 3 decimals (BHD), repeated header row |
| **All transactions** | no "only first N shown": the statement covers the whole period across pages |
| **Footer on every page** | "Page X of Y", disclaimer ("computer-generated, no signature required"), simulator notice |
| **Receipts** | same header and footer style so receipt and statement match |
| **Arabic** | keep the existing right-to-left support for every new part |

**Data change:** `StatementData` gains `openingBalance` and `closingBalance` (the balance before the first and after the last transaction in the period), plus a statement number.

**Test:** generate a statement with 0, 1 and 200 transactions, in English and Arabic, and check the totals: opening + credits − debits = closing.

## 1. New features

### A. Merchant Vouchers (limited stock) – the main race condition

A merchant publishes a voucher, e.g. *"50 BHD gift card for 45 BHD, only 100 available"*. Clients buy it with their wallet balance.

**The race:** 1 voucher left, 2 clients press *Buy* at the same moment.

| Without control | With control |
| --- | --- |
| Both read `remaining = 1`, both buy, stock becomes `-1`, merchant paid twice for one item | One gets `201 Created`, the other `409 VOUCHER_SOLD_OUT` |

**Fix (database first):** one atomic update, which needs no Java lock:

```sql
UPDATE voucher SET remaining = remaining - 1
WHERE id = :id AND remaining > 0
```

If 0 rows change, the voucher is sold out, so we return 409. The buyer's wallet uses the existing wallet lock in the same transaction. A unique constraint stops the same client from buying more than the per-client limit.

**Why this approach:** the database is the only shared place across all Cloud Run instances. A `synchronized` block only protects one JVM.

### B. Bulk Payouts (merchant pays many people at once) – thread pool

A merchant uploads a list (for example 50 recipients and amounts). Every item debits the **same** merchant wallet.

- `ExecutorService` (fixed pool of 5) processes items in parallel. Each item is a `Callable<PayoutResult>`, and we collect the `Future`s.
- **The race:** 5 threads debit one wallet at once. The wallet lock means the balance can never go below zero; items that don't fit are marked `FAILED: INSUFFICIENT_BALANCE`.
- Runs in the **background**: the API returns `202 Accepted` right away, and progress arrives over SSE.
- Live counters use `AtomicInteger` (succeeded and failed).

### C. Idempotency keys – double-click protection

`POST /transfers` and `POST /vouchers/{id}/purchase` accept an `Idempotency-Key` header. The same key sent twice (double click, retry) gives back the first result instead of charging twice. A unique DB constraint enforces it.

## 2. New entities (5, for 16 in total)

| Entity | Purpose |
| --- | --- |
| `Voucher` | merchant offer: price, face value, `remaining`, `perClientLimit`, `@Version` |
| `VoucherPurchase` | who bought which voucher, the code, and the transaction |
| `PayoutBatch` | one bulk payout: status, totals, created by |
| `PayoutItem` | one recipient line: amount, status, failure reason |
| `IdempotencyRecord` | key + user + stored response (unique `key, user`) |

## 3. New API

| Method | Endpoint | Result |
| --- | --- | --- |
| `POST` | `/api/merchant/vouchers` | create voucher (merchant) |
| `GET` | `/api/vouchers` | list available vouchers |
| `POST` | `/api/vouchers/{id}/purchase` | `201` / `409 VOUCHER_SOLD_OUT` / `422 INSUFFICIENT_BALANCE` |
| `GET` | `/api/vouchers/purchases` | my vouchers |
| `POST` | `/api/merchant/payouts` | `202 Accepted`, starts the batch |
| `GET` | `/api/merchant/payouts/{id}` | batch progress and item results |

**New SSE events:** `VOUCHER_PURCHASED`, `VOUCHER_SOLD_OUT`, `PAYOUT_PROGRESS`, `PAYOUT_COMPLETED`.

## 4. Concurrency concepts covered

| Concept | Where |
| --- | --- |
| `ExecutorService`, thread pool, `Callable`, `Future` | bulk payout items |
| Atomic DB update | voucher stock |
| Pessimistic lock | wallet balance (existing, reused) |
| Optimistic lock (`@Version`) | merchant editing a voucher while it sells |
| `AtomicInteger` | payout progress counters |
| `ConcurrentHashMap` | SSE subscribers (existing) |
| `@Async` | payout batch, emails |
| Unique constraints | idempotency, per-client voucher limit |
| Bonus: `CompletableFuture` | combine the payout results, then send the summary email |

## 5. Tests (the proof)

- **Race test:** stock = 1, 20 threads buy at once, then assert exactly 1 success, 19 conflicts, stock = 0 and the balances add up.
- **Payout test:** a merchant with 100 BHD pays 10 × 15 BHD in parallel, so exactly 6 succeed, 4 fail, and the balance never goes below 0.
- **Idempotency test:** the same key sent 10 times in parallel produces one transaction.
- **Run each race test 50 times** (`@RepeatedTest`) to catch flaky timing.
- **Without-lock demo test:** a plain read-then-write version shows overselling, used to explain the bug.
- Unit tests for the new business rules (limits, expired voucher, inactive merchant).

## 6. Order of work (3 days max)

1. **PDF statements**: header and footer, balance summary, debit and credit columns, all pages, receipts.
2. **Data model**: the 5 entities, ERD, seed data (vouchers + a sample payout).
3. **Vouchers**: create, list, purchase; reproduce the race in a test, then fix it with the atomic update + wallet lock.
4. **Idempotency keys** on transfers and voucher purchase.
5. **Bulk payouts**: `ExecutorService`, `Callable`/`Future`, `@Async`, SSE progress.
6. **Concurrency tests + logging.**
7. **Docs**: Swagger, README *Project 3 Changes*, user stories, planning, credits.
8. **Frontend pages** (bonus), deploy, demo script.

Steps 1–3 are the core. If time runs short, steps 4 and 8 are the ones to cut.

## 7. User stories (new)

- As a merchant, I want to sell a limited number of vouchers so that I can run promotions.
- As a client, I want to buy a voucher with my wallet so that I get it instantly.
- **As a client, I want the system to stop two people from buying the last voucher, so that nobody pays for something that doesn't exist.**
- As a merchant, I want to pay many people in one upload so that payroll is fast.
- **As a merchant, I want my balance never to go negative when many payouts run at the same time.**
- As a user, I want a double click to charge me only once.

## 8. Docs to update

README (*Project 3 Changes, Concurrency Explanation, Testing, Updated API, Challenges, Future Improvements, Credits*), ERD, user stories, planning, Swagger, and seed data.

## 9. Logging rules

Log start, end, duration, ids (voucher, batch, item, wallet), result and conflict reason. **Never** log passwords, tokens, codes or API keys.
