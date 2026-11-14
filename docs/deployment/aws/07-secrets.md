# Step 7: Secrets

[← Back to AWS](index.md)

**Goal:** keep every secret out of the code and the images, encrypted in AWS.

**Service:** Systems Manager → Parameter Store. Encrypted and access-controlled like Secrets Manager, but free (Secrets Manager costs $0.40 per secret per month). Elastic Beanstalk reads both.

Create parameter → Tier **Standard**, Type **SecureString** for each.

| Parameter | Value |
| --- | --- |
| `/almotawaj/DB_PASSWORD` | RDS master password |
| `/almotawaj/JWT_SECRET` | New, from `openssl rand -hex 32` |
| `/almotawaj/OTP_SECRET` | New, from `openssl rand -hex 32` |
| `/almotawaj/INTERNAL_API_TOKEN` | New, from `openssl rand -hex 32` |
| `/almotawaj/RESEND_API_KEY` | Resend API key |
| `/almotawaj/SEED_TOKEN` | New, from `openssl rand -hex 32` |
| `/almotawaj/SEED_PASSWORD` | Password for the demo accounts |

**Why new values:** production never shares secrets with the local `.env`.

**Not secret** (set as plain environment variables in Step 8): `DB_URL`, `DB_USERNAME`, `KAFKA_BOOTSTRAP_SERVERS`, service URLs, `MAIL_FROM`, `APP_URL`, `CORS_ALLOWED_ORIGINS`.

**Check:** Parameter Store lists the seven parameters, all `SecureString`.

**Cost:** free (Standard tier).
