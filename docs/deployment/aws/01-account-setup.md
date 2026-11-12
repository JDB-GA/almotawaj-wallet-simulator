# Step 1: Account Setup

[← Back to AWS](index.md)

**Goal:** one region for everything, a budget alert, and an IAM user for daily work.

## 1a. Region

| Value | Why |
| --- | --- |
| `eu-north-1` (Stockholm) | Low prices and every service we need. All resources live here |

**Check:** the top-right of the console shows **Europe (Stockholm)** before creating anything.

**Cost:** none.

## 1b. Budget alert

| Value | Why |
| --- | --- |
| Monthly cost budget, 20 USD, email alert | Warns before the $100 credits run low |

**Check:** Billing and Cost Management → Budgets shows the budget.

**Cost:** free (the first 2 budgets are free).

## 1c. IAM user for Jenkins

IAM → Users → Create user.

| Value | Why |
| --- | --- |
| Name `jenkins-deployer` | Jenkins gets its own login; the root account is never used by tools |
| No console access | Jenkins uses only the command line |
| `AmazonEC2ContainerRegistryPowerUser` | Push images to ECR |
| `AdministratorAccess-AWSElasticBeanstalk` | Deploy to Elastic Beanstalk |

Access keys are created in Step 4, when Jenkins needs them.

**Check:** IAM → Users shows `jenkins-deployer` with the 2 policies.

**Cost:** free.
