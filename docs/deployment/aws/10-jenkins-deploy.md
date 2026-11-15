# Step 10: Jenkins Deploys Automatically

[← Back to AWS](index.md)

**Goal:** every green build on `master` goes live without clicks.

Pipeline: **Build and Test → Build Images → Push to ECR → Deploy**.

[`Jenkinsfile`](../../../Jenkinsfile) `Deploy` stage:

| Command | Why |
| --- | --- |
| `jar -cfM build-N.zip docker-compose.yml` (inside `deploy/`) | Packs the deploy file; the Jenkins image has no `zip`, Java's `jar` makes the same file |
| `aws s3 cp ... s3://elasticbeanstalk-eu-north-1-<account>/almotawaj/` | Beanstalk reads versions from its own S3 bucket |
| `aws elasticbeanstalk create-application-version --version-label build-N` | One version per Jenkins build, so any build can be redeployed |
| `aws elasticbeanstalk update-environment --version-label build-N` | Deploys it; the instance pulls the new `:latest` images |

Uses the same `aws-jenkins` credential; `jenkins-deployer` already has `AdministratorAccess-AWSElasticBeanstalk`.

**Rollback:** Elastic Beanstalk → Application versions → pick an older `build-N` → **Deploy**.

**Check:** after a green build, Beanstalk → Events shows "Environment update completed successfully" and the running version is `build-N`.

**Cost:** S3 storage for small zip files, effectively free.
