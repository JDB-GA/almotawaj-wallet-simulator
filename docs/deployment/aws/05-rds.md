# Step 5: RDS PostgreSQL

[← Back to AWS](index.md)

**Goal:** a managed, private PostgreSQL database for all services.

RDS → Create database.

| Setting | Value | Why |
| --- | --- | --- |
| Creation method | Standard create | Full control of settings |
| Engine | PostgreSQL 18.3 | Latest version; works with the app (local Docker uses 17) |
| Template | Free tier | Covered by credits |
| DB instance identifier | `almotawaj-db` | Name of the RDS instance |
| Master username | `almotawaj_admin` | Not the default `postgres` user name |
| Master password | Auto generated | Shown once after creation; kept in a password manager, never in the repo |
| Instance class | `db.t4g.micro` | Free tier, ARM |
| Public access | No (internet access gateway disabled) | Database is private |
| Security group | `almotawaj-db-sg` (`default` removed) | Only `almotawaj-app-sg` can reach port 5432 |
| Database name | `postgres` (built in) | No initial name was set at creation, and it cannot be added later; tables are created inside `postgres` on first start |

The security group was changed after creation: Modify → Connectivity → remove `default`, add `almotawaj-db-sg` → Apply immediately.

## Connection value for the services

```text
DB_URL=jdbc:postgresql://<RDS endpoint>:5432/postgres
DB_USERNAME=almotawaj_admin
DB_PASSWORD=<from password manager>
```

The endpoint is on the **Connectivity & security** tab. These values go to Secrets Manager in Step 7.

**Check:** status **Available**; security group `almotawaj-db-sg`; publicly accessible **No**.

**Cost:** free tier for 12 months (750 hours of `db.t4g.micro`, 20 GB storage); otherwise about $12 a month.
