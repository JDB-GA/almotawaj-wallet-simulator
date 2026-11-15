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

## The stage, line by line

```groovy
stage('Deploy') {
```
Last stage; runs only if build, tests and the ECR push succeeded.

```groovy
withCredentials([usernamePassword(credentialsId: 'aws-jenkins',
        usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
```
Loads the `jenkins-deployer` keys as environment variables, only inside this block. The `aws` command reads them automatically.

```bash
ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
```
Asks AWS "who am I?" and keeps the account number, so it is not hardcoded.

```bash
BUCKET=elasticbeanstalk-$AWS_REGION-$ACCOUNT
```
Name of the S3 bucket Beanstalk created on the first upload: `elasticbeanstalk-eu-north-1-<account>`.

```bash
VERSION=build-$BUILD_NUMBER
```
Names the release after the Jenkins build number, for example `build-12`.

```bash
cd deploy && jar -cfM ../$VERSION.zip docker-compose.yml && cd ..
```
Zips `deploy/docker-compose.yml` into `build-12.zip`. `-c` create, `-f` file name, `-M` no Java manifest (a plain zip).

```bash
aws s3 cp $VERSION.zip s3://$BUCKET/almotawaj/$VERSION.zip
```
Uploads the zip; Beanstalk only deploys from S3.

```bash
aws elasticbeanstalk create-application-version --application-name almotawaj \
    --version-label $VERSION --source-bundle S3Bucket=$BUCKET,S3Key=almotawaj/$VERSION.zip
```
Registers the zip as application version `build-12`. Old versions stay, which makes rollback possible.

```bash
aws elasticbeanstalk update-environment --environment-name almotawaj-prod --version-label $VERSION
```
Switches `almotawaj-prod` to `build-12`. Beanstalk runs `docker compose up` again and pulls the new `:latest` images.

## Whole chain

```text
git push → tests → build images → push :latest to ECR → zip → S3 → new version → environment pulls new images
```

## Result

First automatic deploy: Jenkins build green, Beanstalk running version `build-7`.
