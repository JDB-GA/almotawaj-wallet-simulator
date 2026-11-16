# Step 16: KYC Uploads to S3

[← Back to AWS](index.md)

**Goal:** KYC documents survive restarts and redeploys. Before, identity wrote them to `/tmp/uploads` inside the container, which is wiped on every restart.

## Code (identity-service)

One switch: `STORAGE_BUCKET` set → S3; empty → local disk (`STORAGE_ROOT`). Local development and tests keep using the disk.

| File | Role |
| --- | --- |
| [`FileStore`](../../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/common/service/FileStore.java) | `save`, `load`, `delete` |
| [`DiskFileStore`](../../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/common/service/DiskFileStore.java) | Files on disk; rejects keys outside the root |
| [`S3FileStore`](../../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/common/service/S3FileStore.java) | Objects in the S3 bucket |
| [`FileStoreConfig`](../../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/config/app/FileStoreConfig.java) | Picks disk or S3 from `storage-bucket` |
| [`FileStorageService`](../../../services/identity-service/src/main/java/com/almotawaj/wallet/identity/common/service/FileStorageService.java) | Unchanged behavior (delete on rollback, delete after commit); now calls `FileStore` |

`application.properties`:

```properties
storage-bucket=${STORAGE_BUCKET:}
storage-region=${AWS_REGION:eu-north-1}
```

Dependency: `software.amazon.awssdk:s3`. On AWS the SDK uses the instance role automatically; no keys in the app.

Test: [`DiskFileStoreTest`](../../../services/identity-service/src/test/java/com/almotawaj/wallet/identity/common/service/DiskFileStoreTest.java).

## 16a. Bucket

S3 → Create bucket.

| Setting | Value | Why |
| --- | --- | --- |
| Name | `almotawaj-kyc-481374029689-481374029689-eu-north-1-an` | The console's account regional option added `-481374029689-eu-north-1-an` to the typed name `almotawaj-kyc-481374029689` |
| Region | `eu-north-1` | |
| Block all public access | On | ID documents are never public |
| Default encryption | SSE-S3 (default) | Encrypted at rest |

## 16b. Permission for the Beanstalk instance

IAM → Roles → `almotawaj-eb-ec2-role` → Add permissions → Create inline policy `almotawaj-kyc-storage`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject", "s3:GetObject", "s3:DeleteObject"],
      "Resource": "arn:aws:s3:::almotawaj-kyc-481374029689-481374029689-eu-north-1-an/*"
    }
  ]
}
```

Only this bucket's objects; no listing.

## 16c. Environment property

Beanstalk → Configuration → Updates, monitoring, and logging → Environment properties (Plain text):

| Name | Value |
| --- | --- |
| `STORAGE_BUCKET` | `almotawaj-kyc-481374029689-481374029689-eu-north-1-an` |

`STORAGE_ROOT` can stay; it is only used when `STORAGE_BUCKET` is empty.

## 16d. Deploy

Pushing the identity change runs the backend stages in Jenkins (`services/**` changed).

**Correction:** the IAM policy and `STORAGE_BUCKET` first used the short name, a bucket that does not exist, so uploads could not land. Both were changed to the real name.

## Correction: `STORAGE_BUCKET` was not saved

Files appeared in the app but not in the bucket. Beanstalk's settings showed only `STORAGE_ROOT=/tmp/uploads`; `STORAGE_BUCKET` was missing, so identity fell back to the container disk (same as the first CORS attempt in Step 14). It was added again and saved with **Apply** at the bottom of the page.

| Name | Value |
| --- | --- |
| `STORAGE_ROOT` | `/tmp/uploads` (keep: identity needs it to start; unused once the bucket is set) |
| `STORAGE_BUCKET` | `almotawaj-kyc-481374029689-481374029689-eu-north-1-an` |

Check what Beanstalk really has:

```bash
aws elasticbeanstalk describe-configuration-settings --region eu-north-1 --application-name almotawaj --environment-name almotawaj-prod --query "ConfigurationSettings[0].OptionSettings[?OptionName=='STORAGE_BUCKET'].Value" --output text
```

Check the bucket:

```bash
aws s3 ls s3://almotawaj-kyc-481374029689-481374029689-eu-north-1-an/
```

## Correction: IAM policy still had the short name

The submit then failed with "Something went wrong". identity's log:

```text
S3Exception: ... almotawaj-eb-ec2-role ... is not authorized to perform: s3:PutObject
on resource: "arn:aws:s3:::almotawaj-kyc-481374029689-481374029689-eu-north-1-an/..."
```

The inline policy `almotawaj-kyc-storage` still pointed to `almotawaj-kyc-481374029689/*`. Changed to:

```json
"Resource": "arn:aws:s3:::almotawaj-kyc-481374029689-481374029689-eu-north-1-an/*"
```

IAM changes apply within seconds; no restart.

## Result

A KYC application submitted on `https://wallet.almotawaj.com` stored 3 files (2 PDF, 1 JPG) at the top level of the bucket, named `<random id>.pdf` or `.jpg` (from `FileStorageService.store`). The application row keeps those names so admin can open them later.
