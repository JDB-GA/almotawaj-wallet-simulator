# Requirements: Phase 04

[← Back to the README](../../README.md)

Source: [Project 04](https://github.com/Java-FT-01-Bahrain/JDB-Info/tree/main/Projects/Project-04)

✅ met · 🟡 partly met · ⬜ not started

## Services (Day 1 and 2)

| #  | Requirement                    | Status | Where                                                                       |
|----|--------------------------------|--------|-----------------------------------------------------------------------------|
| 1  | Microservices architecture     | ✅      | [`services/`](../../services): identity, wallet, notification, audit, gateway |
| 2  | Service boundaries documented  | ✅      | [`services.md`](services.md)                                                |
| 3  | Independent Spring Boot apps   | 🟡     | Own app, `pom.xml` and config per service; Dockerfiles not yet              |
| 4  | Service data ownership         | ✅      | [`services.md`](services.md), [`AccountHolder`][holder] (read-only `users`) |
| 7  | Configuration and env vars     | ✅      | [`.env.example`](../../.env.example), each `application.properties`         |
| 24 | Code quality and refactoring   | 🟡     | `config/constants/` per service; final cleanup on Day 6                     |

## Docker and Kafka (Day 3)

| #  | Requirement                 | Status | Where                                                                                 |
|----|-----------------------------|--------|---------------------------------------------------------------------------------------|
| 5  | Docker                      | ⬜      | Day 3                                                                                 |
| 6  | Docker Compose              | 🟡     | [`docker-compose.yml`](../../docker-compose.yml): Kafka only, services not yet        |
| 8  | Apache Kafka                | ✅      | `account-events`, `email-events`, `audit-events`                                      |
| 9  | Producers and consumers     | ✅      | Producers: [identity][pub-id], [wallet][pub-wallet]. Consumers: [wallet][acc], [notification][email], [audit][audit] |
| 10 | Event-driven processing     | ✅      | [Registration example](services.md#example-registration)                              |
| 11 | Kafka reliability           | ✅      | Retries and `<topic>-dlt` in each `KafkaErrorConfig`; duplicates ignored in listeners |

## CI/CD and tests (Day 4)

| #  | Requirement               | Status | Where                                                                                   |
|----|---------------------------|--------|-----------------------------------------------------------------------------------------|
| 12 | CI/CD pipeline            | ⬜      | Day 4                                                                                   |
| 13 | Continuous integration    | ⬜      | Day 4                                                                                   |
| 14 | Image registry (ECR)      | ⬜      | Day 4                                                                                   |
| 22 | Testing                   | 🟡     | Kafka: [1][t-pub], [2][t-acc], [3][t-email], [4][t-audit]. Health: [5][t-health]. Docker not yet |
| 23 | Cucumber testing          | ✅      | [`features/`](../../services/wallet-service/src/test/resources/features): send money, payment request, bulk payout |

## AWS (Day 5)

| #  | Requirement                 | Status | Where                                                              |
|----|-----------------------------|--------|--------------------------------------------------------------------|
| 15 | AWS deployment              | ⬜      | Day 5                                                              |
| 16 | Cloud database (RDS)        | ⬜      | Day 5                                                              |
| 17 | AWS security                | ⬜      | Day 5                                                              |
| 20 | Monitoring and logging      | ⬜      | Day 5                                                              |
| 21 | Preserve Project 3 concurrency | 🟡  | [`ConcurrencyIntegrationTest`][t-conc], [`ParallelPurchaseHttpTest`][t-par]; 2 instances not yet |

## Docs (Day 6)

| #  | Requirement              | Status | Where |
|----|--------------------------|--------|-------|
| 18 | Deployment architecture  | ⬜      | Day 6 |
| 19 | Deployment documentation | ⬜      | Day 6 |

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
