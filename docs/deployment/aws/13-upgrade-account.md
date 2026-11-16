# Step 13: Upgrade the AWS Account

[← Back to AWS](index.md)

**Why:** the Free account plan put the account in an AWS-managed organization with limits that blocked the project:

| Limit | Seen as |
| --- | --- |
| Free-tier instance types only | `t3.medium` failed: "not eligible for Free Tier" (Step 8) |
| Some services denied | Redshift "explicit deny in a service control policy" |
| One region | No N. Virginia, so no CloudFront certificate (Step 12) |

**How:** account name → Billing and Cost Management → Upgrade plan.

| Setting | Choice | Why |
| --- | --- | --- |
| Stop new resource launches | On | Near the spend limit, only new resources are blocked; the app keeps running |
| Pause idle resources | Off | Could pause Kafka or RDS and break the app |
| Pause top cost drivers | Off | Can take the app offline |

Remaining credits stay and are used first. Alerts arrive at 50%, 75% and 90% of the spend limit, plus the $20 budget alert from Step 1.
