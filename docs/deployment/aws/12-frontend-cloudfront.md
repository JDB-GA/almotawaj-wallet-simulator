# Step 12: Frontend on S3 + CloudFront

[← Back to AWS](index.md)

**Goal:** the React frontend and the backend behind one HTTPS address, so the `Secure` login cookie works and CORS is not needed.

```text
Browser → CloudFront (HTTPS) ─┬─ /              → S3 (frontend files)
                              └─ /api/*, /auth/* → Elastic Beanstalk
```

The frontend reads the backend address from `VITE_API_URL` at build time ([`frontend/src/config/env.ts`](../../../frontend/src/config/env.ts)), so it is built after CloudFront exists.

## 12a. S3 bucket

S3 → Create bucket.

| Setting | Value | Why |
| --- | --- | --- |
| Type | General purpose | |
| Name | `almotawaj-frontend-481374029689` | Bucket names are global; the account number makes it unique |
| Region | `eu-north-1` | Same as everything else |
| Block all public access | On | Only CloudFront reads the files |

## 12b. CloudFront distribution

CloudFront → Create distribution.

| Setting | Value | Why |
| --- | --- | --- |
| Name | `almotawaj-cloudfront` | |
| Type | Single website or app | |
| Custom domain | Skipped for now | The wizard only connects Route 53 domains, and the free account plan blocks N. Virginia certificates. Added in Step 14 |
| Origin | Amazon S3 → `almotawaj-frontend-481374029689` | |
| Origin settings | Recommended (origin access control) | CloudFront reads the private bucket |
| WAF | Do not enable | About $14+ a month; Cloudflare already shields the domain |

Result:

| Value | |
| --- | --- |
| Distribution domain | `d38vn1o0eqiqa8.cloudfront.net` |
| Distribution ID | `E1PQXMBPMZPI7D` |

Bucket policy (S3 → bucket → Permissions) was added automatically: only this distribution may `s3:GetObject`.

The console warning "Missing permissions ... pricingplanmanager:ListSubscriptions" is harmless: the class account cannot view flat-rate plans. The distribution uses pay-as-you-go pricing.

**Cost:** 1 TB transfer and 10 million requests a month free.

## 12c. Backend origin

CloudFront → distribution → Origins → Create origin.

| Setting | Value | Why |
| --- | --- | --- |
| Origin domain | `almotawaj-prod.eba-jpqmjfvd.eu-north-1.elasticbeanstalk.com` (typed, not picked from the list) | The list offers Beanstalk's S3 bucket, which is the wrong origin |
| Protocol | HTTP only, port 80 | Beanstalk serves HTTP; users still get HTTPS from CloudFront |
| Name | `almotawaj-backend` | |

## 12d. Routes (behaviors)

| Precedence | Path | Origin | Cache policy | Origin request policy | Methods |
| --- | --- | --- | --- | --- | --- |
| 0 | `/api/*` | `almotawaj-backend` | `CachingDisabled` | `AllViewerExceptHostHeader` | All |
| 1 | `/auth/*` | `almotawaj-backend` | `CachingDisabled` | `AllViewerExceptHostHeader` | All |
| 2 | Default (`*`) | S3 frontend | `CachingOptimized` | | GET, HEAD |

All use **Redirect HTTP to HTTPS**.

| Choice | Why |
| --- | --- |
| `CachingDisabled` | API answers differ per user and must never be cached |
| `AllViewerExceptHostHeader` | Cookies, headers and query strings reach the backend, so login works |

**Correction:** `/auth/*` was first missing, then created with `UseOriginCacheControlHeaders`; changed to `CachingDisabled`.

## 12e. Page refresh (SPA routing)

React routes like `/wallet` are not files in S3, so a refresh would fail. A CloudFront Function serves `index.html` for any path without a file extension, on the frontend route only (API errors stay untouched).

CloudFront → Functions → Create function `almotawaj-spa-routing` (cloudfront-js-2.0) → Publish:

```javascript
function handler(event) {
    var request = event.request;
    if (!request.uri.includes('.')) {
        request.uri = '/index.html';
    }
    return request;
}
```

Attached to Behaviors → Default (`*`) → Function associations → **Viewer request**.

## 12f. Build and upload

Built inside `frontend/` (the local `.env` is not changed; this value applies to this build only):

```bash
VITE_API_URL=https://d38vn1o0eqiqa8.cloudfront.net pnpm build
```

`VITE_API_URL` is baked into the JavaScript, so every API call goes to the CloudFront address, which forwards `/api` and `/auth` to Beanstalk.

Upload: S3 → `almotawaj-frontend-481374029689` → Upload → the **contents** of `frontend/dist` (`index.html` at the top level, plus `assets/`), not the `dist` folder itself.

## Result and open issue

`https://d38vn1o0eqiqa8.cloudfront.net` loads the app over HTTPS.

Login fails ("Something went wrong"): the gateway's CORS filter blocks the new address.

| Request to `/auth/users/login` | Result |
| --- | --- |
| With `Origin: https://d38vn1o0eqiqa8.cloudfront.net` | 403 (blocked by CORS) |
| Without `Origin` | 400 (reaches the backend) |

Fix in [Step 14](index.md): set `CORS_ALLOWED_ORIGINS` and `APP_URL` to the final domain.
