# Services: Phase 04

[← Back to the README](../../README.md)

Boundaries follow business responsibility. All services share one PostgreSQL database and schema, `almotawaj`; each service owns its own tables and never reads another service's. Money moves inside one database transaction with row locks, so everything that moves money stays in one service.

```text
                 Frontend
                    |
              api-gateway :8080
       +------------+------------+
       |            |            |
 identity :8081  wallet :8084  audit :8083
       |            |
       +--- REST ---+   (/internal/sandbox-accounts, Concurrency Lab only)

 Kafka: account-events -> wallet (creates the wallet)
        email-events   -> notification :8082
        audit-events   -> audit
```

## identity-service (8081)

| | |
| --- | --- |
| Responsibility | Who the user is: accounts, login, email verification, passwords, KYC, permissions |
| APIs | `/auth/**`, `/api/profile`, `/api/kyc/**`, `/api/admin/kyc/**`, `/api/admin/users/**`, `/api/admin/statistics/users`, `/api/seed` |
| Internal API | `POST` and `DELETE /internal/sandbox-accounts`: throwaway users for the Concurrency Lab (needs `X-Internal-Token`) |
| Data | Own tables: users, OTP challenges, KYC applications and documents |
| Communication | Issues the JWT (`uid`, `authorities`) that every service trusts. Publishes `account-events` when a user is created, `email-events` and `audit-events` (async) |

## wallet-service (8084)

| | |
| --- | --- |
| Responsibility | Money: wallets, transfers, top-ups, payment requests, checkout, vouchers, payouts, PDFs, live notifications |
| APIs | `/api/wallet/**`, `/api/checkout/**`, `/api/merchant/**`, `/api/gateway/**`, `/api/vouchers/**`, `/api/notifications/**`, `/api/admin/transactions/**`, `/api/admin/statistics/transactions`, `/api/admin/simulations/**`, `/api/seed/wallet` |
| Internal API | `DELETE /internal/seed-data`: identity sends the demo user ids; wallet removes their data first (needs `X-Internal-Token`) |
| Data | Own tables: wallets, transactions, payment requests, checkout sessions, vouchers, payouts |
| Communication | Verifies the JWT locally. Reads `users` read-only (identity is the only writer), so user data is never copied. Consumes `account-events` to create the wallet at registration (async). Publishes `audit-events`. Calls identity for Concurrency Lab users (sync) |

## audit-service (8083)

| | |
| --- | --- |
| Responsibility | The audit log |
| APIs | `GET /api/admin/audit-logs` (super admin) |
| Data | Own tables: audit logs, actor stored as id and email |
| Communication | Verifies the JWT locally. Consumes `audit-events`; the event id is unique, so duplicates are ignored (async) |

## notification-service (8082)

| | |
| --- | --- |
| Responsibility | Emails: verification code, password reset, invitation, KYC decision |
| APIs | None |
| Data | None |
| Communication | Consumes `email-events` and sends through Resend (async). Failed sends retry 3 times, then go to `email-events-dlt` |

## api-gateway (8080)

| | |
| --- | --- |
| Responsibility | One entry point; routes each path to its service |
| APIs | Everything above, except `/internal/**`, which is never routed |
| Data | None |
| Communication | Forwards HTTP requests (sync) |
