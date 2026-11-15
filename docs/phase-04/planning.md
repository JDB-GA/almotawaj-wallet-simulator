# Planning: Phase 04

[← Back to the README](../../README.md)

**Goal:** the same wallet, split into services, containerized, event-driven and deployed to AWS through a pipeline.

## Before Day 1

- [X] Docker Desktop installed
- [X] AWS account with a billing alert
- [X] AWS CLI configured locally
- [X] Branch `feature-microservices`

## Services

| Service              | Owns                                                                           | Port |
|----------------------|--------------------------------------------------------------------------------|------|
| identity-service     | auth, user, kyc                                                                | 8081 |
| wallet-service       | wallet, paymentrequest, checkout, voucher, payout, statistics, simulation, SSE | 8084 |
| notification-service | email                                                                          | 8082 |
| audit-service        | audit log                                                                      | 8083 |
| api-gateway          | routes every request to the right service                                      | 8080 |

## Day 1: Identity split (Req 1, 2, 3)

- [x] Create service skeletons: identity, wallet, notification, audit
- [x] Document each service: [`services.md`](services.md)
- [x] Move auth, user, kyc into identity-service
- [x] Identity issues a JWT with `uid` and `authorities`
- [x] audit-service trusts the JWT without calling identity
- **Done when:** register, login and KYC work from identity-service ✅

## Day 2: Wallet decoupling (Req 3, 4, 7)

- [x] Wallet maps `users` read-only instead of owning a `User` entity; no user data is copied
- [x] Wallet trusts the JWT instead of reading the user table
- [x] One database `almotawaj` with one schema; each service writes only its own tables
- [x] Secrets and URLs moved to environment variables, `.env.example` added
- [x] Every client and merchant gets a wallet at registration (`account-events`); clients use it only after KYC
- [x] API Gateway routes every request to the right service (`/internal/**` stays private)
- [x] Unused code removed after the split
- **Done when:** a transfer and a voucher purchase work across both services ✅

## Day 3: Docker and Kafka (Req 5, 6, 8, 9, 10, 11)

- [x] Dockerfile per service (multi-stage build)
- [x] `docker-compose.yml`: services, Postgres, Kafka
- [x] Services talk by Docker service name, not localhost
- [x] Topics and event models: `account-events`, `email-events`, `audit-events`
- [x] Identity and wallet publish; wallet, notification and audit consume
- [x] Registration: wallet, OTP email and audit entry through events instead of direct calls
- [x] Retries (3 x 2 s), dead-letter topic (`<topic>-dlt`), duplicate-safe consumers
- **Done when:** `docker compose up` runs all and events flow end to end ✅

## Day 4: CI/CD and tests (Req 12, 13, 14, 22, 23)

- [x] Jenkins: build, test, build images per service ([`jenkins.md`](../deployment/jenkins.md))
- [x] Push images to ECR with version tags
- [x] Pipeline fails on a failing test
- [x] Previous tests still pass
- [x] New tests: Kafka events, health endpoints, service APIs
- [x] Cucumber feature files for key flows (send money, payment request, bulk payout)
- **Done when:** a push runs the full pipeline ✅

## Day 5: AWS (Req 15, 16, 17, 20, 21)

Step-by-step notes: [`deployment/aws`](../deployment/aws/index.md).

- [x] Deploy services to AWS (Elastic Beanstalk)
- [x] Database on RDS, private
- [x] IAM roles, security groups, secrets (Parameter Store)
- [x] Kafka on EC2, private (only the services can reach it)
- [x] Logs in CloudWatch: app, container, Kafka activity
- [x] Pipeline deploys automatically
- [x] End-to-end check on AWS: seed, register, email, verify
- [ ] Upgrade the AWS account to the Paid plan (removes the free-plan limits; credits stay)
- [ ] Certificate in N. Virginia and custom domain `wallet.almotawaj.com` on CloudFront (DNS in Vercel)
- [ ] `CORS_ALLOWED_ORIGINS` and `APP_URL` set to the custom domain; frontend rebuilt and uploaded
- [ ] Jenkins deploys the frontend: build, upload to S3, refresh CloudFront
- [ ] KYC uploads to S3 instead of the container disk
- [ ] Kafka TLS and per-service topic permissions; `email-events` kept 1 hour only
- [ ] 2 wallet instances, voucher race still correct
- **Done when:** the app works end to end on the custom domain

## Day 6: Docs and cleanup (Req 18, 19, 24 + deliverables)

- [ ] Architecture diagram
- [ ] Deployment guide: build, run, Kafka, AWS, verify
- [ ] Remove dead code, debug logs, hard-coded values
- [ ] Architecture doc: services, communication, data, infrastructure, CI/CD
- [ ] API docs updated, Kafka events documented separately
- [ ] README: overview, architecture, services, Kafka, Docker, CI/CD, AWS, env vars, testing, challenges, unsolved,
  future
- [ ] ERD, credits, AWS screenshots
- [ ] Presentation
- **Done when:** every requirement ticked

## Optional bonuses (after Day 6)

- [x] API Gateway: one entry point for the frontend (`services/api-gateway`, port 8080)
- [ ] Service discovery: services find each other by name
- [ ] Advanced Kafka: multiple topics, consumer groups, dead-letter processing
- [ ] Advanced CI/CD: image scanning, versioned releases, rollback
- [ ] AWS observability: CloudWatch alarms and dashboards
- [ ] Rolling deployment: release without downtime

## Day 7: RAG chatbot (extra)

- [ ] Assistant that answers questions about the wallet from its docs, using Google AI File Search
- [ ] Upload the docs to a File Search store
- [ ] Chat endpoint in the backend; API key in Parameter Store
- [ ] Chat window in the frontend
- **Done when:** the chatbot answers wallet questions with sources from the docs

## Every day

- Commit small and often
- Tick tasks here before moving on
