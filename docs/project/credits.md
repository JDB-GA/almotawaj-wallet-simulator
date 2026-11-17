# Credits

[← Back to the README](../../README.md)

## Payment gateway process

The merchant payment gateway in this project follows the hosted checkout process that card payment gateways use. Credit goes to Mastercard for documenting that process publicly in the **Mastercard Gateway: Hosted Checkout** integration guide:

https://ap-gateway.mastercard.com/api/documentation/integrationGuidelines/hostedCheckout/integrationModelHostedCheckout.html

| Step in the Mastercard guide                         | The same step in this project                                              |
| ---------------------------------------------------- | -------------------------------------------------------------------------- |
| The merchant's server starts a checkout session      | `POST /api/gateway/checkout-sessions` with the merchant's API key          |
| The payer is sent to a payment page the gateway hosts | The customer is redirected to the wallet's checkout page                   |
| The payer is returned to the merchant's return URL   | The customer is sent back to the session's `returnUrl`                     |
| The merchant's server retrieves the order to confirm | `GET /api/gateway/checkout-sessions/{sessionId}` before showing the result |
| The gateway sends webhook notifications              | Signed callbacks to the merchant's callback URL                            |

This project is not affiliated with Mastercard and does not use Mastercard's services or code. It pays from a wallet balance, not from a card.

## Statement and receipt font

PDF statements and receipts use **IBM Plex Sans Arabic** (Regular and SemiBold), which covers both English and Arabic. It is made by IBM and released under the SIL Open Font License 1.1; the licence ships next to the font files in `backend/src/main/resources/pdf/fonts/OFL.txt`.

https://github.com/IBM/plex

## Phase 03 resources

| Resource | Link | Used for |
| --- | --- | --- |
| Oracle Java Tutorials: Concurrency | https://docs.oracle.com/javase/tutorial/essential/concurrency/ | Threads, synchronization, executors, atomic variables |
| `ExecutorService` / `Future` / `CountDownLatch` API docs | https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/package-summary.html | The payout thread pool and the parallel tests |
| Spring Framework: Transaction Management | https://docs.spring.io/spring-framework/reference/data-access/transaction.html | Transaction boundaries for purchases and payout items |
| Spring Framework: Task Execution and Scheduling | https://docs.spring.io/spring-framework/reference/integration/scheduling.html | `@Async` background processing |
| PostgreSQL: Explicit Locking | https://www.postgresql.org/docs/current/explicit-locking.html | Row locks (`SELECT … FOR UPDATE`) on wallets |
| Jakarta Persistence: Locking and `@Version` | https://jakarta.ee/specifications/persistence/3.2/ | Optimistic and pessimistic locking, `@CheckConstraint` |
| Hibernate ORM User Guide: Locking | https://docs.jboss.org/hibernate/orm/7.0/userguide/html_single/Hibernate_User_Guide.html#locking | How Hibernate applies version checks and lock modes |
| MDN: Server-sent events | https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events | Live stock and payout progress events |
| IETF draft: The Idempotency-Key HTTP Header | https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/ | Header name and replay behaviour for safe retries |


## Phase 04 resources

| Resource | Link | Used for |
| --- | --- | --- |
| Spring Cloud Gateway (Server MVC) | https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webmvc.html | The API gateway routes |
| Spring for Apache Kafka | https://docs.spring.io/spring-kafka/reference/ | Producers, listeners, retries and dead-letter topics |
| Apache Kafka documentation | https://kafka.apache.org/documentation/ | KRaft mode and listener settings |
| Docker: multi-stage builds | https://docs.docker.com/build/building/multi-stage/ | Service images |
| Docker: multi-platform builds | https://docs.docker.com/build/building/multi-platform/ | `$BUILDPLATFORM` for amd64 images on an Apple chip |
| Jenkins Pipeline syntax | https://www.jenkins.io/doc/book/pipeline/syntax/ | `Jenkinsfile`, `when { changeset }` |
| AWS Elastic Beanstalk: Docker Compose | https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/create_deploy_docker.html | Running the services |
| AWS Elastic Beanstalk: environment secrets | https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/AWSHowTo.secrets.html | Parameter Store secrets |
| Amazon CloudFront: origins and behaviors | https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/distribution-web-values-specify.html | One domain for frontend and API |
| AWS Certificate Manager: CAA | https://docs.aws.amazon.com/acm/latest/userguide/setup-caa.html | Fixing the certificate CAA error |
| Cucumber JVM | https://cucumber.io/docs/installation/java/ | Feature files and step definitions |
| Diagrams (mingrammer) | https://diagrams.mingrammer.com | Architecture diagrams with AWS icons |
