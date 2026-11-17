# Services

[← Back to the README](../../README.md)

## Rules

- Each service owns one business area.
- All services share one database (`almotawaj`, schema `public`).
- Each service writes only its own tables.
- Wallet reads `users` but never changes it, so user data is never copied.
- Everything that moves money stays in wallet, so one transaction and its row locks cover it.

## How they talk

| Type | From → To | Used for |
|------|-----------|----------|
| REST (sync) | Frontend → gateway → service | Every user request |
| Internal REST (sync) | identity → wallet | Delete demo data |
| Internal REST (sync) | wallet → identity | Concurrency Lab test users |
| Kafka (async) | identity → wallet | `account-events`: create the wallet |
| Kafka (async) | identity → notification | `email-events`: send an email |
| Kafka (async) | identity, wallet → audit | `audit-events`: save an audit entry |
| JWT | identity → wallet, audit, chatbot | Token signed once, checked by each service |
| HTTPS (sync) | chatbot → Gemini API | Answer questions, manage the docs |

- Internal REST needs the `X-Internal-Token` header.
- The gateway never routes `/internal/**`.
- Events are sent only after the database save succeeds.
- Failed events retry 3 times, then go to `<topic>-dlt`.
- Duplicate events are ignored.

## Example: registration

1. Frontend → gateway → identity saves the user.
2. identity sends `account-events` → wallet creates the wallet.
3. identity sends `email-events` → notification emails the code.
4. identity sends `audit-events` → audit saves the entry.

identity does not wait for steps 2 to 4.

## api-gateway (8080)

- **Does:** routes each path to its service; handles CORS.
- **APIs:** everything below, except `/internal/**`.
- **Data:** none.
- **Talks:** forwards HTTP (sync).

## identity-service (8081)

- **Does:** accounts, login, email verification, passwords, KYC, permissions.
- **APIs:** `/auth/**`, `/api/profile`, `/api/kyc/**`, `/api/admin/kyc/**`, `/api/admin/users/**`, `/api/admin/statistics/users`, `/api/seed`.
- **Internal API:** `/internal/sandbox-accounts` (Concurrency Lab users).
- **Data:** users, OTP codes, KYC applications; KYC files in S3.
- **Talks:** issues the JWT; sends all three event types (async); calls wallet to delete demo data (sync).

## wallet-service (8084)

- **Does:** wallets, transfers, payment requests, checkout, vouchers, payouts, PDFs, live notifications.
- **APIs:** `/api/wallet/**`, `/api/checkout/**`, `/api/merchant/**`, `/api/gateway/**`, `/api/vouchers/**`, `/api/notifications/**`, `/api/admin/transactions/**`, `/api/admin/statistics/transactions`, `/api/admin/simulations/**`, `/api/seed/wallet`.
- **Internal API:** `/internal/seed-data` (delete demo data).
- **Data:** wallets, transactions, payment requests, checkout sessions, vouchers, payouts.
- **Talks:** checks the JWT; reads `account-events`; sends `audit-events` (async); calls identity for test users (sync).

## notification-service (8082)

- **Does:** emails (verification code, password reset, invitation, KYC decision).
- **APIs:** none.
- **Data:** none.
- **Talks:** reads `email-events`; sends through Resend (async).

## audit-service (8083)

- **Does:** the audit log.
- **APIs:** `GET /api/admin/audit-logs` (super admin).
- **Data:** audit entries.
- **Talks:** checks the JWT; reads `audit-events` (async).

## chatbot-service (8085)

- **Does:** the RAG assistant; answers only from the uploaded docs, in the user's language.
- **APIs:** `POST /api/chat` (any signed-in user); `GET`, `POST`, `DELETE /api/admin/chat/documents` (super admin).
- **Data:** none; files and embeddings live in the Gemini File Search store.
- **Talks:** checks the JWT; calls the Gemini API (sync).

## JWT example

```json
{
  "sub": "client@almotawaj.com",
  "uid": "3f2b8c1e-7a4d-4e2b-9c1a-5d6e7f8a9b0c",
  "authorities": ["ROLE_CLIENT"],
  "iat": 1762761600,
  "exp": 1762848000
}
```

- `sub`: email
- `uid`: user id
- `authorities`: roles
- `iat`, `exp`: issued and expiry times
