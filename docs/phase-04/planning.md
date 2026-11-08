# Planning: Phase 04

[← Back to the README](../../README.md)

**Goal:** the same wallet, split into services, containerized, event-driven and deployed to AWS through a pipeline.

## Before Day 1

- [X] Docker Desktop installed
- [X] AWS account with a billing alert
- [X] AWS CLI configured locally
- [X] Branch `feature-microservices`

## Services

| Service | Owns | Port |
| --- | --- | --- |
| identity-service | auth, user, kyc | 8081 |
| wallet-service | wallet, paymentrequest, checkout, voucher, payout, statistics, simulation, SSE | 8080 |
| notification-service | email | 8082 |
| audit-service | audit log | 8083 |
| api-gateway | routes every request to the right service | 8000 |

## Day 1: Identity split (Req 1, 2, 3)

- [x] Create service skeletons: identity, wallet, notification, audit
- [ ] Document each service: responsibility, APIs, data, communication
- [x] Move auth, user, kyc into identity-service
- [x] Identity issues a JWT with `uid` and `authorities`
- [x] audit-service trusts the JWT without calling identity
- **Done when:** register, login and KYC work from identity-service ✅

## Day 2: Wallet decoupling (Req 3, 4, 7)

- [x] Wallet keeps its own `account_holders` copy instead of the `User` entity
- [x] Wallet trusts the JWT instead of reading the user table
- [x] Wallet copies accounts from identity by REST when missing (`/internal/accounts`)
- [x] One database per service: `identity`, `wallet`, `audit`
- [x] Secrets and URLs moved to environment variables, `.env.example` added
- [x] Every client and merchant gets a wallet when their account is copied; clients use it only after KYC
- [x] API Gateway routes every request to the right service (`/internal/**` stays private)
- [x] Unused code removed after the split
- **Done when:** a transfer and a voucher purchase work across both services ✅

## Day 3: Docker and Kafka (Req 5, 6, 8, 9, 10, 11)

- [ ] Dockerfile per service (multi-stage build)
- [ ] `docker-compose.yml`: services, Postgres, Kafka
- [ ] Services talk by Docker service name, not localhost
- [ ] Topics and event models defined
- [ ] Identity and wallet publish; notification and audit consume
- [ ] Registration: wallet copy, OTP email and audit entry through events instead of direct calls
- [ ] Retries, dead-letter topic, duplicate-safe consumers
- **Done when:** `docker compose up` runs all and events flow end to end

## Day 4: CI/CD and tests (Req 12, 13, 14, 22, 23)

- [ ] GitHub Actions: build, test, build images per service
- [ ] Push images to ECR with version tags
- [ ] Pipeline fails on a failing test
- [ ] Previous tests still pass
- [ ] New tests: Kafka events, health endpoints, service APIs
- [ ] Cucumber feature files for key flows
- **Done when:** a push runs the full pipeline

## Day 5: AWS (Req 15, 16, 17, 20, 21)

- [ ] Deploy services to AWS
- [ ] Database on RDS, private
- [ ] IAM roles, security groups, Secrets Manager
- [ ] Logs in CloudWatch: app, container, Kafka activity
- [ ] 2 wallet instances, voucher race still correct
- [ ] Pipeline deploys automatically
- **Done when:** live endpoint works on AWS

## Day 6: Docs and cleanup (Req 18, 19, 24 + deliverables)

- [ ] Architecture diagram
- [ ] Deployment guide: build, run, Kafka, AWS, verify
- [ ] Remove dead code, debug logs, hard-coded values
- [ ] Architecture doc: services, communication, data, infrastructure, CI/CD
- [ ] API docs updated, Kafka events documented separately
- [ ] README: overview, architecture, services, Kafka, Docker, CI/CD, AWS, env vars, testing, challenges, unsolved, future
- [ ] ERD, credits, AWS screenshots
- [ ] Presentation
- **Done when:** every requirement ticked

## Optional bonuses (after Day 6)

- [x] API Gateway: one entry point for the frontend (`services/api-gateway`, port 8000)
- [ ] Service discovery: services find each other by name
- [ ] Advanced Kafka: multiple topics, consumer groups, dead-letter processing
- [ ] Advanced CI/CD: image scanning, versioned releases, rollback
- [ ] AWS observability: CloudWatch alarms and dashboards
- [ ] Rolling deployment: release without downtime

## Every day

- Commit small and often
- Tick tasks here before moving on
