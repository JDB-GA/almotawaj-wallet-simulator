# Step 11: Verify End to End

[← Back to AWS](index.md)

**Goal:** one real user journey through every AWS part at once.

Base URL: `http://almotawaj-prod.eba-jpqmjfvd.eu-north-1.elasticbeanstalk.com` (tested with Postman).

## 1. Seed demo data

Token: Parameter Store → `/almotawaj/SEED_TOKEN` → Show decrypted value.

| Request | Header | Result | Proves |
| --- | --- | --- | --- |
| `POST /api/seed` | `Authorization: Bearer <SEED_TOKEN>` | ✅ 201 | identity writes users to RDS |
| `POST /api/seed/wallet` (about 10 s later) | same | ✅ 201 | wallets exist, so `account-events` went through Kafka on EC2 to wallet |

Seed status codes: 403 wrong token (check `Bearer ` and the space), 404 token empty on the server, 422 already seeded or wallets not ready yet.

## 2. Register a real user

```text
POST /auth/users/register
```

```json
{
  "email": "<real inbox>",
  "mobileNumber": "<8 digits, not a demo number>",
  "password": "<at least 8 characters>"
}
```

| Check | Result | Proves |
| --- | --- | --- |
| Register request | ✅ Success | gateway → identity → RDS |
| Verification email | ✅ Received | `email-events` → Kafka → notification → Resend |
| Email verified with the code | ✅ | OTP flow on AWS |

## 3. Services answer through the gateway

| Path | Result |
| --- | --- |
| `/actuator/health` | ✅ 200 `UP` |
| `/api/vouchers` (wallet) | ✅ 401 |
| `/api/profile` (identity) | ✅ 401 |
| `/api/admin/audit-logs` (audit) | ✅ 401 |

## Known limit

The login cookie is `Secure`, so browsers send it only over HTTPS. The Beanstalk URL is HTTP, so staying logged in from a browser needs [Step 14](index.md) (HTTPS).
