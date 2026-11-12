# Step 3: ECR Repositories

[← Back to AWS](index.md)

**Goal:** a private place to store each service's Docker image.

ECR → Create repository, once per service. Visibility **Private**, other settings default.

| Repository | Image of |
| --- | --- |
| `almotawaj/identity-service` | identity-service |
| `almotawaj/wallet-service` | wallet-service |
| `almotawaj/notification-service` | notification-service |
| `almotawaj/audit-service` | audit-service |
| `almotawaj/api-gateway` | api-gateway |

**Why:** one repository per service, so each image has its own versions. The `almotawaj/` prefix groups them.

**Check:** ECR → Private repositories lists the five.

**Cost:** 500 MB free per month, then about $0.10 per GB.
