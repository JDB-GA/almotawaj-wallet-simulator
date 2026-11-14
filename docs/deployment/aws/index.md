# AWS Deployment

[← Back to the README](../../../README.md)

Every AWS step is written down as it is configured. Follow the steps in order; each one links to its own page.

Budget: $100 credits. Delete everything after the presentation (see [Cleanup](#cleanup)).

## Architecture

```text
Internet
   |
Load Balancer (HTTPS)          public subnet
   |
Elastic Beanstalk: api-gateway, identity, wallet, notification, audit
   |                |
RDS PostgreSQL    Kafka on EC2  private subnet
```

## Services used

| Need | AWS service |
| --- | --- |
| Docker images | ECR |
| Run the services | Elastic Beanstalk (Docker) |
| Database | RDS PostgreSQL |
| Kafka | EC2 |
| Secrets | Systems Manager Parameter Store |
| Logs | CloudWatch |
| Access | IAM, VPC, security groups |

## Steps

| # | Step | Page | Status |
| --- | --- | --- | --- |
| 1 | Region, budget alert, IAM user | [01-account-setup.md](01-account-setup.md) | ✅ |
| 2 | VPC and security groups | [02-security-groups.md](02-security-groups.md) | ✅ |
| 3 | ECR repositories | [03-ecr.md](03-ecr.md) | ✅ |
| 4 | Jenkins pushes images to ECR | [04-jenkins-ecr.md](04-jenkins-ecr.md) | ✅ |
| 5 | RDS PostgreSQL | [05-rds.md](05-rds.md) | ✅ |
| 6 | Kafka on EC2 | [06-kafka-ec2.md](06-kafka-ec2.md) | ✅ |
| 7 | Secrets (Parameter Store) | [07-secrets.md](07-secrets.md) | ✅ |
| 8 | Elastic Beanstalk | | ⬜ |
| 9 | CloudWatch logs | | ⬜ |
| 10 | Jenkins deploys automatically | | ⬜ |
| 11 | Verify end to end | | ⬜ |

## How each step page is written

- **Goal:** what this step gives us
- **Console clicks or commands:** exactly what was done, in order
- **Values chosen:** names, sizes, ports (never secrets)
- **Why:** one line per choice
- **Check:** how to confirm it works
- **Cost:** what it charges

## Cleanup

Filled in as resources are created, so nothing is forgotten.

| Resource | Name | How to delete |
| --- | --- | --- |
