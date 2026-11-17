# Almotawaj Digital Wallet: Project Overview

## What it is

- Almotawaj Digital Wallet is a bilingual (English and Arabic) digital wallet for Bahrain.
- It was built by Muntadher Almutawaj.
- Website: https://wallet.almotawaj.com
- Currency: Bahraini Dinar (BHD), with 3 decimal places (for example 5.250 BHD).
- Time zone for daily limits: Bahrain time.

## Who uses it

| Role | What they can do |
| --- | --- |
| Client | Register, verify email, submit KYC. After KYC approval: wallet, receive money, send and request money, buy vouchers, pay merchants. |
| Merchant | Wallet, receive money, send and request money, API keys, payment links, cancel and refund payments, sell vouchers, bulk payouts. |
| Admin | Only what their permissions allow. |
| Super admin | Everything, including granting permissions, the audit log, the Concurrency Lab and the assistant files. |

### Admin permissions

| Permission | Unlocks |
| --- | --- |
| `KYC_REVIEW` | KYC review list, documents, approve or reject |
| `USER_MANAGE` | Search, create and edit users, suspend, reactivate or close accounts, see a user's transactions |
| `STATISTICS_VIEW` | User counts per role, transaction totals and all wallet transactions |

- Admins with `USER_MANAGE` manage clients and merchants only.
- Only the super admin manages admins.
- Nobody can manage their own account or a super admin from user management.

## Main features

1. **Registration and login:** email or mobile number with a password; email verified with a one-time code.
2. **KYC (identity verification):** CPR details, address in Bahrain and identity documents; reviewed by an admin.
3. **Wallet with IBAN:** created automatically after registration.
4. **Top up:** simulated incoming bank transfer from demo accounts.
5. **Send money:** to an email, mobile number or IBAN, instantly.
6. **Payment requests:** ask someone to pay you; they pay or decline.
7. **Transactions:** search, filter, sort, receipt PDF and statement PDF.
8. **Vouchers:** merchants sell limited vouchers; clients buy them; stock can never oversell.
9. **Merchant payments:** payment links, an API with API keys, callbacks (webhooks), cancel and refund.
10. **Bulk payouts:** a merchant pays up to 100 people at once in the background.
11. **Live notifications:** pushed to the browser.
12. **Emails:** verification code, password reset, invitation and KYC decision.
13. **Audit log:** every important action is recorded.
14. **Concurrency Lab:** a safe demo that shows how race conditions are prevented.
15. **AI assistant (RAG chatbot):** answers questions from the documentation, in English or Arabic, with sources, streamed live.

## Business rules and limits

### One-time codes
- Codes expire after 10 minutes (invitations: 48 hours).
- 5 attempts are allowed.
- A code can be resent once every 60 seconds.

### KYC
- Applicants must be 18 or older.
- Both identity documents must be unexpired.
- Documents must be real PDFs, and photos real JPEG or PNG files, 5 MB maximum each.
- Only pending applications can be reviewed; a rejection needs a reason.
- Two reviewers cannot decide the same application at the same time.

### Money limits
- Each top-up and each transfer: from 0.100 to 5,000.000 BHD.
- Daily: at most 10,000.000 BHD received by top-up and 10,000.000 BHD sent by transfer.
- Merchant payments (checkout): from 0.100 to 5,000.000 BHD each, with a separate daily limit of 10,000.000 BHD.
- You can never send more than your balance.

### Payment requests
- Only the payer can pay or decline; only the requester can cancel.
- Only while the request is pending.
- Paying a request is a normal transfer, so the balance and daily limit apply.

### Merchant payments (checkout)
- Each order reference can be paid only once per merchant.
- A payment link works for 30 minutes; unpaid links are marked expired.
- Only KYC-approved clients can pay.
- The merchant API allows 120 calls per minute per merchant.

### Vouchers
- A voucher has a price, a face value, a quantity, a per-client limit, a sale end date and an optional expiry date.
- Only KYC-approved clients can buy, up to the per-client limit.
- Each purchase gets a unique code; the money goes to the merchant.
- If stock runs out, the buyer gets "sold out" and is not charged.

### Bulk payouts
- 1 to 100 recipients per batch, each 0.100 to 5,000.000 BHD.
- Runs in the background on 5 threads.
- The merchant's balance never goes below zero; items that do not fit are marked failed with a reason.
- The batch ends as completed or partially failed.

### Safety
- Money movements lock the wallet rows, so two requests at once can never overspend.
- Write actions are rate-limited per user.

## Architecture

The app runs as 6 microservices behind one API gateway.

| Service | Port | Does |
| --- | --- | --- |
| api-gateway | 8080 | Routes each request to its service; handles CORS |
| identity-service | 8081 | Accounts, login, email verification, passwords, KYC, permissions; issues the JWT |
| notification-service | 8082 | Sends emails through Resend |
| audit-service | 8083 | Saves the audit log |
| wallet-service | 8084 | Wallets, transfers, payment requests, checkout, vouchers, payouts, PDFs, live notifications |
| chatbot-service | 8085 | The RAG assistant |

- All services share one PostgreSQL database; each service writes only its own tables.
- The login token (JWT) is signed once by identity and checked by every service.
- Services talk with REST (sync) and Kafka events (async).

### Kafka events

| Topic | From | To | Result |
| --- | --- | --- | --- |
| `account-events` | identity | wallet | Wallet created |
| `email-events` | identity | notification | Email sent |
| `audit-events` | identity, wallet | audit | Audit entry saved |

- Events are sent only after the database save succeeds.
- A failed message is retried 3 times, then moved to a dead-letter topic.
- Duplicate events are ignored.

## Technologies

- **Backend:** Java 17, Spring Boot 4, Spring Security, Spring Cloud Gateway, Spring Kafka
- **Frontend:** React 19, TypeScript, Vite, Tailwind CSS, i18next (English and Arabic, right to left)
- **Data:** PostgreSQL, Apache Kafka, Amazon S3
- **DevOps:** Docker, Docker Compose, Jenkins
- **AWS:** Elastic Beanstalk, EC2, RDS, S3, CloudFront, ECR, IAM, Parameter Store, CloudWatch
- **AI:** Google Gemini API with File Search
- **Tests:** JUnit 5, Cucumber

## AWS deployment

- Region: `eu-north-1` (Stockholm).
- Users reach `wallet.almotawaj.com` through Cloudflare DNS and CloudFront.
- CloudFront serves the React app from S3 and sends `/api` and `/auth` to the gateway on Elastic Beanstalk.
- One EC2 instance on Beanstalk runs the services as Docker containers.
- The database is a private Amazon RDS PostgreSQL.
- Kafka runs privately on its own EC2.
- KYC files are stored in a private S3 bucket.
- Secrets are stored in Parameter Store; EC2 uses IAM roles, so no access keys are stored.
- Logs go to CloudWatch.

## Admin tools

- **Jenkins:** https://jenkins.almotawaj.com (login required)
- **pgAdmin:** https://pgadmin.almotawaj.com (login required)
- Both run on one EC2, each behind its own CloudFront.

## CI/CD

- Jenkins checks GitHub every 2 minutes and runs only what changed.
- Backend changed: run tests, build Docker images, push to Amazon ECR, deploy to Beanstalk.
- Frontend changed: build, upload to S3, refresh CloudFront.
- If any test fails, nothing is deployed.

## AI assistant (RAG chatbot)

- RAG means Retrieval-Augmented Generation: the assistant first finds relevant parts of the documentation, then writes an answer from them.
- A super admin uploads `.md` or `.txt` files (up to 5 MB) on the **Assistant files** page.
- Gemini splits each file into chunks and stores their embeddings in a File Search store.
- When a user asks, chatbot-service sends the question to Gemini (model `gemini-3.5-flash-lite`) with the file search tool.
- Gemini finds the matching chunks and answers from them only, with the file names as sources.
- The answer is streamed to the chat widget word by word.
- If the documents do not cover the question, the assistant says it does not know.
- It replies in the language of the question.
- Only signed-in users can use it.

## Testing

- 173 automated tests: identity 21, wallet 141, notification 3, audit 2, chatbot 6.
- Cucumber scenarios: send money, pay a payment request, bulk payout.
- Concurrency test: 20 buyers race for 1 voucher; exactly 1 wins, every time.

## Project phases

- **Phase 02:** core wallet: accounts, KYC, wallet, transfers, payment requests, merchant payments.
- **Phase 03:** vouchers, bulk payouts, concurrency safety and the Concurrency Lab.
- **Phase 04:** split into microservices with Kafka, Docker, Jenkins and AWS; added the RAG assistant.

## Challenges solved

1. Images built on a Mac (arm64) failed on AWS (amd64); fixed by building for amd64.
2. The free AWS plan blocked the instance size and certificate region; fixed by upgrading.
3. The database password had a character RDS rejects; fixed with a new safe password.
4. The domain did not allow Amazon certificates; fixed with a CAA record.
5. Users were logged out after login because the frontend called a different address; fixed by using one domain.
6. Finding where deadlocks and race conditions happen; fixed with row locks in a fixed order and atomic updates.

## Known limits and future work

- Kafka has no encryption or per-service permissions yet.
- All services share one server.
- Live notifications work on one server only.
- Future: secure Kafka, one container service per microservice (ECS) behind a load balancer, CloudWatch alarms and dashboards.
