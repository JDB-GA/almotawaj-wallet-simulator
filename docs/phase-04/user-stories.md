# User Stories: Phase 04

[← Back to the README](../../README.md)

Format: _As a [user], I want to [action], so that [reason]._ Extends the [Phase 03 stories](../phase-03/user-stories.md).

## Day 1 and 2: Services

**US-44: Independent services.** As an administrator, I want each service to run on its own, so that one can change without rebuilding the rest.

- [ ] Each service has its own app, config and database.

**US-45: No secrets in code.** As an administrator, I want configuration in environment variables, so that secrets never reach GitHub.

- [ ] No passwords, keys or environment URLs in the repository.

## Day 3: Docker and Kafka

**US-46: One-command start.** As a developer, I want to start everything with one command, so that I don't install each dependency by hand.

- [ ] `docker compose up` starts all services, Postgres and Kafka.

**US-47: Reliable notifications.** As a wallet holder, I want my notification even if the notification service was down, so that I never miss a money event.

- [ ] Events wait in Kafka, are processed once the service is back, and never twice.

**US-48: Automatic recovery.** As an administrator, I want failed containers to restart, so that the app recovers without me.

- [ ] A stopped container comes back on its own.

## Day 4: CI/CD

**US-49: Safe releases.** As a developer, I want every push built and tested, so that broken code never reaches production.

- [ ] A failing test stops the pipeline.

## Day 5: AWS

**US-50: Correct under scale.** As a client, I want my voucher purchase correct with several wallet instances, so that the last voucher is never sold twice.

- [ ] With 2 instances: one `201`, the rest `409`.

**US-51: Visible failures.** As an administrator, I want logs in one place, so that I can find why a service failed.

- [ ] App, container and Kafka logs visible in CloudWatch.
