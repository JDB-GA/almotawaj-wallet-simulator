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
| Name | `almotawaj-kyc-481374029689` | Unique name |
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
      "Resource": "arn:aws:s3:::almotawaj-kyc-481374029689/*"
    }
  ]
}
```

Only this bucket's objects; no listing.

## 16c. Environment property

Beanstalk → Configuration → Updates, monitoring, and logging → Environment properties (Plain text):

| Name | Value |
| --- | --- |
| `STORAGE_BUCKET` | `almotawaj-kyc-481374029689` |

`STORAGE_ROOT` can stay; it is only used when `STORAGE_BUCKET` is empty.

## 16d. Deploy

Pushing the identity change runs the backend stages in Jenkins (`services/**` changed).

**Check:** submit a KYC application, then S3 → `almotawaj-kyc-481374029689` shows the files; an admin can still open them after **Restart app server**.

**Cost:** a few cents a month.
