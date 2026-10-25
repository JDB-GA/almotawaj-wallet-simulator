import type { BadgeVariant } from '@/features/kyc/types/badge-variant.types'
import type { PayoutBatchStatus, PayoutItemStatus, VoucherPurchaseDisplayStatus, VoucherStatus } from '../types/voucher.types'

export const VOUCHER_ENDPOINTS = {
  available: '/api/vouchers',
  purchases: '/api/vouchers/purchases',
  purchase: (voucherId: string) => `/api/vouchers/${encodeURIComponent(voucherId)}/purchase`,
  merchantVouchers: '/api/merchant/vouchers',
  merchantVoucher: (voucherId: string) => `/api/merchant/vouchers/${encodeURIComponent(voucherId)}`,
  payouts: '/api/merchant/payouts',
  payout: (batchId: string) => `/api/merchant/payouts/${encodeURIComponent(batchId)}`,
} as const

export const VOUCHER_QUERY_KEYS = {
  all: ['vouchers'],
  available: (page: number) => ['vouchers', 'available', page],
  purchases: (page: number) => ['vouchers', 'purchases', page],
  merchant: (page: number) => ['vouchers', 'merchant', page],
} as const

export const PAYOUT_QUERY_KEYS = {
  all: ['payouts'],
  page: (page: number) => ['payouts', 'page', page],
  batch: (batchId: string) => ['payouts', 'batch', batchId],
} as const

export const VOUCHER_STATUSES = ['ACTIVE', 'PAUSED', 'SOLD_OUT', 'EXPIRED'] as const
export const PAYOUT_BATCH_STATUSES = ['PROCESSING', 'COMPLETED', 'PARTIALLY_FAILED'] as const
export const PAYOUT_ITEM_STATUSES = ['PENDING', 'PAID', 'FAILED'] as const
export const VOUCHER_PURCHASE_STATUSES = ['UNUSED', 'REDEEMED'] as const
export const PAYOUT_PROCESSING_ERROR = 'PROCESSING_ERROR'
export const PAYOUT_FAILURE_REASONS = ['INSUFFICIENT_BALANCE', 'RECIPIENT_UNAVAILABLE', PAYOUT_PROCESSING_ERROR] as const

export const VOUCHER_BADGE_VARIANTS: Readonly<Record<VoucherStatus, BadgeVariant>> = {
  ACTIVE: 'default',
  PAUSED: 'secondary',
  SOLD_OUT: 'destructive',
  EXPIRED: 'outline',
}

export const PAYOUT_BATCH_BADGE_VARIANTS: Readonly<Record<PayoutBatchStatus, BadgeVariant>> = {
  PROCESSING: 'secondary',
  COMPLETED: 'default',
  PARTIALLY_FAILED: 'destructive',
}

export const PAYOUT_ITEM_BADGE_VARIANTS: Readonly<Record<PayoutItemStatus, BadgeVariant>> = {
  PENDING: 'secondary',
  PAID: 'default',
  FAILED: 'destructive',
}

export const VOUCHER_PURCHASE_BADGE_VARIANTS: Readonly<Record<VoucherPurchaseDisplayStatus, BadgeVariant>> = {
  UNUSED: 'default',
  REDEEMED: 'secondary',
  EXPIRED: 'outline',
}

export const VOUCHERS_PAGE_SIZE = 12
export const PURCHASES_PAGE_SIZE = 10
export const PAYOUTS_PAGE_SIZE = 10
export const CREATED_AT_DESC = 'createdAt,desc'
export const IDEMPOTENCY_HEADER = 'Idempotency-Key'
export const NETWORK_ERROR_KEY = 'network'
export const VOUCHER_TITLE_MAX_LENGTH = 70
export const VOUCHER_QUANTITY_MIN = 1
export const VOUCHER_QUANTITY_MAX = 10000
export const VOUCHER_PER_CLIENT_MIN = 1
export const VOUCHER_PER_CLIENT_MAX = 100
export const PAYOUT_ITEMS_MAX = 100
export const LOW_STOCK_THRESHOLD = 5
export const DEFAULT_VOUCHER_SALE_DAYS = 30
export const QUANTITY_PLACEHOLDER = '100'
export const WHOLE_NUMBER_PATTERN = /^\d+$/
export const SHOP_TAB = 'shop'
export const MINE_TAB = 'mine'
