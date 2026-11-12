# Services: Phase 04

[← Back to the README](../../README.md)

Boundaries follow business responsibility. All services share one PostgreSQL database, `almotawaj`, with one schema, `public`. Each service owns its own tables; the only exception is wallet reading `users` read-only. Money moves inside one database transaction with row locks, so everything that moves money stays in one service.

```text
                 Frontend
                    |
              api-gateway :8080
       +------------+------------+
       |            |            |
 identity :8081  wallet :8084  audit :8083
       |            |
       +--- REST ---+   identity -> wallet: /internal/seed-data
                        wallet -> identity: /internal/sandbox-accounts

 Kafka: account-events -> wallet (creates the wallet)
        email-events   -> notification :8082
        audit-events   -> audit
```

## How they communicate

| Way | From -> To | Used for |
| --- | --- | --- |
| REST through the gateway (sync) | Frontend -> any service | Every user request |
| REST internal (sync) | identity -> wallet | `DELETE /internal/seed-data`: remove demo data |
| REST internal (sync) | wallet -> identity | `/internal/sandbox-accounts`: Concurrency Lab users |
| Kafka (async) | identity -> wallet | `account-events`: create the wallet |
| Kafka (async) | identity -> notification | `email-events`: send an email |
| Kafka (async) | identity, wallet -> audit | `audit-events`: save an audit entry |
| JWT | identity -> wallet, audit | Token signed by identity, checked locally |

- Internal REST calls need the `X-Internal-Token` header. The gateway never routes `/internal/**`.
- Events are sent only after the database save commits.
- Every consumer retries 3 times (2 s apart), then moves the message to `<topic>-dlt`.
- Duplicate events are ignored (audit: unique event id; notification: remembers sent event ids; wallet: creates a wallet only if missing).

### Example: JWT payload

```json
{
  "sub": "client@almotawaj.com",
  "uid": "3f2b8c1e-7a4d-4e2b-9c1a-5d6e7f8a9b0c",
  "authorities": ["ROLE_CLIENT"],
  "iat": 1762761600,
  "exp": 1762848000
}
```

`sub` is the email, `uid` the user id, `authorities` the roles, `iat` and `exp` issue and expiry times.

### Example: registration

1. Frontend -> gateway -> identity saves the user.
2. identity publishes `account-events` -> wallet creates the wallet.
3. identity publishes `email-events` -> notification sends the verification code.
4. identity publishes `audit-events` -> audit saves the entry.

identity does not wait for any of them.

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
| Communication | Consumes `email-events` and sends through Resend (async). Failed sends retry 3 times, then go to `email-events-dlt`. A failed send throws, so it is retried instead of lost |

## api-gateway (8080)

| | |
| --- | --- |
| Responsibility | One entry point; routes each path to its service and handles CORS for the frontend (`CORS_ALLOWED_ORIGINS`) |
| APIs | Everything above, except `/internal/**`, which is never routed |
| Data | None |
| Communication | Forwards HTTP requests (sync) |
