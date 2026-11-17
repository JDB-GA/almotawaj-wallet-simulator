# Jenkins Setup

[← Back to the README](../../README.md)

Jenkins builds and tests every service, then builds the Docker images. The steps live in [`Jenkinsfile`](../../Jenkinsfile).

## 1. Build the Jenkins image

[`jenkins/Dockerfile`](../../jenkins/Dockerfile) adds the Docker command to Jenkins, so it can build images.

```bash
docker build -t almotawaj-jenkins jenkins
```

## 2. Start Jenkins

```bash
docker run -d --name jenkins -p 8090:8080 -v jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --user root almotawaj-jenkins
```

| Part | Meaning |
| --- | --- |
| `-p 8090:8080` | Open at `localhost:8090` (8080 is the gateway) |
| `-v jenkins-data:...` | Keep Jenkins settings after restart |
| `-v /var/run/docker.sock:...` | Use the host's Docker to build images |

## 3. First login

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

1. Open `http://localhost:8090` and paste the password.
2. Choose **Install suggested plugins**.
3. Create the admin user.
4. Keep the Jenkins URL `http://localhost:8090/`.

## 4. Create the pipeline job

1. **New Item** → `almotawaj-wallet` → **Pipeline**.
2. Under **Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/JDB-GA/almotawaj-wallet-simulator.git`
   - Branch: `*/master`
   - Script Path: `Jenkinsfile`
3. **Save** → **Build Now**.

## 5. Build automatically on every push

Jenkins runs locally, so GitHub cannot call it. Jenkins checks GitHub instead.

1. Job → **Configure** → **Triggers** → tick **Poll SCM**.
2. Schedule: `H/2 * * * *` (check every 2 minutes; build only if there is a new commit).
3. **Save**.

## 6. AWS additions

- AWS CLI and the `aws-jenkins` credential: [AWS Step 4](aws/04-jenkins-ecr.md)
- Push to ECR stage: [AWS Step 4d](aws/04-jenkins-ecr.md#4d-push-to-ecr-stage)
- Images built for amd64: [AWS Step 8a](aws/08-elastic-beanstalk.md#8a-images-built-for-amd64)
- Deploy to Beanstalk: [AWS Step 10](aws/10-jenkins-deploy.md)
- Node.js and pnpm, frontend deploy, separate frontend and backend: [AWS Step 15](aws/15-jenkins-frontend.md)

## What the pipeline does

```text
services/** changed → Backend: Build and Test (5 in parallel) → Build Images → Push to ECR → Deploy (Beanstalk)
deploy/**   changed → Backend: Deploy
frontend/** changed → Frontend: Build and Deploy (S3 + CloudFront refresh)
a test fails        → stop (red); nothing is pushed or deployed
```

Details: [AWS Step 10](aws/10-jenkins-deploy.md) (backend) and [AWS Step 15](aws/15-jenkins-frontend.md) (frontend).

## Useful commands

```bash
docker stop jenkins
```

```bash
docker start jenkins
```
