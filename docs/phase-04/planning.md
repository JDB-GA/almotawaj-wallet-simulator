# Planning: Phase 04

[← Back to the README](../../README.md)

**Goal:** the same wallet, split into services, containerized, event-driven and deployed to AWS through a pipeline.

## Before Day 1

- [ ] Docker Desktop installed
- [ ] AWS account with a billing alert
- [ ] AWS CLI configured locally
- [ ] Branch `feature-microservices`

## Day 1: Services (Req 1, 2, 3, 4, 7)

- [ ] Decide service boundaries: identity, wallet, notification
- [ ] Document each service: responsibility, APIs, data, communication
- [ ] One Spring Boot app per service, no shared internal classes
- [ ] One database or schema per service
- [ ] Secrets and URLs moved to environment variables, `.env.example` added
- **Done when:** every service starts on its own

## Day 2: Docker and Kafka (Req 5, 6, 8, 9, 10, 11)

- [ ] Dockerfile per service (multi-stage build)
- [ ] `docker-compose.yml`: services, Postgres, Kafka
- [ ] Services talk by Docker service name, not localhost
- [ ] Topic and event model defined
- [ ] Producer in wallet service, consumer in notification service
- [ ] Retries, dead-letter topic, duplicate-safe consumer
- **Done when:** `docker compose up` runs all and an event flows end to end

## Day 3: CI/CD and tests (Req 12, 13, 14, 22, 23)

- [ ] GitHub Actions: build, test, build images
- [ ] Push images to ECR with version tags
- [ ] Pipeline fails on a failing test
- [ ] Previous tests still pass
- [ ] New tests: Kafka events, health endpoints, service APIs
- [ ] Cucumber feature files for key flows
- **Done when:** a push runs the full pipeline

## Day 4: AWS (Req 15, 16, 17, 20, 21)

- [ ] Deploy services to AWS
- [ ] Database on RDS, private
- [ ] IAM roles, security groups, Secrets Manager
- [ ] Logs in CloudWatch: app, container, Kafka activity
- [ ] 2 wallet instances, voucher race still correct
- [ ] Pipeline deploys automatically
- **Done when:** live endpoint works on AWS

## Day 5: Docs and cleanup (Req 18, 19, 24 + deliverables)

- [ ] Architecture diagram
- [ ] Deployment guide: build, run, Kafka, AWS, verify
- [ ] Remove dead code, debug logs, hard-coded values
- [ ] Architecture doc: services, communication, data, infrastructure, CI/CD
- [ ] API docs updated, Kafka events documented separately
- [ ] README: overview, architecture, services, Kafka, Docker, CI/CD, AWS, env vars, testing, challenges, unsolved, future
- [ ] ERD, credits, AWS screenshots
- [ ] Presentation
- **Done when:** every requirement ticked

## Optional bonuses (after Day 5)

- [ ] API Gateway: one entry point for the frontend
- [ ] Service discovery: services find each other by name
- [ ] Advanced Kafka: multiple topics, consumer groups, dead-letter processing
- [ ] Advanced CI/CD: image scanning, versioned releases, rollback
- [ ] AWS observability: CloudWatch alarms and dashboards
- [ ] Rolling deployment: release without downtime

## Every day

- Commit small and often
- Tick tasks here before moving on
