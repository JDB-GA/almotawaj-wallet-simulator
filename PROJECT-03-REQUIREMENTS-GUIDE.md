# Project 03 Requirements Guide

Where each [Project 3 requirement](https://github.com/Java-FT-01-Bahrain/JDB-Info/tree/main/Projects/Project-03) is met.

| # | Requirement | File |
| - | - | - |
| 1 | Extend existing app | [Auth feature](backend/src/main/java/com/almotawaj/wallet/auth/), [Wallet feature](backend/src/main/java/com/almotawaj/wallet/wallet/) |
| 2 | 5+ new models | [Voucher models](backend/src/main/java/com/almotawaj/wallet/voucher/model/), [Payout models](backend/src/main/java/com/almotawaj/wallet/payout/model/), [`erd.dbml`](docs/erd.dbml) |
| 3 | New business workflow | [`VoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherPurchaseService.java), [`PayoutService.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutService.java) |
| 4 | Multithreading | [`PayoutRunner.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutRunner.java), [`p03-bulk-payout-flow.png`](docs/p03-bulk-payout-flow.png) |
| 5 | Concurrency scenario | [`IdempotentVoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/IdempotentVoucherPurchaseService.java), [`p03-idempotency-flow.png`](docs/p03-idempotency-flow.png) |
| 6 | Race condition docs | [`README.md`](README.md), [`p03-voucher-race-flow.png`](docs/p03-voucher-race-flow.png)<br>Solved in: [`IdempotentVoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/IdempotentVoucherPurchaseService.java) (ReentrantLock), [`Voucher.java`](backend/src/main/java/com/almotawaj/wallet/voucher/model/Voucher.java) (`@Version` optimistic lock), [`VoucherService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherService.java) (version check) |
| 7 | Thread safety | [`IdempotentVoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/IdempotentVoucherPurchaseService.java) (ReentrantLock) |
| 8 | Thread pool | [`PayoutExecutorConfig.java`](backend/src/main/java/com/almotawaj/wallet/config/app/PayoutExecutorConfig.java), [`AsyncConfig.java`](backend/src/main/java/com/almotawaj/wallet/config/app/AsyncConfig.java) |
| 9 | Concurrent HTTP | [`ParallelPurchaseHttpTest.java`](backend/src/test/java/com/almotawaj/wallet/voucher/controller/ParallelPurchaseHttpTest.java) |
| 10 | Database concurrency | Atomic update: [`VoucherRepository.java`](backend/src/main/java/com/almotawaj/wallet/voucher/repository/VoucherRepository.java) (`takeOne`)<br>Pessimistic: [`WalletLocker.java`](backend/src/main/java/com/almotawaj/wallet/wallet/service/WalletLocker.java), [`CheckoutSessionRepository.java`](backend/src/main/java/com/almotawaj/wallet/checkout/repository/CheckoutSessionRepository.java) (`@Lock`)<br>Optimistic: [`Voucher.java`](backend/src/main/java/com/almotawaj/wallet/voucher/model/Voucher.java) (`@Version`)<br>Deadlock: [`p03-wallet-deadlock-flow.png`](docs/p03-wallet-deadlock-flow.png) |
| 11 | Extend resource workflow | [`CheckoutPaymentService.java`](backend/src/main/java/com/almotawaj/wallet/checkout/service/CheckoutPaymentService.java) |
| 12 | Async processing | [`PayoutRunner.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutRunner.java) (`onBatchCreated`) |
| 13 | Real-time (SSE) | [`NotificationHub.java`](backend/src/main/java/com/almotawaj/wallet/notification/service/NotificationHub.java) |
| 14 | Updated APIs | [`api.md`](docs/api.md), [Voucher controllers](backend/src/main/java/com/almotawaj/wallet/voucher/controller/), [Payout controllers](backend/src/main/java/com/almotawaj/wallet/payout/controller/) |
| 15 | Swagger docs | [`VoucherController.java`](backend/src/main/java/com/almotawaj/wallet/voucher/controller/VoucherController.java), [`PayoutController.java`](backend/src/main/java/com/almotawaj/wallet/payout/controller/PayoutController.java) (`@Operation`, `@ApiResponse`) |
| 16 | Logging | Logs: [`VoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherPurchaseService.java), [`VoucherService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherService.java)<br>Audit log ([`AuditService.java`](backend/src/main/java/com/almotawaj/wallet/audit/service/AuditService.java)): [`VoucherPurchaseService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherPurchaseService.java), [`VoucherService.java`](backend/src/main/java/com/almotawaj/wallet/voucher/service/VoucherService.java), [`PayoutService.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutService.java), [`PayoutItemProcessor.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutItemProcessor.java), [`CheckoutPaymentService.java`](backend/src/main/java/com/almotawaj/wallet/checkout/service/CheckoutPaymentService.java) |
| 17 | Testing | [`ConcurrencyIntegrationTest.java`](backend/src/test/java/com/almotawaj/wallet/voucher/service/ConcurrencyIntegrationTest.java)<br>Idempotency key: [`VoucherAndPayoutControllerTest.java`](backend/src/test/java/com/almotawaj/wallet/voucher/controller/VoucherAndPayoutControllerTest.java) (same key twice → one purchase) |
| 18 | Refactoring | [`README.md`](backend/README.md) (Structure) |
| 19 | Git | Commit history and `feature-*` branches |
| 20 | User stories | [`USER-STORIES-P03.md`](docs/USER-STORIES-P03.md) |
| 21 | Planning | [`PLANNING-P03.md`](PLANNING-P03.md) |
| 22 | Conflict responses | [`GlobalExceptionHandler.java`](backend/src/main/java/com/almotawaj/wallet/exception/GlobalExceptionHandler.java) |
| 23 | README | [`README.md`](README.md), [`CREDITS.md`](CREDITS.md) |

## Bonus

| Bonus | File |
| - | - |
| 1. Advanced concurrency | [`PayoutItemProcessor.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutItemProcessor.java) |
| 2. CompletableFuture | [`PayoutRunner.java`](backend/src/main/java/com/almotawaj/wallet/payout/service/PayoutRunner.java) |
| 3. Thread pools | [`PayoutExecutorConfig.java`](backend/src/main/java/com/almotawaj/wallet/config/app/PayoutExecutorConfig.java) |
| 5. DB locking | Optimistic: [`Voucher.java`](backend/src/main/java/com/almotawaj/wallet/voucher/model/Voucher.java) (`@Version`)<br>Pessimistic: [`WalletLocker.java`](backend/src/main/java/com/almotawaj/wallet/wallet/service/WalletLocker.java), [`CheckoutSessionRepository.java`](backend/src/main/java/com/almotawaj/wallet/checkout/repository/CheckoutSessionRepository.java) (`@Lock`) |
| 6. Real-time | [`NotificationHub.java`](backend/src/main/java/com/almotawaj/wallet/notification/service/NotificationHub.java) |
| 7. Frontend | [Simulation frontend](frontend/src/features/simulation/) |
| 8. Third-party API | [`ResendConfig.java`](backend/src/main/java/com/almotawaj/wallet/config/app/ResendConfig.java) |
| 9. Advanced testing | [`ParallelPurchaseHttpTest.java`](backend/src/test/java/com/almotawaj/wallet/voucher/controller/ParallelPurchaseHttpTest.java) |
