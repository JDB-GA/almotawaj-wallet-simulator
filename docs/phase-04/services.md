# Services: Phase 04

[← Back to the README](../../README.md)

Boundaries follow business responsibility. Money moves inside one database transaction with row locks, so everything that moves money stays in one service.

```text
                 Frontend
                    |
              api-gateway :8080
       +------------+------------+
       |            |            |
 identity :8081  wallet :8084  audit :8083
       |            |
       +--- REST ---+   (/internal/accounts)

 notification :8082   (waits for Kafka, Day 3)
```

## identity-service (8081)

| | |
| --- | --- |
| Responsibility | Who the user is: accounts, login, email verification, passwords, KYC, permissions |
| APIs | `/auth/**`, `/api/profile`, `/api/kyc/**`, `/api/admin/kyc/**`, `/api/admin/users/**`, `/api/admin/statistics/users`, `/api/seed` |
| Internal API | `GET /internal/accounts/{id}`, `GET /internal/accounts?identifier=` (needs `X-Internal-Token`) |
| Data | `identity` database: users, OTP challenges, KYC applications and documents |
| Communication | Issues the JWT (`uid`, `authorities`) that every service trusts. Answers wallet's REST lookups (sync) |

## wallet-service (8084)

| | |
| --- | --- |
| Responsibility | Money: wallets, transfers, top-ups, payment requests, checkout, vouchers, payouts, PDFs, live notifications |
| APIs | `/api/wallet/**`, `/api/checkout/**`, `/api/merchant/**`, `/api/gateway/**`, `/api/vouchers/**`, `/api/notifications/**`, `/api/admin/transactions/**`, `/api/admin/statistics/transactions`, `/api/admin/simulations/**`, `/api/seed/wallet` |
| Data | `wallet` database: account holders (copy), wallets, transactions, payment requests, checkout sessions, vouchers, payouts |
| Communication | Verifies the JWT locally. Copies an account from identity by REST the first time it is needed (sync) and creates its wallet |

## audit-service (8083)

| | |
| --- | --- |
| Responsibility | The audit log |
| APIs | `GET /api/admin/audit-logs` (super admin) |
| Data | `audit` database: audit logs, actor stored as id and email |
| Communication | Verifies the JWT locally. Receives audit events through Kafka (async, Day 3) |

## notification-service (8082)

| | |
| --- | --- |
| Responsibility | Emails: verification code, password reset, invitation, KYC decision |
| APIs | None |
| Data | None |
| Communication | Consumes email events through Kafka (async, Day 3). Until then identity sends its own emails |

## api-gateway (8080)

| | |
| --- | --- |
| Responsibility | One entry point; routes each path to its service |
| APIs | Everything above, except `/internal/**`, which is never routed |
| Data | None |
| Communication | Forwards HTTP requests (sync) |
