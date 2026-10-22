# Planning – P03

**Goal:** when many people touch the same money or stock at once, the result is always correct.

Already in P02: 11 entities, wallet locking on transfers, `@Async` emails, SSE notifications.

## Work (in order, max 3 days)

| #   | Task                                                                                                           | Done when                                           |
| --- | -------------------------------------------------------------------------------------------------------------- | --------------------------------------------------- |
| 1   | **Bank-style PDFs**: letterhead, page X of Y, opening/closing balance, debit and credit columns, IBM Plex font | statement looks like a bank's, EN + AR              |
| 2   | **5 new entities**: `Voucher`, `VoucherPurchase`, `PayoutBatch`, `PayoutItem`, `IdempotencyRecord` (16 total)  | ERD + seed data updated                             |
| 3   | **Vouchers**: merchant sells limited stock, clients buy with wallet balance                                    | last voucher: one buyer gets `201`, the other `409` |
| 4   | **Bulk payouts**: merchant pays many people at once, in the background                                         | balance never goes below zero                       |
| 5   | **Idempotency key** on transfers and purchases                                                                 | a double click charges once                         |
| 6   | **Tests + logging**                                                                                            | race tests pass 50 runs in a row                    |
| 7   | **Docs**: README P03 section, Swagger, user stories, credits                                                   | all requirements ticked                             |

## The race and the fix

| Problem                                 | Fix                                                          | Why                                          |
| --------------------------------------- | ------------------------------------------------------------ | -------------------------------------------- |
| 2 buyers, 1 voucher left                | `UPDATE … SET remaining = remaining - 1 WHERE remaining > 0` | atomic in the database, works across servers |
| 5 threads debit one wallet              | existing wallet lock (`PESSIMISTIC_WRITE`)                   | balance can't go negative                    |
| Same request sent twice                 | unique key in the database                                   | second request returns the first result      |
| Merchant edits a voucher while it sells | `@Version` (optimistic lock)                                 | stale edit is rejected                       |

## Concurrency tools used

`ExecutorService` (pool of 5) · `Callable` + `Future` · `AtomicInteger` · `ConcurrentHashMap` · `@Async` · transactions · bonus: `CompletableFuture`

## New endpoints

`POST /api/merchant/vouchers` · `GET /api/vouchers` · `POST /api/vouchers/{id}/purchase` · `GET /api/vouchers/purchases` · `POST /api/merchant/payouts` (`202`) · `GET /api/merchant/payouts/{id}`

SSE events: `VOUCHER_PURCHASED` · `VOUCHER_SOLD_OUT` · `PAYOUT_PROGRESS` · `PAYOUT_COMPLETED`

## Key tests

- 20 threads buy 1 voucher: exactly 1 succeeds, stock = 0.
- 100 BHD, 10 payouts of 15 BHD in parallel: 6 succeed, 4 fail.
- Same idempotency key 10 times: one transaction.

## Key user story

As a client, I want the system to stop two people buying the last voucher, so nobody pays for something that doesn't exist.
