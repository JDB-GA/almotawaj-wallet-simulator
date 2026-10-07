# API reference

[← Back to the README](../README.md)

Interactive documentation with request/response schemas, status codes and examples: **`/swagger-ui.html`**. To try protected endpoints, call `POST /auth/users/login` first; the browser keeps the session cookie.

Base URLs: `http://localhost:8080` in development and `https://api.almotawaj.com` in production.

Errors use [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) with a machine-readable `code`:

```json
{
  "detail": "This verification request has already been reviewed",
  "instance": "/api/admin/kyc/4021630a-51e9-447e-afd6-355d60ae9339/approve",
  "status": 422,
  "title": "Unprocessable Content",
  "code": "KYC_ALREADY_REVIEWED"
}
```

Lists accept `page`, `size` (max 100) and `sort` (e.g. `sort=createdAt,desc`) and return `content`, `page`, `size`, `totalElements`, `totalPages`.

| Method | Endpoint                                                | Functionality                    | Access        |
| ------ | ------------------------------------------------------- | -------------------------------- | ------------- |
| POST   | `/auth/users/register`                                  | Register a client account        | Public        |
| POST   | `/auth/users/login`                                     | Log in                           | Public        |
| POST   | `/auth/users/logout`                                    | Log out                          | Public        |
| GET    | `/auth/users/me`                                        | Get the current user             | Signed in     |
| POST   | `/auth/users/verify-email`                              | Verify email                     | Signed in     |
| POST   | `/auth/users/verify-email/resend`                       | Resend the verification code     | Signed in     |
| POST   | `/auth/users/password/forgot`                           | Request a password reset code    | Public        |
| POST   | `/auth/users/password/reset`                            | Reset the password               | Public        |
| POST   | `/auth/users/password/change`                           | Change the password              | Signed in     |
| GET    | `/api/profile`                                          | Get my profile                   | Client, merchant |
| PATCH  | `/api/profile`                                          | Set my business name             | Merchant      |
| GET    | `/api/profile/picture`                                  | Get my profile picture           | Client, merchant |
| PUT    | `/api/profile/picture`                                  | Upload or replace my picture     | Client, merchant |
| DELETE | `/api/profile/picture`                                  | Remove my picture                | Client, merchant |
| POST   | `/api/kyc`                                              | Submit an application            | Client        |
| GET    | `/api/kyc/me`                                           | Get my latest application        | Client        |
| GET    | `/api/kyc/me/documents/{documentId}`                    | Download one of my documents     | Client        |
| GET    | `/api/admin/kyc`                                        | List applications                | `KYC_REVIEW`  |
| GET    | `/api/admin/kyc/{applicationId}`                        | Get an application               | `KYC_REVIEW`  |
| POST   | `/api/admin/kyc/{applicationId}/approve`                | Approve an application           | `KYC_REVIEW`  |
| GET    | `/api/admin/kyc/{applicationId}/documents/{documentId}` | Download an application document | `KYC_REVIEW`  |
| POST   | `/api/admin/kyc/{applicationId}/reject`                 | Reject an application            | `KYC_REVIEW`  |
| GET    | `/api/admin/users`                                      | Search users                     | `USER_MANAGE` |
| POST   | `/api/admin/users`                                      | Create a user                    | `USER_MANAGE` |
| GET    | `/api/admin/users/{userId}`                             | Get a user                       | `USER_MANAGE` |
| PATCH  | `/api/admin/users/{userId}`                             | Update contact details           | `USER_MANAGE` |
| DELETE | `/api/admin/users/{userId}`                             | Close a user (soft delete)       | `USER_MANAGE` |
| POST   | `/api/admin/users/{userId}/reactivate`                  | Reactivate a user                | `USER_MANAGE` |
| POST   | `/api/admin/users/{userId}/suspend`                     | Suspend a user                   | `USER_MANAGE` |
| PUT    | `/api/admin/users/{userId}/permissions/{permission}`    | Grant a permission               | Super admin   |
| DELETE | `/api/admin/users/{userId}/permissions/{permission}`    | Revoke a permission              | Super admin   |
| GET    | `/api/admin/audit-logs`                                 | List audit entries               | Super admin   |
| GET    | `/api/wallet`                                           | Get my wallet                    | Wallet holder |
| GET    | `/api/wallet/transactions`                              | Search and filter my transactions | Wallet holder |
| GET    | `/api/wallet/transactions/{transactionId}/receipt`      | Download a receipt (PDF)         | Wallet holder |
| GET    | `/api/wallet/transactions/statement`                    | Download a statement (PDF)       | Wallet holder |
| GET    | `/api/wallet/top-ups/options`                           | Get top-up sources and limits    | Wallet holder |
| POST   | `/api/wallet/top-ups`                                   | Receive a bank transfer (simulated) | Wallet holder |
| GET    | `/api/wallet/recipients/suggestions`                    | Suggest recipients               | Wallet holder |
| GET    | `/api/wallet/recipients`                                | Find a recipient                 | Wallet holder |
| GET    | `/api/wallet/transfers/options`                         | Get transfer limits              | Wallet holder |
| POST   | `/api/wallet/transfers`                                 | Send money                       | Wallet holder |
| GET    | `/api/wallet/requests`                                  | List my payment requests         | Wallet holder |
| POST   | `/api/wallet/requests`                                  | Request money                    | Wallet holder |
| POST   | `/api/wallet/requests/{requestId}/pay`                  | Pay a request sent to me         | Wallet holder |
| POST   | `/api/wallet/requests/{requestId}/decline`              | Decline a request sent to me     | Wallet holder |
| POST   | `/api/wallet/requests/{requestId}/cancel`               | Cancel a request I sent          | Wallet holder |
| GET    | `/api/checkout/{sessionId}`                             | Get a checkout to pay            | Client        |
| POST   | `/api/checkout/{sessionId}/pay`                         | Pay a checkout from my wallet    | Client (KYC approved) |
| GET    | `/api/merchant/api-keys`                                | List my API keys                 | Merchant      |
| POST   | `/api/merchant/api-keys`                                | Create an API key (shown once)   | Merchant      |
| DELETE | `/api/merchant/api-keys/{keyId}`                        | Revoke an API key                | Merchant      |
| GET    | `/api/merchant/webhook`                                 | Get my callback URL and secret   | Merchant      |
| PUT    | `/api/merchant/webhook`                                 | Set my callback URL              | Merchant      |
| DELETE | `/api/merchant/webhook`                                 | Remove my callback URL           | Merchant      |
| GET    | `/api/merchant/checkout-sessions`                       | List my payments                 | Merchant      |
| POST   | `/api/merchant/checkout-sessions`                       | Create a payment link            | Merchant      |
| POST   | `/api/merchant/checkout-sessions/{sessionId}/cancel`    | Cancel a pending payment         | Merchant      |
| POST   | `/api/merchant/checkout-sessions/{sessionId}/refund`    | Refund a paid payment            | Merchant      |
| POST   | `/api/gateway/checkout-sessions`                        | Create a checkout session        | API key       |
| GET    | `/api/gateway/checkout-sessions/{sessionId}`            | Get a checkout session           | API key       |
| POST   | `/api/gateway/checkout-sessions/{sessionId}/cancel`     | Cancel a pending session         | API key       |
| POST   | `/api/gateway/checkout-sessions/{sessionId}/refund`     | Refund a paid session            | API key       |
| GET    | `/api/notifications/stream`                             | Live notifications (SSE)         | Wallet holder |
| GET    | `/api/admin/users/{userId}/transactions`                | Search a user's transactions     | `USER_MANAGE` |
| GET    | `/api/admin/statistics/users`                           | Count users per role             | `STATISTICS_VIEW` |
| GET    | `/api/admin/statistics/transactions`                    | Count and total transactions     | `STATISTICS_VIEW` |
| GET    | `/api/admin/transactions`                               | Search every wallet's transactions | `STATISTICS_VIEW` |
| DELETE | `/api/admin/seed-data`                                  | Remove demo data                 | Super admin   |
| POST   | `/api/seed`                                             | Seed demo data                   | Seed token    |

"Signed in" endpoints work before email verification; every other non-public endpoint also requires a verified email. "Wallet holder" means a merchant or a KYC-approved client. "API key" endpoints are called by a merchant's server with the `X-API-Key` header instead of a session cookie.

## Rate limiting

Sensitive endpoints allow 10 requests per minute for each client address and endpoint. A blocked request returns `429` with a `Retry-After` header and the code `TOO_MANY_REQUESTS`.

| Limited | Endpoints |
| --- | --- |
| Every request | register, login, verify email and resend, forgot/reset/change password, top-ups, transfers, recipient lookup and suggestions, PDF receipts and statements, seeding and demo data clean-up |
| Writes only | payment requests (create, pay, decline, cancel), API keys, callback settings, merchant payments, checkout payments and profile changes |
| 120 per minute per merchant | every gateway call made with an API key |

## Payment gateway

Merchants read the same guide inside the app under **Developer Guide** (`/merchant/developers`), in English and Arabic, with copyable cURL and Node.js examples that use the real API address.

A merchant accepts wallet payments in four steps:

1. Create an API key under **API keys** in the merchant dashboard (or `POST /api/merchant/api-keys`). The full key is returned once; only its SHA-256 hash is stored.
2. The merchant's server creates a checkout session for an order:

```bash
curl -X POST https://api.almotawaj.com/api/gateway/checkout-sessions \
  -H "X-API-Key: almt_..." \
  -H "Content-Type: application/json" \
  -d '{"orderReference":"ORDER-1042","amount":12.500,"description":"2 x Arabic coffee beans","returnUrl":"https://shop.example.com/orders/1042/complete"}'
```

3. The response contains `checkoutUrl`. The merchant sends the customer there; the customer signs in and pays from their wallet. After paying, the customer is sent to `returnUrl` with `sessionId` and `orderReference` added to it.
4. The merchant confirms the result before fulfilling the order, either with `GET /api/gateway/checkout-sessions/{sessionId}` or from the signed callback below.

### Callbacks

A merchant sets one callback URL (`PUT /api/merchant/webhook`, or the API keys page). When a session becomes `PAID`, `CANCELLED`, `EXPIRED` or `REFUNDED` the API sends a JSON `POST` to it:

```json
{"event":"checkout.paid","sessionId":"…","orderReference":"ORDER-1042","amount":12.500,"status":"PAID","occurredAt":"2026-10-06T16:45:31Z"}
```

- `X-Wallet-Event` carries the event name and `X-Wallet-Signature` the hex HMAC-SHA256 of the exact body, keyed with the merchant's signing secret.
- A callback is tried up to 3 times until the merchant answers with a 2xx status within 5 seconds.
- The URL must be a public https address. Private, loopback and link-local addresses are refused both when the URL is saved and before each call, and redirects are not followed. `webhook-allow-private-hosts=true` lifts this for local development only.

| Status | Meaning | Next |
| --- | --- | --- |
| `PENDING` | Waiting for the customer, for 30 minutes | `PAID`, `CANCELLED`, `EXPIRED` |
| `PAID` | Money moved from the customer to the merchant | `REFUNDED` |
| `CANCELLED` | Cancelled by the merchant | – |
| `EXPIRED` | Not paid in time | – |
| `REFUNDED` | The full amount moved back to the customer | – |

A session may set `expiresInMinutes` (1 to 30, default 30). A runnable sample store is in [`examples/demo-shop`](../examples/demo-shop/README.md).

An API key only opens `/api/gateway/**`; it cannot read the wallet or any other endpoint. Without a valid key the gateway answers `401`.

## PDF documents

Receipts and statements are generated by the API with OpenPDF and an embedded DejaVu Sans font, so they look the same on every device. The `Accept-Language` header (`en` or `ar`) chooses the language; Arabic documents are laid out right-to-left. A statement accepts the same filters as the transaction list, shows the totals of everything that matches and lists at most the 500 newest transactions.

## Live notifications

`GET /api/notifications/stream` is a Server-Sent Events stream for the signed-in wallet holder. Events are sent only after the database transaction commits.

| Event | Sent to | Payload |
| --- | --- | --- |
| `money-received` | the receiver of a top-up or transfer | `amount`, `senderName`, `reference` |
| `payment-requested` | the payer of a new payment request | the payment request |
| `payment-request-updated` | both sides when a request is created, paid, declined or cancelled | the payment request |
