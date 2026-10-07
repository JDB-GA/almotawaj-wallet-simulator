import { API_KEY_HEADER } from '@/features/merchant/constants/merchant.constants'

export const GATEWAY_PATH = '/api/gateway/checkout-sessions'
export const SAMPLE_API_KEY = 'almt_YOUR_API_KEY'
export const SAMPLE_SESSION_ID = '4c6b7166-e58a-487b-b789-966e640b9adc'
export const GUIDE_HEADER = API_KEY_HEADER

export const SIGNATURE_HEADER = 'X-Wallet-Signature'
export const EVENT_HEADER = 'X-Wallet-Event'

export const GUIDE_STEPS = ['createKey', 'createSession', 'redirect', 'return', 'confirm'] as const

export const CALLBACK_EVENTS = ['checkout.paid', 'checkout.cancelled', 'checkout.expired', 'checkout.refunded'] as const

export const GUIDE_ENDPOINTS = [
  { method: 'POST', path: GATEWAY_PATH, key: 'create' },
  { method: 'GET', path: `${GATEWAY_PATH}/{sessionId}`, key: 'get' },
  { method: 'POST', path: `${GATEWAY_PATH}/{sessionId}/cancel`, key: 'cancel' },
  { method: 'POST', path: `${GATEWAY_PATH}/{sessionId}/refund`, key: 'refund' },
] as const

export const REQUEST_FIELDS = [
  { name: 'orderReference', type: 'string', required: true },
  { name: 'amount', type: 'number', required: true },
  { name: 'description', type: 'string', required: false },
  { name: 'returnUrl', type: 'string', required: false },
  { name: 'expiresInMinutes', type: 'number', required: false },
] as const

export const RESPONSE_FIELDS = [
  'id',
  'orderReference',
  'amount',
  'description',
  'status',
  'payerName',
  'checkoutUrl',
  'returnUrl',
  'expiresAt',
  'paidAt',
  'refundedAt',
  'createdAt',
] as const

export const GUIDE_ERRORS = [
  { status: 400, code: 'VALIDATION_FAILED' },
  { status: 401, code: 'UNAUTHORIZED' },
  { status: 404, code: 'CHECKOUT_NOT_FOUND' },
  { status: 409, code: 'ORDER_ALREADY_EXISTS' },
  { status: 422, code: 'CHECKOUT_NOT_PENDING' },
  { status: 422, code: 'CHECKOUT_NOT_PAID' },
  { status: 422, code: 'INSUFFICIENT_BALANCE' },
  { status: 429, code: 'TOO_MANY_REQUESTS' },
] as const

export const GUIDE_RULES = ['amount', 'expiry', 'order', 'refund', 'rateLimit', 'secret', 'testing'] as const
