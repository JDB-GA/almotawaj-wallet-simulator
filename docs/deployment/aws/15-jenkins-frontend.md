# Step 15: Jenkins Deploys the Frontend

[← Back to AWS](index.md)

**Goal:** a push updates the frontend too, and frontend and backend deploy separately.

## 15a. Permission for `jenkins-deployer`

IAM → Users → `jenkins-deployer` → Add permissions → Create inline policy `almotawaj-frontend-deploy`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    { "Effect": "Allow", "Action": ["s3:ListBucket"], "Resource": "arn:aws:s3:::almotawaj-frontend-481374029689" },
    { "Effect": "Allow", "Action": ["s3:PutObject", "s3:DeleteObject"], "Resource": "arn:aws:s3:::almotawaj-frontend-481374029689/*" },
    { "Effect": "Allow", "Action": ["cloudfront:CreateInvalidation"], "Resource": "arn:aws:cloudfront::481374029689:distribution/E1PQXMBPMZPI7D" }
  ]
}
```

Only the frontend bucket and this one distribution.

## 15b. Node.js and pnpm in Jenkins

[`jenkins/Dockerfile`](../../../jenkins/Dockerfile) copies Node.js from the official image and installs the same pnpm as the Mac:

```dockerfile
COPY --from=node:22-bookworm-slim /usr/local/bin/node /usr/local/bin/node
COPY --from=node:22-bookworm-slim /usr/local/lib/node_modules /usr/local/lib/node_modules
...
    && ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm \
    && npm install -g pnpm@11.23.0
```

Rebuild and restart Jenkins (jobs and credentials stay in `jenkins-data`):

```bash
docker build -t almotawaj-jenkins jenkins
docker rm -f jenkins
docker run -d --name jenkins -p 8090:8080 -v jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --user root almotawaj-jenkins
```

## 15c. Separate frontend and backend

Each stage runs only when its folder changed (`when { changeset ... }`), so a frontend change does not rebuild or restart the backend.

| Changed | Stages that run |
| --- | --- |
| `services/**` | Backend: Build and Test → Build Images → Push to ECR → Deploy |
| `deploy/**` | Backend: Deploy |
| `frontend/**` | Frontend: Build and Deploy |
| Only docs | Nothing |

## 15d. Frontend stage, line by line

```bash
pnpm install --frozen-lockfile
```
Installs exactly the versions in `pnpm-lock.yaml`.

```bash
VITE_API_URL=$FRONTEND_URL pnpm build
```
Builds with `https://wallet.almotawaj.com` baked in.

```bash
aws s3 sync dist s3://$FRONTEND_BUCKET --delete
```
Uploads new and changed files and deletes files that no longer exist.

```bash
aws cloudfront create-invalidation --distribution-id $CLOUDFRONT_ID --paths "/*"
```
CloudFront drops its cached copy, so users get the new version.

The bucket, distribution and domain are set once in the `environment` block of the [`Jenkinsfile`](../../../Jenkinsfile).

## Limit

`changeset` compares with the previous build. **Build Now** with no new commit skips every stage. To force a deploy, push a commit that touches the folder.
