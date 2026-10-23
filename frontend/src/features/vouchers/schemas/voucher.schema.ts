import { z } from 'zod'
import { pageSchema } from '@/lib/api/page.schema'
import { PAYOUT_BATCH_STATUSES, PAYOUT_ITEM_STATUSES, VOUCHER_PURCHASE_STATUSES, VOUCHER_STATUSES } from '../constants/voucher.constants'

export const voucherStatusSchema = z.enum(VOUCHER_STATUSES)
export const payoutBatchStatusSchema = z.enum(PAYOUT_BATCH_STATUSES)
export const payoutItemStatusSchema = z.enum(PAYOUT_ITEM_STATUSES)
export const voucherPurchaseStatusSchema = z.enum(VOUCHER_PURCHASE_STATUSES)

export const voucherSchema = z.object({
  id: z.uuid(),
  merchantName: z.string(),
  title: z.string(),
  price: z.number(),
  faceValue: z.number(),
  totalQuantity: z.number().int(),
  remaining: z.number().int(),
  perClientLimit: z.number().int(),
  status: voucherStatusSchema,
  expiresAt: z.iso.datetime({ offset: true }),
  version: z.number().int(),
  createdAt: z.iso.datetime({ offset: true }),
})

export const voucherPurchaseSchema = z.object({
  id: z.uuid(),
  voucherId: z.uuid(),
  title: z.string(),
  merchantName: z.string(),
  code: z.string(),
  price: z.number(),
  faceValue: z.number(),
  transactionReference: z.string(),
  status: voucherPurchaseStatusSchema,
  redeemedAt: z.iso.datetime({ offset: true }).nullable(),
  createdAt: z.iso.datetime({ offset: true }),
})

export const payoutItemSchema = z.object({
  id: z.uuid(),
  recipientName: z.string(),
  amount: z.number(),
  status: payoutItemStatusSchema,
  failureReason: z.string().nullable(),
  transactionReference: z.string().nullable(),
  processedAt: z.iso.datetime({ offset: true }).nullable(),
})

export const payoutBatchSchema = z.object({
  id: z.uuid(),
  status: payoutBatchStatusSchema,
  itemCount: z.number().int(),
  paidCount: z.number().int(),
  failedCount: z.number().int(),
  totalAmount: z.number(),
  createdAt: z.iso.datetime({ offset: true }),
  completedAt: z.iso.datetime({ offset: true }).nullable(),
  items: z.array(payoutItemSchema),
})

export const voucherPageSchema = pageSchema(voucherSchema)
export const voucherPurchasePageSchema = pageSchema(voucherPurchaseSchema)
export const payoutBatchPageSchema = pageSchema(payoutBatchSchema)

export const voucherStockNotificationSchema = z.object({ voucherId: z.uuid(), remaining: z.number().int() })

export const voucherSoldNotificationSchema = z.object({
  voucherId: z.uuid(),
  title: z.string(),
  price: z.number(),
  remaining: z.number().int(),
})

export const payoutProgressNotificationSchema = z.object({
  batchId: z.uuid(),
  status: payoutBatchStatusSchema,
  paid: z.number().int(),
  failed: z.number().int(),
  total: z.number().int(),
})
