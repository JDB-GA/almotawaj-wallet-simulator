import type { BadgeVariant } from '@/features/kyc/types/badge-variant.types'
import type { CheckoutStatus, CheckoutStatusFilter } from '../types/merchant.types'

const sessionPath = (sessionId: string) => `/api/merchant/checkout-sessions/${encodeURIComponent(sessionId)}`

export const MERCHANT_ENDPOINTS = {
  apiKeys: '/api/merchant/api-keys',
  apiKey: (keyId: string) => `/api/merchant/api-keys/${encodeURIComponent(keyId)}`,
  sessions: '/api/merchant/checkout-sessions',
  webhook: '/api/merchant/webhook',
  cancel: (sessionId: string) => `${sessionPath(sessionId)}/cancel`,
  refund: (sessionId: string) => `${sessionPath(sessionId)}/refund`,
} as const

export const MERCHANT_QUERY_KEYS = {
  apiKeys: ['merchant', 'api-keys'],
  webhook: ['merchant', 'webhook'],
  sessions: ['merchant', 'sessions'],
  sessionsPage: (status: CheckoutStatusFilter, page: number) => ['merchant', 'sessions', status, page],
} as const

export const CHECKOUT_STATUSES = ['PENDING', 'PAID', 'CANCELLED', 'EXPIRED', 'REFUNDED'] as const
export const ALL_STATUSES_FILTER = 'ALL'
export const CHECKOUT_STATUS_FILTERS: readonly CheckoutStatusFilter[] = [ALL_STATUSES_FILTER, ...CHECKOUT_STATUSES]

export const CHECKOUT_STATUS_BADGE_VARIANTS: Readonly<Record<CheckoutStatus, BadgeVariant>> = {
  PENDING: 'secondary',
  PAID: 'default',
  CANCELLED: 'outline',
  EXPIRED: 'outline',
  REFUNDED: 'destructive',
}

export const SESSIONS_PAGE_SIZE = 10
export const SESSIONS_SORT = 'createdAt,desc'
export const API_KEY_NAME_MAX_LENGTH = 50
export const ORDER_REFERENCE_MAX_LENGTH = 64
export const URL_MAX_LENGTH = 500
export const HTTP_URL_PATTERN = /^https?:\/\/\S+$/
export const ORDER_REFERENCE_PATTERN = /^[A-Za-z0-9._-]+$/
export const API_KEY_HEADER = 'X-API-Key'
