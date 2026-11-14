# Step 4: Jenkins Pushes Images to ECR

[← Back to AWS](index.md)

**Goal:** after tests pass, Jenkins pushes the five images to ECR.

## 4a. Access key for Jenkins

IAM → Users → `jenkins-deployer` → Security credentials → Create access key.

| Value | Why |
| --- | --- |
| Use case: Third-party service | Jenkins is a tool outside AWS |
| Downloaded as `.csv`, kept outside the repo | The secret is shown only once and must never be committed |

## 4b. Key stored in Jenkins

Manage Jenkins → Credentials → (global) → Add Credentials.

| Field | Value |
| --- | --- |
| Kind | Username with password |
| Username | Access key ID |
| Password | Secret access key |
| ID | `aws-jenkins` |

**Why:** Jenkins encrypts it; the `Jenkinsfile` refers only to the ID `aws-jenkins`.

**Check:** Manage Jenkins → Credentials lists `aws-jenkins`.

**Cost:** free.

## 4c. AWS CLI inside Jenkins

[`jenkins/Dockerfile`](../../../jenkins/Dockerfile) copies the AWS CLI from the official `amazon/aws-cli` image, so Jenkins can log in to ECR.

```dockerfile
COPY --from=amazon/aws-cli:2.22.35 /usr/local/aws-cli /usr/local/aws-cli
USER root
RUN ln -s /usr/local/aws-cli/v2/current/bin/aws /usr/local/bin/aws
USER jenkins
```

`USER root` is needed for the link; without it the build fails with "Permission denied".

Rebuild and restart (jobs and credentials stay in the `jenkins-data` volume):

```bash
docker build -t almotawaj-jenkins jenkins
docker rm -f jenkins
docker run -d --name jenkins -p 8090:8080 -v jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --user root almotawaj-jenkins
```

**Check:** `docker exec jenkins aws --version` prints the version.

## 4d. Push to ECR stage

[`Jenkinsfile`](../../../Jenkinsfile) runs **Build and Test → Build Images → Push to ECR**.

| Part | Why |
| --- | --- |
| `AWS_REGION = 'eu-north-1'` | Same region as every resource |
| `COMPOSE_PROJECT_NAME = 'almotawaj'` | Images are always named `almotawaj-<service>` |
| `withCredentials(... 'aws-jenkins' ...)` | Loads the access key only inside this stage |
| `aws sts get-caller-identity` | Reads the account number, so it is not hardcoded |
| Tag `:$BUILD_NUMBER` | Fixed version for rollback |
| Tag `:latest` | Always the newest green build; used by the deploy |

A failed test stops the pipeline before this stage, so `latest` only moves on a green build.

**Check:** ECR → each `almotawaj/<service>` repository shows a build number tag and `latest`.

**Cost:** storage only (about $0.10 per GB after 500 MB free).
