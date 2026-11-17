# Planning: Phase 04

[← Back to the README](../../README.md)

**Goal:** split the wallet into services, run them in Docker with Kafka, and deploy to AWS through Jenkins.

## Before Day 1

- [x] Docker Desktop installed
- [x] AWS account with a billing alert
- [x] AWS CLI configured
- [x] Branch `feature-microservices`

## Day 1: Identity split

- [x] Create the service skeletons
- [x] Document each service: [`services.md`](services.md)
- [x] Move auth, users and KYC into identity
- [x] Identity issues the JWT; other services check it

## Day 2: Wallet split

- [x] Wallet reads `users` but never writes it
- [x] One database; each service writes only its own tables
- [x] Secrets moved to environment variables (`.env.example`)
- [x] Wallet created at registration through `account-events`
- [x] Gateway routes every request; `/internal/**` stays private
- [x] Unused code removed

## Day 3: Docker and Kafka

- [x] One Dockerfile per service
- [x] `docker-compose.yml` with services, Postgres and Kafka
- [x] Topics: `account-events`, `email-events`, `audit-events`
- [x] Retries (3 x 2 s), dead-letter topics, duplicate-safe consumers

## Day 4: CI/CD and tests

- [x] Jenkins builds, tests and builds images: [`jenkins.md`](../deployment/jenkins.md)
- [x] Images pushed to ECR with version tags
- [x] A failing test stops the pipeline
- [x] New tests: Kafka events, health, service APIs
- [x] Cucumber: send money, payment request, bulk payout

## Day 5: AWS

Guide: [`deployment/aws`](../deployment/aws/index.md)

- [x] Services on Elastic Beanstalk
- [x] Private RDS database
- [x] IAM roles, security groups, Parameter Store
- [x] Private Kafka on EC2
- [x] Logs in CloudWatch
- [x] Pipeline deploys automatically
- [x] Custom domain `wallet.almotawaj.com` on CloudFront
- [x] Jenkins deploys the frontend to S3
- [x] KYC files in S3
- [x] Jenkins and pgAdmin on one EC2
- [ ] Kafka TLS and per-service permissions

## Day 6: Docs and cleanup

- [x] Architecture diagrams: [Step 18](../deployment/aws/18-architecture.md)
- [x] Deployment guide
- [x] Dead code and hard-coded values removed
- [x] API and Kafka docs updated
- [x] README
- [x] ERD updated: [`erd.dbml`](../reference/erd.dbml)
- [ ] Presentation

## Day 7: RAG chatbot

- [x] Gemini API key in `.env` and Parameter Store
- [x] `chatbot-service`: chat API with Gemini File Search
- [x] Admin page to upload, list and delete the docs
- [x] Chat widget for signed-in users (English and Arabic)
- [x] Gateway route, Docker Compose, Jenkins and ECR
- [x] Diagram: [Step 18](../deployment/aws/18-architecture.md)

## Optional bonuses

- [x] API gateway
- [ ] Service discovery
- [ ] Advanced Kafka
- [ ] Advanced CI/CD: image scanning, rollback
- [ ] CloudWatch alarms and dashboards
- [ ] Zero-downtime deployment

## Day 7: RAG chatbot (extra)

- [ ] Upload the docs to Google AI File Search
- [ ] Chat endpoint in the backend
- [ ] Chat window in the frontend
