import { z } from 'zod'
import { checkoutStatusSchema } from '@/features/merchant/schemas/checkout-session.schema'

export const checkoutSchema = z.object({
  id: z.uuid(),
  merchantName: z.string(),
  orderReference: z.string(),
  amount: z.number(),
  voucherDiscount: z.number().nullable(),
  description: z.string().nullable(),
  status: checkoutStatusSchema,
  returnUrl: z.url().nullable(),
  expiresAt: z.iso.datetime({ offset: true }),
})

export const usableVoucherSchema = z.object({
  id: z.uuid(),
  title: z.string(),
  code: z.string(),
  faceValue: z.number(),
})

export const usableVouchersSchema = z.array(usableVoucherSchema)
