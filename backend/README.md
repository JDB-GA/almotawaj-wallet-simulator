# Backend

[← Back to the README](../README.md)

Java 17, Spring Boot 4, Spring Security, Spring Data JPA, PostgreSQL and OpenPDF. Runs on http://localhost:8080.

## Setup

Prerequisites: Java 17+, PostgreSQL.

```bash
cd backend
createdb -U postgres digital-wallet
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
```

Fill in `application-dev.properties` (it is gitignored – never commit it):

| Key                           | Value                                                         |
| ----------------------------- | ------------------------------------------------------------- |
| `spring.datasource.password`  | your PostgreSQL password                                      |
| `jwt-secret`                  | `openssl rand -hex 32`                                        |
| `otp-secret`                  | another `openssl rand -hex 32` (different from `jwt-secret`)  |
| `resend-api-key`, `mail-from` | your [Resend](https://resend.com) API key and verified sender |
| `cors-allowed-origins`        | `http://localhost:5173`                                       |
| `app-url`                     | `http://localhost:5173` (used in email links)                 |
| `storage-root`                | folder for uploaded files, default `uploads`                  |
| `seed-token`, `seed-password` | optional, see [Seeding](#seeding)                             |
| `webhook-allow-private-hosts` | `true` to let payment callbacks reach `localhost` while developing |

```bash
./mvnw spring-boot:run
./mvnw test
```

Tests use the `test` profile with an in-memory H2 database, so they never touch your PostgreSQL data.

## Profiles

| Profile | Database | Configuration |
| --- | --- | --- |
| `dev` (default) | local PostgreSQL | `application-dev.properties` (untracked) |
| `test` | in-memory H2 | `src/test/resources/application-test.properties` |
| `prod` | PostgreSQL on Railway | environment variables, see [deployment](../docs/deployment.md) |

## Seeding

Seeding is a single protected request, so it works on an empty database with no admin account.

1. Generate a token: `openssl rand -hex 32`.
2. Set `SEED_TOKEN` to it and `SEED_PASSWORD` to the password the demo accounts should use (in dev: `seed-token` / `seed-password`).
3. Call it from Postman, or in Swagger click **Authorize** and paste the token under `seedToken`:

```http
POST /api/seed
Authorization: Bearer <SEED_TOKEN>
```

`201` means the demo data exists. Running it again changes nothing. Without `SEED_TOKEN` the endpoint returns `404`; a wrong token returns `403`.

| Email                           | Mobile   | Role          | Demo state                  |
| ------------------------------- | -------- | ------------- | --------------------------- |
| `admin@almotawaj.com`           | 30000001 | `SUPER_ADMIN` | –                           |
| `reviewer@almotawaj.com`        | 30000004 | `ADMIN`       | `KYC_REVIEW`, `USER_MANAGE`, `STATISTICS_VIEW` |
| `merchant@almotawaj.com`        | 30000003 | `MERCHANT`    | –                           |
| `client@almotawaj.com`          | 30000002 | `CLIENT`      | KYC pending                 |
| `illia@almotawaj.com`           | 30000005 | `CLIENT`      | KYC approved                |
| `salman@almotawaj.com`          | 30000006 | `CLIENT`      | KYC rejected with a reason  |

All accounts use `SEED_PASSWORD` and have verified emails. Seeded KYC applications have no document files.

`DELETE /api/admin/seed-data` (super admin) removes only the demo accounts with their wallets, transactions, payment requests, verification records and documents. Other accounts keep their data.

## Structure

```
src/main/java/com/almotawaj/wallet
├── config/       security, CORS, rate limiting, OpenAPI, constants (paths, messages, docs)
├── controller/   REST controllers (thin – no business logic)
├── service/      business rules, policies, emails, file storage, notifications, payment gateway (pdf/ renders receipts and statements)
├── repository/   Spring Data JPA repositories and specifications
├── model/        entities, enums, request/response DTOs
├── event/        domain events (KYC reviewed, money received) handled after commit
├── exception/    domain exceptions and the global handler
└── util/         IBAN and login identifier helpers
```

Conventions: every path, error code, message and Swagger text is a constant in `config/constants`; controllers only map requests to services; files stay at or under 120 lines.

## Code documentation

The public methods of the main services are documented with JavaDoc:

| Area | File |
| --- | --- |
| Authentication | [`UserService.java`](src/main/java/com/almotawaj/wallet/service/UserService.java) |
| Wallet | [`WalletService.java`](src/main/java/com/almotawaj/wallet/service/WalletService.java) |
| Transactions (transfers) | [`TransferService.java`](src/main/java/com/almotawaj/wallet/service/TransferService.java) |

Generate the HTML documentation with `./mvnw javadoc:javadoc`; it is written to `target/reports/apidocs`.

## Tests

`./mvnw test` runs service, policy, security and repository-backed tests on the `test` profile. They never touch PostgreSQL.
