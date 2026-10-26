# Planning – P03

**Goal:** when many people touch the same money or stock at once, the result is always correct.

Already in P02: 11 entities, wallet locking on transfers, `@Async` emails, SSE notifications.

## Work and progress

| #   | Task                                                                                                           | Done when                                           | Status  |
| --- | -------------------------------------------------------------------------------------------------------------- | --------------------------------------------------- | ------- |
| 1   | **Bank-style PDFs**: letterhead, page X of Y, opening/closing balance, debit and credit columns, IBM Plex font | statement looks like a bank's, EN + AR              | ✅ Done |
| 2   | **5 new entities**: `Voucher`, `VoucherPurchase`, `PayoutBatch`, `PayoutItem`, `IdempotencyRecord` (16 total)  | ERD + seed data updated                             | ✅ Done |
| 3   | **Vouchers**: merchant sells limited stock, clients buy with wallet balance                                    | last voucher: one buyer gets `201`, the other `409` | ✅ Done |
| 4   | **Bulk payouts**: merchant pays many people at once, in the background                                         | balance never goes below zero                       | ✅ Done |
| 5   | **Idempotency key** on voucher purchases                                                                       | a double click charges once                         | ✅ Done |
| 6   | **Tests + logging**                                                                                            | race tests pass 50 runs in a row                    | ✅ Done |
| 7   | **Frontend pages** + live updates                                                                              | shop, merchant vouchers, bulk payouts               | ✅ Done |
| 8   | **Concurrency Lab** (simulation page)                                                                          | safe vs unsafe race visible on a thread timeline    | ✅ Done |
| 9   | **Docs**: README P03 section, Swagger, user stories, diagrams                                                  | all requirements ticked                             | ✅ Done |
| 10  | **Use vouchers at checkout** + **expiry date**                                                                 | one voucher, two tabs: one payment succeeds         | ✅ Done |
| 11  | **Java concurrency tools**: `ReentrantLock` per voucher, `CompletableFuture` payouts, own `@Async` pool        | 208 tests pass                                      | ✅ Done |

## The race and the fix

| Problem                                 | Fix                                                          | Why                                               |
| --------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------- |
| 2 buyers, 1 voucher left                | `UPDATE … SET remaining = remaining - 1 WHERE remaining > 0` | atomic in the database, works across servers      |
| 5 threads debit one wallet              | existing wallet lock (`PESSIMISTIC_WRITE`)                   | balance can't go negative                         |
| Same request sent twice                 | unique key in the database                                   | second request returns the first result           |
| Merchant edits a voucher while it sells | `@Version` (optimistic lock)                                 | stale edit is rejected                            |
| One voucher used in two tabs            | `UPDATE … SET status = 'REDEEMED' WHERE status = 'UNUSED'`   | only one payment can use it                       |
| Buyers of one voucher on one server     | `ReentrantLock` per voucher in front of the update           | queues them in Java; the database stays the guard |

## Concurrency tools used

`ExecutorService` (pool of 5) · `CompletableFuture` · `ReentrantLock` · `CountDownLatch` · `AtomicInteger` · `ConcurrentHashMap` · `@Async` · transactions · pessimistic and optimistic locking

## New endpoints

`GET/POST /api/merchant/vouchers` · `PUT /api/merchant/vouchers/{id}` · `GET /api/vouchers` · `POST /api/vouchers/{id}/purchase` · `GET /api/vouchers/purchases` · `GET/POST /api/merchant/payouts` (`202`) · `GET /api/merchant/payouts/{id}` · `POST /api/admin/simulations/{voucher-race,payout-race}`

SSE events: `voucher-stock-changed` · `voucher-sold` · `payout-progress` · `payout-completed`

## Key tests

- 20 threads buy 1 voucher: exactly 1 succeeds, stock = 0.
- 100 BHD, 10 payouts of 15 BHD in parallel: 6 succeed, 4 fail.
- Same idempotency key 10 times: one transaction.
- 20 real parallel HTTP requests for the last voucher: one `201`, nineteen `409`.
- Unsafe read-then-write (Concurrency Lab): oversells, proving the race is real.

## Key user story

As a client, I want the system to stop two people buying the last voucher, so nobody pays for something that doesn't exist.
