# Sample shop

[← Back to the README](../../README.md)

A small store that takes payments through the wallet's payment gateway, the same way an outside merchant's website would. It is a separate website that only talks to the wallet API with a merchant API key. Live: https://demo-shop.almotawaj.com

## How it works

1. The customer presses **Pay with Digital Wallet**; the shop's server calls `POST /api/gateway/checkout-sessions` with its API key and gets a `checkoutUrl`.
2. The customer is redirected there, signs in to the wallet and pays, then is sent back to `/orders/complete?sessionId=…`.
3. The shop asks the gateway for the session and shows the result; it never trusts the browser for the payment status.
4. The wallet also posts a signed callback to `/webhooks/wallet`, which the shop verifies and logs.

The API key and signing secret stay on the shop's server. The shop has no database: the ids of a visitor's last 10 orders are kept for 7 days in an `HttpOnly`, `SameSite=Lax` cookie, every status is read from the gateway, and a visitor can only open, cancel or refund orders in their own cookie.

## Run it locally

Needs Node.js 22.18 or newer and nothing installed (`pnpm install && pnpm typecheck` only checks the types). Sign in to the wallet as a merchant, create an API key on **API Keys**, then:

```bash
cd examples/demo-shop
cp .env.example .env
node src/server.ts
```

Put the key in `.env` before starting (the file is gitignored), then open http://localhost:4000.

| Variable                | Default                 | Meaning                                                            |
| ----------------------- | ----------------------- | ------------------------------------------------------------------ |
| `WALLET_API_KEY`        | required                | The merchant API key, shown once when it is created                |
| `WALLET_API_URL`        | `http://localhost:8080` | Address of the wallet API                                          |
| `SHOP_URL`              | `http://localhost:4000` | Public address of the shop, used for the return address            |
| `WALLET_SIGNING_SECRET` | none                    | Signing secret from **API Keys**; without it callbacks are refused |
| `PORT`                  | `4000`                  | Local port                                                         |

For local callbacks, set the callback URL to `http://localhost:4000/webhooks/wallet` on **API Keys** and add `webhook-allow-private-hosts=true` to the backend's `application-dev.properties`. Deploying to Vercel is in the [deployment guide](../../docs/deployment/deployment.md#sample-shop--vercel).

## Code

[`src/gateway.ts`](src/gateway.ts) calls the wallet API and checks callback signatures, [`src/server.ts`](src/server.ts) is the web server, [`src/pages.ts`](src/pages.ts) holds the HTML and [`src/shop.ts`](src/shop.ts) the settings and products.

| Page or endpoint                             | What it does                                                |
| -------------------------------------------- | ----------------------------------------------------------- |
| `GET /`                                      | Products                                                    |
| `POST /buy`                                  | Creates a checkout session and redirects to the wallet      |
| `GET /orders/complete`                       | Result page the customer returns to                         |
| `GET /orders`                                | The orders placed from this browser, with their live status |
| `POST /orders/{sessionId}/cancel`, `/refund` | Cancels a pending order, refunds a paid one                 |
| `POST /webhooks/wallet`                      | Receives signed callbacks                                   |

## Try every outcome

| To see                     | Do this                                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------------------ |
| `PAID`                     | Buy a product and pay as a KYC-approved client; you are returned to "Payment received"           |
| `PENDING`                  | Buy, then use the return link on the checkout page without paying ("Payment not completed")      |
| `CANCELLED`                | Buy, open **Orders** and press **Cancel**                                                        |
| `EXPIRED`                  | Tick "Expire in 1 minute" before buying, wait a minute, then open the payment link or **Orders** |
| `REFUNDED`                 | Pay an order, then press **Refund** on **Orders**                                                |
| `401`                      | Start the shop with a wrong or revoked `WALLET_API_KEY` and buy something                        |
| `422 CHECKOUT_EXPIRED`     | Try to pay an expired payment link                                                               |
| `422 INSUFFICIENT_BALANCE` | Pay with a client whose balance is too low, or refund after the merchant moved the money out     |
| `429`                      | Send more than 120 gateway requests in a minute                                                  |

Failed gateway calls show the status and error code on the shop's error page. Accepted callbacks are logged as `callback checkout.paid for SHOP-…: PAID` (the terminal locally, the project's **Logs** on Vercel).
