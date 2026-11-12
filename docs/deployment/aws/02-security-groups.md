# Step 2: Security Groups

[← Back to AWS](index.md)

**Goal:** only the services can reach the database and Kafka; nothing from the internet can.

EC2 → Security Groups → Create security group. All in the default VPC.

| Name | Description | Inbound | Why |
| --- | --- | --- | --- |
| `almotawaj-app-sg` | Almotawaj services | None yet (load balancer added in Step 8) | Firewall for the 5 services |
| `almotawaj-db-sg` | Postgres from services only | PostgreSQL 5432 from `almotawaj-app-sg` | Database reachable only by the services |
| `almotawaj-kafka-sg` | Kafka from services only | TCP 9092 from `almotawaj-app-sg` | Kafka reachable only by the services |

Outbound: default (all traffic) on all three, so services can reach Resend and pull images.

Source is picked from the list (`almotawaj-app-sg | sg-...`), not typed, otherwise the console shows "CIDR block, a security group ID or a prefix list has to be specified".

**Check:** EC2 → Security Groups lists the three groups.

**Cost:** free.
