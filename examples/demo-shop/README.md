# Sample shop

[← Back to the README](../../README.md)

A tiny store that takes payments through the wallet's payment gateway, the same way an outside merchant's website would: it creates a checkout session with an API key, sends the customer to the checkout page, shows a result page when they come back, and accepts signed callbacks. It has no dependencies.

## Run it

Needs Node.js 22.18 or newer. Sign in to the wallet as a merchant, create an API key on **API Keys**, then:

```bash
cd examples/demo-shop
WALLET_API_KEY=almt_your_key node src/server.ts
```

Open http://localhost:4000. Other settings are in `.env.example`; the defaults point at a local backend on port 8080.

To receive callbacks, set the callback URL to `http://localhost:4000/webhooks/wallet` on the **API Keys** page and start the shop with `WALLET_SIGNING_SECRET` as well. A local backend only calls `localhost` when `webhook-allow-private-hosts=true` is in `application-dev.properties`.

## Try every outcome

| To see | Do this |
| --- | --- |
| `PAID` – "Payment received" | Buy a product and pay as a KYC-approved client. You are returned to the shop. |
| `PENDING` – "Payment not completed" | Buy a product, then use "Return to localhost:4000" on the checkout page without paying. |
| `CANCELLED` | Buy a product, open **Orders** in the shop and press **Cancel**. |
| `EXPIRED` | Tick "Expire in 1 minute" before buying, wait a minute, then open the payment link or **Orders**. |
| `REFUNDED` | Pay an order, then press **Refund** on **Orders**. |
| `401` | Start the shop with a wrong or revoked `WALLET_API_KEY` and buy something. |
| `422 CHECKOUT_EXPIRED` | Try to pay an expired payment link. |
| `422 INSUFFICIENT_BALANCE` | Pay with a client whose balance is lower than the price, or refund after the merchant has moved the money out. |
| `429` | Send more than 120 gateway requests in a minute. |

Failed gateway calls show the status and error code on the shop's error page. Orders are kept in memory and are lost when the shop restarts.
