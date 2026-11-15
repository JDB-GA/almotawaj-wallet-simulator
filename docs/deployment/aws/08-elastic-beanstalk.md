# Step 8: Elastic Beanstalk

[← Back to AWS](index.md)

**Goal:** run the five services on AWS behind one public address.

## 8a. Images built for amd64

Free-tier and `t3` instances use Intel/AMD (amd64) chips, but Jenkins runs on an Apple chip (arm64). An arm64 image fails on `t3` with `exec format error`.

[`Jenkinsfile`](../../../Jenkinsfile) `environment` block:

```groovy
DOCKER_DEFAULT_PLATFORM = 'linux/amd64'
```

### Build fix: compile natively, package for amd64

With only the line above, Maven runs inside an emulated amd64 container and fails:

```text
tar: apache-maven-3.10.0/...: Cannot open: Function not implemented
failed to untar
```

Fix: the first line of every service `Dockerfile` builds on the machine's own chip:

```dockerfile
FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk AS build
```

| Stage | Platform | Why |
| --- | --- | --- |
| `build` (JDK, Maven) | Native (arm64 on the Mac) | No emulation, faster; a `.jar` runs on any chip |
| Final (JRE) | `linux/amd64` | Matches the `t3` instances; it has no `RUN`, so nothing is emulated |

## 8b. Instance role

IAM → Roles → Create role → AWS service → **EC2**.

| Value | Why |
| --- | --- |
| Name `almotawaj-eb-ec2-role` | Identity of the Beanstalk instance |
| `AWSElasticBeanstalkWebTier` | Beanstalk basics (logs, health) |
| `AWSElasticBeanstalkMulticontainerDocker` | Run several containers |
| `AmazonEC2ContainerRegistryReadOnly` | Pull images from ECR |
| `AmazonSSMReadOnlyAccess` | Read the Parameter Store secrets |

## 8c. Deploy file

[`deploy/docker-compose.yml`](../../../deploy/docker-compose.yml) is what Beanstalk runs.

| Choice | Why |
| --- | --- |
| `image:` from ECR, tag `latest` | Nothing is built on AWS |
| No Postgres or Kafka | Those are RDS (Step 5) and EC2 (Step 6) |
| `env_file: .env` | Beanstalk writes environment properties and secrets into `.env` on the instance |
| `x-service: &service` | Shared settings written once |
| `JAVA_TOOL_OPTIONS: -Xmx…` | Caps memory so five JVMs fit on one instance |
| `"80:8080"` on the gateway only | The load balancer reaches port 80; other services stay internal |

Zip it for upload (not committed):

```bash
cd deploy && zip ../almotawaj-deploy.zip docker-compose.yml && cd ..
```

## 8d. Instance size

`c7i-flex.large` (2 vCPU, 4 GiB, x86_64): five Java services need about 1.5 to 2 GiB; `t3.small` (2 GiB) is too tight.

**Correction:** `t3.medium` was chosen first, but the account only allows free-tier instance types. The environment hung on "There are no instances" and the Auto Scaling activity showed:

```text
The specified instance type is not eligible for Free Tier.
```

Free-tier types in this account:

```bash
aws ec2 describe-instance-types --region eu-north-1 --filters Name=free-tier-eligible,Values=true
```

| Type | Memory | Chip |
| --- | --- | --- |
| `t3.micro` / `t4g.micro` | 1 GiB | x86 / ARM |
| `t3.small` / `t4g.small` | 2 GiB | x86 / ARM |
| `c7i-flex.large` | 4 GiB | x86 |
| `m7i-flex.large` | 8 GiB | x86 |

Fix applied while the environment was stuck creating (it could not be changed or terminated then): a new version of its launch template with `c7i-flex.large`, then the Beanstalk setting `aws:ec2:instances InstanceTypes` set to `c7i-flex.large` once it was Ready.

## 8e. Create the environment

Elastic Beanstalk → Create application.

| Page | Setting | Value |
| --- | --- | --- |
| 1 | Tier | Web server environment |
| 1 | Application / environment | `almotawaj` / `almotawaj-prod` |
| 1 | Platform | Docker running on 64bit Amazon Linux 2023 |
| 1 | Code | Upload `almotawaj-deploy.zip`, version `v1` |
| 1 | Preset | Single instance |
| 2 | Service role | `aws-elasticbeanstalk-service-role` (created) |
| 2 | Instance profile | `almotawaj-eb-ec2-role` |
| 2 | Key pair | None |
| 3 | VPC / subnets | Default VPC, all subnets |
| 3 | Public IP | Activated (single instance has no load balancer) |
| 3 | Database | Off (RDS from Step 5 is used) |
| 4 | Security group | `almotawaj-app-sg` |
| 4 | Instance type | `c7i-flex.large` (see 8d) |
| 5 | Health reporting | Enhanced |
| 5 | Instance log streaming to CloudWatch | Activated, 7 days, delete on termination |

### Environment properties (Plain text)

| Name | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://almotawaj-db.cx080yqe428j.eu-north-1.rds.amazonaws.com:5432/postgres` |
| `DB_USERNAME` | `almotawaj_admin` |
| `KAFKA_BOOTSTRAP_SERVERS` | `172.31.14.149:9092` |
| `JWT_EXPIRATION_MS` | `86400000` |
| `MAIL_FROM` | `Almotawaj Digital Wallet <no-reply@notify.almotawaj.com>` |
| `MAIL_LOGO_URL` | `https://almotawaj.com/logo/logo.png` |
| `APP_URL` | `http://localhost:5173` (until the frontend is deployed, Step 12) |
| `STORAGE_ROOT` | `/tmp/uploads` (lost on restart until Step 13) |

### Environment properties (SSM Parameter Store)

Value is the parameter ARN `arn:aws:ssm:eu-north-1:481374029689:parameter/almotawaj/<NAME>` for: `DB_PASSWORD`, `JWT_SECRET`, `OTP_SECRET`, `INTERNAL_API_TOKEN`, `RESEND_API_KEY`, `SEED_TOKEN`, `SEED_PASSWORD`.

## Result

URL: `http://almotawaj-prod.eba-jpqmjfvd.eu-north-1.elasticbeanstalk.com`

| Check | Result |
| --- | --- |
| Gateway `/actuator/health` | ✅ 200 `UP` |
| wallet `/api/vouchers` | ✅ 401 (running, login required) |
| identity `/api/profile` | ✅ 401 |
| audit `/api/admin/audit-logs` | ✅ 401 |
| notification | ✅ Started (no HTTP API) |

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://almotawaj-prod.eba-jpqmjfvd.eu-north-1.elasticbeanstalk.com/api/vouchers
```

401 means the service runs and asks for a login; 500 or 503 means it is down.

## Correction: database password mismatch

**Symptom:** identity, wallet and audit restarted every ~20 seconds. Logs showed:

```text
FATAL: password authentication failed for user "almotawaj_admin"
```

**Checks:** Beanstalk fetched `/almotawaj/DB_PASSWORD` correctly (eb-engine log: "Fetching SSM Parameter Store parameter"); the network to RDS worked (a login error, not a timeout). Setting RDS to the stored value failed with:

```text
Only printable ASCII characters besides '/', '@', '"', ' ' may be used.
```

So the stored value was never a valid RDS password.

**Fix:** one new password saved in both places at once, never printed:

```bash
P=$(openssl rand -hex 16) && aws ssm put-parameter --region eu-north-1 --name /almotawaj/DB_PASSWORD --type SecureString --overwrite --value "$P" && aws rds modify-db-instance --region eu-north-1 --db-instance-identifier almotawaj-db --apply-immediately --master-user-password "$P"; unset P
```

Then Elastic Beanstalk → Actions → **Restart app server(s)**. No rebuild is needed.

**Lesson:** generate passwords with `openssl rand -hex`, which only uses `0-9a-f` and is valid everywhere.

## Logs

CloudWatch → Log groups → `/aws/elasticbeanstalk/almotawaj-prod/var/log/eb-docker/containers/eb-current-app/stdouterr.log`. Use **30m** and filter `Started` or `Caused by`.

## Security note

Beanstalk's security group allows only HTTP 80 from anywhere; no SSH rule (checked in Step 9).
