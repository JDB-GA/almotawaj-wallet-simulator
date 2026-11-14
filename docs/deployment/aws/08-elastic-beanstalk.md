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

`t3.medium` (4 GiB): five Java services need about 1.5 to 2 GiB; `t3.small` (2 GiB) is too tight. About $31 a month.
