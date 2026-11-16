# Requirements: Phase 04

[← Back to the README](../../README.md)

Source: [Project 04](https://github.com/Java-FT-01-Bahrain/JDB-Info/tree/main/Projects/Project-04)

✅ met · 🟡 partly met · ⬜ not started

## Services (Day 1 and 2)

| #  | Requirement                    | Status | Where                                                                       |
|----|--------------------------------|--------|-----------------------------------------------------------------------------|
| 1  | Microservices architecture     | ✅      | [`services/`](../../services): identity, wallet, notification, audit, gateway |
| 2  | Service boundaries documented  | ✅      | [`services.md`](services.md)                                                |
| 3  | Independent Spring Boot apps   | ✅ | Own app, `pom.xml`, config and [`Dockerfile`](../../services/wallet-service/Dockerfile) per service |
| 4  | Service data ownership         | ✅      | [`services.md`](services.md), [`AccountHolder`][holder] (read-only `users`) |
| 7  | Configuration and env vars     | ✅      | [`.env.example`](../../.env.example), each `application.properties`         |
| 24 | Code quality and refactoring   | ✅ | No debug prints, TODOs or secrets in code; 12 leftover docs/constants classes and about 175 unused constants from the monolith split removed; each Swagger tag list holds only its own service |

## Docker and Kafka (Day 3)

| #  | Requirement                 | Status | Where                                                                                 |
|----|-----------------------------|--------|---------------------------------------------------------------------------------------|
| 5  | Docker                      | ✅ | Multi-stage `Dockerfile` per service (build natively, run on amd64: [Step 8a](../deployment/aws/08-elastic-beanstalk.md#8a-images-built-for-amd64)) |
| 6  | Docker Compose              | ✅ | [`docker-compose.yml`](../../docker-compose.yml): 5 services, Postgres, Kafka; [`deploy/docker-compose.yml`](../../deploy/docker-compose.yml) for AWS |
| 8  | Apache Kafka                | ✅      | `account-events`, `email-events`, `audit-events`                                      |
| 9  | Producers and consumers     | ✅      | Producers: [identity][pub-id], [wallet][pub-wallet]. Consumers: [wallet][acc], [notification][email], [audit][audit] |
| 10 | Event-driven processing     | ✅      | [Registration example](services.md#example-registration)                              |
| 11 | Kafka reliability           | ✅      | Retries and `<topic>-dlt` in each `KafkaErrorConfig`; duplicates ignored in listeners |

## CI/CD and tests (Day 4)

| #  | Requirement               | Status | Where                                                                                   |
|----|---------------------------|--------|-----------------------------------------------------------------------------------------|
| 12 | CI/CD pipeline            | ✅ | Jenkins: test → images → ECR → Beanstalk; frontend → S3 + CloudFront ([`jenkins.md`](../deployment/jenkins.md)) |
| 13 | Continuous integration    | ✅ | Every push to `master` builds and tests; a failing test stops the pipeline |
| 14 | Image registry (ECR)      | ✅ | 5 repositories, tags `build-N` and `latest` ([Step 3](../deployment/aws/03-ecr.md), [Step 4](../deployment/aws/04-jenkins-ecr.md)) |
| 22 | Testing                   | ✅ | Kafka: [1][t-pub], [2][t-acc], [3][t-email], [4][t-audit]. Health: [5][t-health]. Storage: `DiskFileStoreTest`. End to end on AWS: [Step 11](../deployment/aws/11-verify.md) |
| 23 | Cucumber testing          | ✅      | [`features/`](../../services/wallet-service/src/test/resources/features): send money, payment request, bulk payout |

## AWS (Day 5)

| #  | Requirement                 | Status | Where                                                              |
|----|-----------------------------|--------|--------------------------------------------------------------------|
| 15 | AWS deployment              | ✅ | Elastic Beanstalk, live at `https://wallet.almotawaj.com` ([AWS index](../deployment/aws/index.md)) |
| 16 | Cloud database (RDS)        | ✅ | Private RDS PostgreSQL ([Step 5](../deployment/aws/05-rds.md)) |
| 17 | AWS security                | ✅ | Security groups, IAM roles with least access, Parameter Store, private RDS and Kafka ([Steps 2, 7](../deployment/aws/index.md)) |
| 20 | Monitoring and logging      | ✅ | CloudWatch logs for all containers ([Step 9](../deployment/aws/09-cloudwatch.md)) |
| 21 | Preserve Project 3 concurrency | ✅  | [`ConcurrencyIntegrationTest`][t-conc], [`ParallelPurchaseHttpTest`][t-par]; locks in PostgreSQL keep data consistent across instances; multi-instance demo optional |

## Docs (Day 6)

| #  | Requirement              | Status | Where |
|----|--------------------------|--------|-------|
| 18 | Deployment architecture  | 🟡 | Plan in [Step 17](../deployment/aws/17-architecture.md) |
| 19 | Deployment documentation | ✅ | [`deployment/aws`](../deployment/aws/index.md), [`jenkins.md`](../deployment/jenkins.md) |

[holder]: ../../services/wallet-service/src/main/java/com/almotawaj/wallet/account/model/AccountHolder.java
[pub-id]: ../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/event/EventPublisher.java
[pub-wallet]: ../../services/wallet-service/src/main/java/com/almotawaj/wallet/event/EventPublisher.java
[acc]: ../../services/wallet-service/src/main/java/com/almotawaj/wallet/account/event/AccountEventListener.java
[email]: ../../services/notification-service/src/main/java/com/almotawaj/wallet/notification/email/EmailEventListener.java
[audit]: ../../services/audit-service/src/main/java/com/almotawaj/wallet/audit/service/AuditEventListener.java
[t-pub]: ../../services/identity-service/src/test/java/com/almotawaj/wallet/identity/event/EventPublisherTest.java
[t-acc]: ../../services/wallet-service/src/test/java/com/almotawaj/wallet/account/event/AccountEventListenerTest.java
[t-email]: ../../services/notification-service/src/test/java/com/almotawaj/wallet/notification/email/EmailEventListenerTest.java
[t-audit]: ../../services/audit-service/src/test/java/com/almotawaj/wallet/audit/service/AuditServiceTest.java
[t-health]: ../../services/wallet-service/src/test/java/com/almotawaj/wallet/health/HealthEndpointTest.java
[t-conc]: ../../services/wallet-service/src/test/java/com/almotawaj/wallet/voucher/service/ConcurrencyIntegrationTest.java
[t-par]: ../../services/wallet-service/src/test/java/com/almotawaj/wallet/voucher/controller/ParallelPurchaseHttpTest.java
