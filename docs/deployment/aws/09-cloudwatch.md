# Step 9: CloudWatch Logs

[← Back to AWS](index.md)

**Goal:** application, container and Kafka client logs in one place (requirement 20).

Enabled in the Beanstalk create form (page 5 → Platform software → Instance log streaming to CloudWatch logs).

| Setting | Value | Why |
| --- | --- | --- |
| Log streaming | Activated | Container output goes to CloudWatch |
| Retention | 7 days | Enough for debugging; keeps cost low |
| Lifecycle | Delete logs upon termination | Nothing left behind after cleanup |

## Where to look

| Log group | Contains |
| --- | --- |
| `/aws/elasticbeanstalk/almotawaj-prod/var/log/eb-docker/containers/eb-current-app/stdouterr.log` | All 5 services: startup, requests, Kafka producer and consumer activity, errors |
| `/aws/elasticbeanstalk/almotawaj-prod/var/log/eb-engine.log` | Deploys, image pulls, secret fetching |
| `/aws/elasticbeanstalk/almotawaj-prod/var/log/docker-events.log` | Container start, stop and restart |

Search: open the group → **Search log group** → pick **30m** → filter, for example `Started`, `Caused by`, `ERROR`, `Sent VERIFICATION_CODE`.

A service that restarts every ~20 seconds shows its `Starting` line again and again with no `Started` line: see the [password fix](08-elastic-beanstalk.md#correction-database-password-mismatch).

## Security check

Beanstalk's security group (`sg-0984fa46c27e76469`) allows only **HTTP 80 from anywhere**. No SSH rule exists.

**Cost:** first 5 GB of logs per month free.
