# Step 14: Custom Domain `wallet.almotawaj.com`

[← Back to AWS](index.md)

**Goal:** frontend and backend on one HTTPS address, so the `Secure` login cookie works and no CORS is needed between them.

```text
https://wallet.almotawaj.com → CloudFront ─┬─ /              → S3 (frontend)
                                           └─ /api/*, /auth/* → Elastic Beanstalk
```

DNS for `almotawaj.com` is managed in **Cloudflare** (nameservers `mckenzie` and `garret.ns.cloudflare.com`). Records added in Vercel are ignored.

## 14a. Certificate (N. Virginia)

CloudFront only uses certificates from `us-east-1`, even though everything else runs in Stockholm.

Direct link: `https://us-east-1.console.aws.amazon.com/acm/home?region=us-east-1#/certificates/request`

| Setting | Value |
| --- | --- |
| Type | Public certificate |
| Domain | `wallet.almotawaj.com` |
| Validation | DNS |

The "Failed to list certificate authorities" error on that page is about private certificates and can be ignored.

### Validation record (Cloudflare)

| Type | Name | Target | Proxy |
| --- | --- | --- | --- |
| CNAME | `_476f7d1caa39cc4ee8ed38f64688ec97.wallet` | `_839f1856311c5752e47788a545e5bb9b.wzccmgtwzk.acm-validations.aws.` | DNS only |

Keep it: ACM uses it again to renew the certificate every year.

### Correction: CAA blocked Amazon

The first certificate failed: "failed validation due to a certificate authority authorization (CAA) error". The domain's CAA records allowed Let's Encrypt, Google, DigiCert, Sectigo, SSL.com and Comodo, but not Amazon.

Fix in Cloudflare:

| Type | Name | Tag | CA domain |
| --- | --- | --- | --- |
| CAA | `@` | Only allow specific hostnames (`issue`) | `amazon.com` |

A failed certificate never retries, so it was deleted and requested again (same validation record). Result: **Issued**.

Check:

```bash
dig +short CAA almotawaj.com
```

## 14b. Domain on CloudFront

CloudFront → distribution → General → Settings → Edit:

| Setting | Value |
| --- | --- |
| Alternate domain name | `wallet.almotawaj.com` |
| Custom SSL certificate | `wallet.almotawaj.com` (ACM, us-east-1) |

## 14c. Point `wallet` to CloudFront (Cloudflare)

| Type | Name | Target | Proxy |
| --- | --- | --- | --- |
| CNAME | `wallet` | `d38vn1o0eqiqa8.cloudfront.net` | **DNS only** |

A specific name beats the `*.almotawaj.com` wildcard (Vercel), so only `wallet` moves to AWS.

**Correction:** first saved as **Proxied**, which returned Cloudflare's addresses and a 404. Switched to **DNS only**.

Check:

```bash
dig +short wallet.almotawaj.com @mckenzie.ns.cloudflare.com
```

It must return `d38vn1o0eqiqa8.cloudfront.net.` A Mac may show the old answer for a few minutes; clear it with:

```bash
sudo dscacheutil -flushcache; sudo killall -HUP mDNSResponder
```

## 14d. Backend settings

Beanstalk → Configuration → Updates, monitoring, and logging → Environment properties (Plain text):

| Name | Value | Why |
| --- | --- | --- |
| `CORS_ALLOWED_ORIGINS` | `https://wallet.almotawaj.com` | The gateway blocked logins from any other origin (403) |
| `APP_URL` | `https://wallet.almotawaj.com` | Links in emails point to the live app |

## 14e. Frontend build

```bash
VITE_API_URL=https://wallet.almotawaj.com pnpm build
```

Built for the custom domain, not the CloudFront address: the page and the API must share one address, or the browser treats the login cookie as cross-site and drops it. From Step 15 on, Jenkins builds and uploads it.

`api.almotawaj.com` (in the local `frontend/.env`) is the old Google Cloud backend and is not used on AWS.

## Result

| Request | Result |
| --- | --- |
| `https://wallet.almotawaj.com/` | ✅ 200 |
| `https://wallet.almotawaj.com/api/vouchers` | ✅ 401 (backend through CloudFront) |
