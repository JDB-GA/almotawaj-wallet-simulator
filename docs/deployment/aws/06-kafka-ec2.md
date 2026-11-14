# Step 6: Kafka on EC2

[← Back to AWS](index.md)

**Goal:** one private Kafka broker that only the services can reach. Amazon MSK costs about $100+ a month, so a small EC2 instance runs the same Kafka image used locally.

EC2 → Launch instance.

| Setting | Value | Why |
| --- | --- | --- |
| Name | `almotawaj-kafka` | |
| AMI | Amazon Linux 2023, 64-bit (x86) | Matches `t3` |
| Instance type | `t3.small` (2 GiB) | Free tier; Kafka needs more than 1 GiB |
| Key pair | None | Set up by the startup script; no SSH needed |
| VPC | Default | Same as RDS and the services |
| Auto-assign public IP | Enabled | Needed to download Docker and Kafka (no NAT gateway); inbound is still blocked |
| Security group | `almotawaj-kafka-sg` | Only `almotawaj-app-sg` can reach port 9092 |
| Instances | 1 | One broker, same as local |

## Startup script (User data)

Runs once on first boot: installs Docker, reads the private IP, starts Kafka announcing that IP.

```bash
#!/bin/bash
dnf install -y docker
systemctl enable --now docker

TOKEN=$(curl -s -X PUT http://169.254.169.254/latest/api/token -H "X-aws-ec2-metadata-token-ttl-seconds: 60")
PRIVATE_IP=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/local-ipv4)

docker run -d --name kafka --restart unless-stopped -p 9092:9092 \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://$PRIVATE_IP:9092 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_NUM_PARTITIONS=3 \
  apache/kafka:3.9.1
```

## Connection value for the services

```text
KAFKA_BOOTSTRAP_SERVERS=172.31.14.149:9092
```

The private IP stays the same while the instance exists (stop and start keep it; terminate loses it).

## Limitations

- No TLS and no per-service topic permissions yet (planned in [`planning.md`](../../phase-04/planning.md)).
- One broker: if the instance stops, events wait in the producers' retries until it is back.

**Check:** instance state **Running**; status checks **2/2 passed**.

**Cost:** free tier for 750 hours a month; otherwise about $15 a month.
