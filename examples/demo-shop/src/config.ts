const DEFAULT_PORT = 4000

const required = (name: string): string => {
  const value = process.env[name]
  if (!value) {
    throw new Error(`Set ${name} before starting the shop. See .env.example.`)
  }
  return value
}

const port = Number(process.env.PORT ?? DEFAULT_PORT)

export const config = {
  port,
  shopUrl: (process.env.SHOP_URL ?? `http://localhost:${String(port)}`).replace(/\/+$/, ''),
  apiUrl: (process.env.WALLET_API_URL ?? 'http://localhost:8080').replace(/\/+$/, ''),
  apiKey: required('WALLET_API_KEY'),
  signingSecret: process.env.WALLET_SIGNING_SECRET ?? null,
} as const

export const PATHS = {
  home: '/',
  buy: '/buy',
  orders: '/orders',
  complete: '/orders/complete',
  webhook: '/webhooks/wallet',
} as const

export const MAX_BODY_BYTES = 100_000
export const API_KEY_HEADER = 'X-API-Key'
export const SIGNATURE_HEADER = 'x-wallet-signature'
export const QUICK_EXPIRY_MINUTES = 1
