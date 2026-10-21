# Deployment

[← Back to the README](../../README.md)

![Deployment architecture](deployment-architecture.jpg)

## Server Deployment – Google Cloud Run

The API runs on **Cloud Run** as service `wallet-api` in project `almotawaj-wallet`, region `europe-west4` (Netherlands, a few milliseconds from the Frankfurt database, and one of the regions that supports Cloud Run custom domains), on the custom domain `api.almotawaj.com`. Cloud Build packs `/backend` into a container, so there is no Dockerfile. `application-prod.properties` ships inside the build; Cloud Run only provides the values it reads: plain settings as environment variables, secrets from **Secret Manager**.

### Database – Neon

Neon is serverless PostgreSQL: it suspends after about 5 idle minutes and wakes in about a second, so the free plan covers the demo. Cloud SQL would run 24/7 at roughly $9–10 a month.

1. In the [Neon console](https://console.neon.tech), create a project in **AWS Europe Central 1 (Frankfurt)** with Postgres 17+. Leave the extras (Auth, Data API, object storage) off: uploads go to Cloud Storage instead. The default database `neondb` with role `neondb_owner` is enough.
2. Open **Connect** and read the parameters (not the URL with the password inside):
   - `DB_URL` = `jdbc:postgresql://<host>/<database>?sslmode=require` (host looks like `ep-xxxx.eu-central-1.aws.neon.tech`; use the direct host, without `-pooler`)
   - `DB_USERNAME` = the role
   - `DB_PASSWORD` = the role password, stored only in Secret Manager (step 3)
3. Nothing else is needed: Hibernate creates the tables on first start (`ddl-auto=update`), and `POST /api/seed` adds demo data.

### Google Cloud

**1. Project and billing** (once):

```bash
gcloud projects create almotawaj-wallet --name="Almotawaj Wallet"
gcloud billing projects link almotawaj-wallet --billing-account=<billing-account-id>
gcloud config set project almotawaj-wallet
gcloud services enable run.googleapis.com cloudbuild.googleapis.com artifactregistry.googleapis.com secretmanager.googleapis.com
```

**2. A dedicated runtime identity.** The service runs as its own service account with no project roles, instead of the default compute account:

```bash
gcloud iam service-accounts create wallet-api --display-name="Wallet API runtime"
```

**3. Secrets.** Generated ones never appear on screen:

```bash
openssl rand -hex 32 | tr -d '\n' | gcloud secrets create JWT_SECRET --data-file=-
openssl rand -hex 32 | tr -d '\n' | gcloud secrets create OTP_SECRET --data-file=-
openssl rand -hex 24 | tr -d '\n' | gcloud secrets create SEED_TOKEN --data-file=-
```

Typed ones: run each, paste the value, then press Ctrl+D twice (no Enter, so no trailing newline):

```bash
gcloud secrets create DB_PASSWORD --data-file=-
gcloud secrets create RESEND_API_KEY --data-file=-
gcloud secrets create SEED_PASSWORD --data-file=-
```

Grant the service account read access to each secret separately, not project-wide:

```bash
for s in JWT_SECRET OTP_SECRET SEED_TOKEN DB_PASSWORD RESEND_API_KEY SEED_PASSWORD; do
  gcloud secrets add-iam-policy-binding $s --member="serviceAccount:wallet-api@almotawaj-wallet.iam.gserviceaccount.com" --role=roles/secretmanager.secretAccessor
done
```

**4. Uploads bucket.** KYC documents and profile pictures go to a private Cloud Storage bucket mounted into the container at `/data`, so they survive restarts and redeploys with no code change (the app's default `STORAGE_ROOT` is `/data/uploads`):

```bash
gcloud storage buckets create gs://almotawaj-wallet-uploads --location europe-west4 --uniform-bucket-level-access --public-access-prevention
gcloud storage buckets add-iam-policy-binding gs://almotawaj-wallet-uploads --member="serviceAccount:wallet-api@almotawaj-wallet.iam.gserviceaccount.com" --role=roles/storage.objectUser
```

Public access is blocked on the bucket; files are only served through the API, which checks who is asking.

**5. Deploy** from the repository root:

```bash
gcloud run deploy wallet-api --source backend --region europe-west4 --service-account wallet-api@almotawaj-wallet.iam.gserviceaccount.com \
  --allow-unauthenticated --ingress all --min-instances 1 --max-instances 2 --memory 1Gi --execution-environment gen2 \
  --add-volume name=uploads,type=cloud-storage,bucket=almotawaj-wallet-uploads --add-volume-mount volume=uploads,mount-path=/data \
  --set-env-vars "^;^SPRING_PROFILES_ACTIVE=prod;DB_URL=jdbc:postgresql://<host>/<database>?sslmode=require;DB_USERNAME=<role>;CORS_ALLOWED_ORIGINS=https://wallet.almotawaj.com,https://www.wallet.almotawaj.com;APP_URL=https://wallet.almotawaj.com;MAIL_FROM=Almotawaj Wallet <no-reply@almotawaj.com>" \
  --set-secrets "DB_PASSWORD=DB_PASSWORD:latest,JWT_SECRET=JWT_SECRET:latest,OTP_SECRET=OTP_SECRET:latest,RESEND_API_KEY=RESEND_API_KEY:latest,SEED_TOKEN=SEED_TOKEN:latest,SEED_PASSWORD=SEED_PASSWORD:latest"
```

`--allow-unauthenticated` is required because the API is public; the app itself enforces JWT, API-key and role checks. `--max-instances 2` caps cost and keeps the in-memory rate limiter meaningful.

The `^;^` prefix makes `;` the separator, because `CORS_ALLOWED_ORIGINS` itself contains a comma. `--min-instances 1` keeps one instance warm so the demo never waits for Spring Boot to start; `--memory 1Gi` gives the JVM room.

**6. Map the domain.** `almotawaj.com` is already verified for the Google account (`gcloud domains list-user-verified`; otherwise run `gcloud domains verify almotawaj.com`). Domain mappings need the beta component:

```bash
gcloud components install beta
gcloud beta run domain-mappings create --service wallet-api --domain api.almotawaj.com --region europe-west4
```

Then, in **Vercel → Domains → almotawaj.com → DNS Records** (Vercel hosts the DNS), set `api` to `CNAME ghs.googlehosted.com.` and remove any older `api` record. Google issues the HTTPS certificate on its own once DNS resolves, usually within 15–60 minutes. Check with:

```bash
gcloud beta run domain-mappings describe --domain api.almotawaj.com --region europe-west4 --format="value(status.conditions)"
```

Until then the API is reachable at its `*.run.app` URL (`gcloud run services describe wallet-api --region europe-west4 --format="value(status.url)"`). The web app must use `https://api.almotawaj.com`, not the `run.app` URL, because the login cookie is `SameSite=Strict`.

| Variable                       | Source         | Value                                                                                      |
| ------------------------------ | -------------- | ------------------------------------------------------------------------------------------ |
| `SPRING_PROFILES_ACTIVE`       | env var        | `prod`                                                                                     |
| `DB_URL`                       | env var        | `jdbc:postgresql://<host>.neon.tech/<db>?sslmode=require`                                  |
| `DB_USERNAME`                  | env var        | Neon role                                                                                  |
| `DB_PASSWORD`                  | Secret Manager | Neon password                                                                              |
| `JWT_SECRET` / `OTP_SECRET`    | Secret Manager | two different `openssl rand -hex 32` values                                                |
| `JWT_EXPIRATION_MS`            | env var        | optional, default `86400000` (24 h)                                                        |
| `CORS_ALLOWED_ORIGINS`         | env var        | `https://wallet.almotawaj.com,https://www.wallet.almotawaj.com`                            |
| `RESEND_API_KEY`               | Secret Manager | Resend key                                                                                 |
| `MAIL_FROM`                    | env var        | verified Resend sender                                                                     |
| `MAIL_LOGO_URL`                | env var        | optional logo URL for emails                                                               |
| `APP_URL`                      | env var        | `https://wallet.almotawaj.com` (email links)                                               |
| `STORAGE_ROOT`                 | –              | leave unset: default `/data/uploads` is the mounted bucket                                 |
| `SEED_TOKEN` / `SEED_PASSWORD` | Secret Manager | optional, enables `POST /api/seed` (see the [backend guide](../../backend/README.md#seeding)) |
| `WEBHOOK_ALLOW_PRIVATE_HOSTS`  | –              | leave unset in production; `true` only allows callbacks to local addresses in development  |

Cloud Run sets `PORT` itself. The bucket mount needs the `gen2` execution environment.

### Running the live API

All commands assume `gcloud config set project almotawaj-wallet`.

**Service URL and status:**

```bash
gcloud run services describe wallet-api --region europe-west4 --format="value(status.url,status.conditions[0].status)"
```

**Logs** (startup, errors, requests):

```bash
gcloud run services logs read wallet-api --region europe-west4 --limit 100
gcloud beta run services logs tail wallet-api --region europe-west4
```

**Seed the demo data** once after the first deploy. The token and the demo password live only in Secret Manager:

```bash
gcloud secrets versions access latest --secret SEED_TOKEN
gcloud secrets versions access latest --secret SEED_PASSWORD
curl -X POST https://api.almotawaj.com/api/seed -H "Authorization: Bearer $(gcloud secrets versions access latest --secret SEED_TOKEN)"
```

`201` means the demo accounts exist; every one of them signs in with `SEED_PASSWORD` (accounts listed in the [backend guide](../../backend/README.md#seeding)). To switch seeding off, remove the secret from the service: `gcloud run services update wallet-api --region europe-west4 --remove-secrets SEED_TOKEN`.

**Redeploy after code changes:** run the deploy command from step 5 again. Settings and secrets stay unless the command changes them, so a code-only redeploy can be shortened to:

```bash
gcloud run deploy wallet-api --source backend --region europe-west4
```

**Change a setting:** `gcloud run services update wallet-api --region europe-west4 --update-env-vars KEY=value`.

**Rotate a secret:** add a new version, then roll a new revision so it is read:

```bash
gcloud secrets versions add JWT_SECRET --data-file=-
gcloud run services update wallet-api --region europe-west4 --update-labels rotated=$(date +%s)
```

Rotating `JWT_SECRET` signs everyone out; rotating `DB_PASSWORD` must happen in Neon first (**Roles → Reset password**).

**Uploaded files:** `gcloud storage ls -r gs://almotawaj-wallet-uploads/uploads/`.

**Stop paying while idle:** `gcloud run services update wallet-api --region europe-west4 --min-instances 0` (the next request then waits for Spring Boot to start).

### Running the database

| Task                   | Where                                                                                                              |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------ |
| Query the data         | Neon console → **SQL Editor**, or `psql "<connection string>"`                                                     |
| Back up / restore      | Neon console → **Branches** (point-in-time restore within the history window)                                      |
| Reset the demo         | `DELETE /api/admin/seed-data` as the super admin, then seed again                                                  |
| Change the password    | Neon console → **Roles** → reset, then `gcloud secrets versions add DB_PASSWORD --data-file=-` and roll a revision |
| Usage and suspend time | Neon console → **Monitoring**; the compute suspends after 5 idle minutes                                           |

The API connects to the direct host (no `-pooler`) with `sslmode=require`; the database name is `neondb` and the role `neondb_owner`.

## Web App – Vercel

Root directory `frontend`, ignored build step: _only build production_, domain `wallet.almotawaj.com`): set `VITE_API_URL=https://api.almotawaj.com`. `frontend/vercel.json` provides the SPA rewrite and security headers (CSP, HSTS, frame, referrer and permissions policies). API and web app share `almotawaj.com`, so the `SameSite=Strict` cookie works across them.

## Sample shop – Vercel

The [sample shop](../../examples/demo-shop/README.md) is a second Vercel project from the same repository, on `demo-shop.almotawaj.com`. It is a merchant website of its own: its server calls `https://api.almotawaj.com` with an API key, and customers are redirected to `wallet.almotawaj.com` to pay.

1. On `wallet.almotawaj.com`, sign in as the merchant, create an API key on **API Keys**, set the callback URL to `https://demo-shop.almotawaj.com/webhooks/wallet` and copy the signing secret.
2. In Vercel, add a new project from this repository with root directory `examples/demo-shop` and framework preset **Other**. Leave the build and output settings empty: Vercel detects `src/server.ts` and runs it as a function.
3. Set the variables below, deploy, and add the domain `demo-shop.almotawaj.com`.

| Variable                | Value                             |
| ----------------------- | --------------------------------- |
| `WALLET_API_URL`        | `https://api.almotawaj.com`       |
| `WALLET_API_KEY`        | the API key from step 1           |
| `WALLET_SIGNING_SECRET` | the signing secret from step 1    |
| `SHOP_URL`              | `https://demo-shop.almotawaj.com` |

Nothing changes on the backend: the shop calls the API from its server, so it needs no CORS entry, and callbacks reach it because it has a public `https` address. Revoking the API key on the wallet turns the shop's payments off.
