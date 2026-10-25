import type { z } from 'zod'
import type { createVoucherSchema, payoutFormSchema } from '../schemas/voucher-form.schema'
import type {
  payoutBatchPageSchema,
  payoutBatchSchema,
  payoutBatchStatusSchema,
  payoutItemSchema,
  payoutItemStatusSchema,
  payoutProgressNotificationSchema,
  voucherPageSchema,
  voucherPurchasePageSchema,
  voucherPurchaseSchema,
  voucherPurchaseStatusSchema,
  voucherSchema,
  voucherStatusSchema,
} from '../schemas/voucher.schema'

export type VoucherStatus = z.infer<typeof voucherStatusSchema>
export type PayoutBatchStatus = z.infer<typeof payoutBatchStatusSchema>
export type PayoutItemStatus = z.infer<typeof payoutItemStatusSchema>
export type VoucherPurchaseStatus = z.infer<typeof voucherPurchaseStatusSchema>
export type VoucherPurchaseDisplayStatus = VoucherPurchaseStatus | 'EXPIRED'

export type Voucher = z.infer<typeof voucherSchema>
export type VoucherPage = z.infer<typeof voucherPageSchema>
export type VoucherPurchase = z.infer<typeof voucherPurchaseSchema>
export type VoucherPurchasePage = z.infer<typeof voucherPurchasePageSchema>
export type PayoutItem = z.infer<typeof payoutItemSchema>
export type PayoutBatch = z.infer<typeof payoutBatchSchema>
export type PayoutBatchPage = z.infer<typeof payoutBatchPageSchema>
export type PayoutProgress = z.infer<typeof payoutProgressNotificationSchema>

export type CreateVoucherInput = z.input<typeof createVoucherSchema>
export type CreateVoucherValues = z.output<typeof createVoucherSchema>
export type PayoutFormInput = z.input<typeof payoutFormSchema>
export type PayoutFormValues = z.output<typeof payoutFormSchema>

export interface UpdateVoucherPayload {
  title: string
  price: number
  status: Extract<VoucherStatus, 'ACTIVE' | 'PAUSED'>
  saleEndsAt: string
  expiresAt: string | null
  version: number
}
