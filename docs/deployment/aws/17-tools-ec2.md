# Step 17: Jenkins and pgAdmin on EC2

[← Back to AWS](index.md)

**Goal:** Jenkins always on (no longer depends on the Mac), and pgAdmin able to reach the private RDS. Both on one EC2 instance inside the VPC.

```text
EC2 "almotawaj-tools" (13.62.29.44)
 ├─ Jenkins  :8080 ──► GitHub, ECR, Beanstalk, S3, CloudFront
 └─ pgAdmin  :5050 ──► RDS :5432 (private)
```

## Access: HTTPS domains through CloudFront

Changed from the first plan (an SSM tunnel from the Mac) to real addresses, so the tools open from any browser:

```text
https://jenkins.almotawaj.com ─► CloudFront ─► EC2 :8080 (Jenkins login)
https://pgadmin.almotawaj.com ─► CloudFront ─► EC2 :5050 (pgAdmin login)
```

Only CloudFront can reach the instance; both tools have their own login; WAF rate limiting slows password guessing. A shell on the instance is still available without SSH: EC2 → Instances → `almotawaj-tools` → Connect → **Session Manager**.

Builds keep using **Poll SCM** (Jenkins has no public address for GitHub webhooks).

## Steps

| # | Step |
| --- | --- |
| 17a | IAM role `almotawaj-tools-role`: `AmazonSSMManagedInstanceCore` (tunnel) + the `jenkins-deployer` permissions (ECR push, Beanstalk deploy, frontend S3, CloudFront invalidation), so no access keys are stored in Jenkins |
| 17b | Security group `almotawaj-tools-sg`: no inbound rules; add it as a source in `almotawaj-db-sg` (PostgreSQL 5432) |
| 17c | EC2 `almotawaj-tools`: Amazon Linux 2023, `t3.medium` (4 GiB: Maven + Docker builds), 30 GiB disk, role from 17a, SG from 17b |
| 17d | User data: install Docker; run the Jenkins image from [`jenkins/Dockerfile`](../../../jenkins/Dockerfile) and `dpage/pgadmin4` |
| 17e | Move Jenkins: recreate the job (Pipeline from SCM, `master`, Poll SCM), credentials replaced by the instance role |
| 17f | pgAdmin: add server `almotawaj-db...rds.amazonaws.com`, database `postgres`, user `almotawaj_admin`, password from Parameter Store |
| 17g | Verify: a push deploys from the EC2 Jenkins; pgAdmin lists the tables; stop the local Jenkins |

## Decisions

| Choice | Why |
| --- | --- |
| One instance for both tools | Cheapest; both are internal tools |
| SSM instead of SSH | No open port and no IP list, from any network |
| Instance role instead of access keys | No long-lived keys stored in Jenkins |
| Services stay on one Beanstalk instance | Project is small; one instance per service would cost about $190 a month and no requirement asks for it |

**Cost:** about $38 a month (`t3.medium` + 30 GiB); stop the instance when not working to pay only for running hours.

## Done

### 17a. Role `almotawaj-tools-role`

IAM → Roles → Create role → AWS service → EC2:

| Policy | Why |
| --- | --- |
| `AmazonSSMManagedInstanceCore` | SSM tunnel |
| `AmazonEC2ContainerRegistryPowerUser` | Push images to ECR |
| `AdministratorAccess-AWSElasticBeanstalk` | Deploy to Beanstalk |
| Inline `almotawaj-frontend-deploy` | Frontend bucket upload and CloudFront invalidation (same JSON as [Step 15a](15-jenkins-frontend.md#15a-permission-for-jenkins-deployer)) |

### 17b. Security group `almotawaj-tools-sg`

| Setting | Value |
| --- | --- |
| Inbound | None (SSM needs no open port) |
| Outbound | All (GitHub, ECR, AWS APIs) |

`almotawaj-db-sg` got a second inbound rule: PostgreSQL 5432 from `almotawaj-tools-sg`, so pgAdmin reaches RDS. The rule for `almotawaj-app-sg` stays.

### 17c. Instance `almotawaj-tools`

| Setting | Value |
| --- | --- |
| Instance ID | `i-0466bdd6c74dec2c6` |
| AMI / type | Amazon Linux 2023 x86, `t3.medium`, 30 GiB gp3 |
| Key pair | None |
| Public IP | Enabled; then replaced by Elastic IP `13.62.29.44` (`ec2-13-62-29-44.eu-north-1.compute.amazonaws.com`), so the address survives stop and start |
| Security group | `almotawaj-tools-sg` (the wizard's own group would have opened SSH to everyone, so **Select existing** was used) |
| Role | `almotawaj-tools-role` |

### 17d. User data (first boot)

```bash
#!/bin/bash
dnf install -y docker git
systemctl enable --now docker

git clone https://github.com/JDB-GA/almotawaj-wallet-simulator.git /opt/almotawaj
docker build -t almotawaj-jenkins /opt/almotawaj/jenkins

docker run -d --name jenkins --restart unless-stopped -p 8080:8080 \
  -v jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock \
  --user root almotawaj-jenkins
```

pgAdmin was then restarted in **login mode** from the browser shell (Session Manager); the password is typed there and never stored in a file:

```bash
sudo docker rm -f pgadmin && sudo docker volume rm pgadmin-data && sudo docker run -d --name pgadmin --restart unless-stopped -p 5050:80 -e PGADMIN_DEFAULT_EMAIL=admin@almotawaj.com -e PGADMIN_DEFAULT_PASSWORD='<password>' -v pgadmin-data:/var/lib/pgadmin dpage/pgadmin4
```

The volume is removed because pgAdmin reads the password only when it first creates its data.

### 17e. Only CloudFront reaches the instance

`almotawaj-tools-sg` inbound: Custom TCP **5050-8080** from prefix list `pl-fab65393` (`com.amazonaws.global.cloudfront.origin-facing`). One rule for the range, because each address in the list counts toward the group's rule limit and two rules would exceed it.

### 17f. Certificate and domains

| Item | Value |
| --- | --- |
| Certificate (us-east-1) | `jenkins.almotawaj.com` + `pgadmin.almotawaj.com` (one certificate) |
| Validation | Two CNAMEs in Cloudflare (DNS only) |

| Distribution | ID | Domain | Origin |
| --- | --- | --- | --- |
| `almotawaj-jenkins` | `EW6RP7GI40G00` | `d2xafh8ol15p58.cloudfront.net` → `jenkins.almotawaj.com` | EC2 :8080 |
| `almotawaj-pgadmin` | `E3Q7IRAYEFLDCX` | `d37a49wgzkiayt.cloudfront.net` → `pgadmin.almotawaj.com` | EC2 :5050 |

Both: origin HTTP only, Redirect HTTP to HTTPS, all methods, `CachingDisabled`, `AllViewerExceptHostHeader`, WAF (included in the plan) with rate limiting on.

Cloudflare (DNS only): `jenkins` → `d2xafh8ol15p58.cloudfront.net`, `pgadmin` → `d37a49wgzkiayt.cloudfront.net`.

### Notes

- pgAdmin's login wants the full email `admin@almotawaj.com`; just `admin` gives "Email/Username is not valid".
- On a network that inspects HTTPS (Fortinet), the certificate appears issued by Fortinet instead of Amazon; use another network or mobile data to check.

### 17g. pgAdmin connected to RDS

`https://pgadmin.almotawaj.com` → login `admin@almotawaj.com` → Servers → Register → Server:

| Field | Value |
| --- | --- |
| Name | `almotawaj-db` |
| Host | `almotawaj-db.cx080yqe428j.eu-north-1.rds.amazonaws.com` |
| Port | `5432` |
| Maintenance database | `postgres` |
| Username | `almotawaj_admin` |
| Password | Parameter Store `/almotawaj/DB_PASSWORD` |

**Result:** ✅ the tables in `postgres → public` are visible, so the private RDS is reachable from the tools instance only.

### 17h. Jenkins first setup

1. First-login password, from the browser shell (EC2 → `almotawaj-tools` → Connect → Session Manager):

   ```bash
   sudo docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

2. `https://jenkins.almotawaj.com` → paste the password → **Install suggested plugins**.
3. Admin user with a strong password (this login page is public).
4. Jenkins URL: `https://jenkins.almotawaj.com/`.

### 17i. Job and no access keys

Job `almotawaj-wallet`: Pipeline script from SCM, Git `https://github.com/JDB-GA/almotawaj-wallet-simulator.git`, branch `*/master`, Script Path `Jenkinsfile`, Poll SCM `H/2 * * * *`.

The [`Jenkinsfile`](../../../Jenkinsfile) no longer wraps AWS commands in `withCredentials('aws-jenkins')`: on EC2 the `aws` CLI uses the instance role `almotawaj-tools-role`, so no access keys are stored in Jenkins.

The local Jenkins on the Mac is stopped (`docker stop jenkins`), so only one Jenkins deploys each push. The `jenkins-deployer` access key is no longer used and can be deactivated (IAM → Users → `jenkins-deployer` → Security credentials).

## Result

The "Redesign Login Page" push ran only **Frontend: Build and Deploy** on the EC2 Jenkins, with the instance role and no access keys, and the new login page went live on `wallet.almotawaj.com`.
