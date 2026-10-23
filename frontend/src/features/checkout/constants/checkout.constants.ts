const checkoutPath = (sessionId: string) => `/api/checkout/${encodeURIComponent(sessionId)}`

export const CHECKOUT_ENDPOINTS = {
  detail: checkoutPath,
  pay: (sessionId: string) => `${checkoutPath(sessionId)}/pay`,
  vouchers: (sessionId: string) => `${checkoutPath(sessionId)}/vouchers`,
} as const

export const RETURN_DELAY_MS = 3000

export const CHECKOUT_QUERY_KEYS = {
  detail: (sessionId: string) => ['checkout', sessionId],
  vouchers: (sessionId: string) => ['checkout', sessionId, 'vouchers'],
} as const
