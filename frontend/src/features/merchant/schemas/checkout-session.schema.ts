import { z } from 'zod'
import { pageSchema } from '@/lib/api/page.schema'
import { CHECKOUT_STATUSES } from '../constants/merchant.constants'

export const checkoutStatusSchema = z.enum(CHECKOUT_STATUSES)

export const checkoutSessionSchema = z.object({
  id: z.uuid(),
  orderReference: z.string(),
  amount: z.number(),
  description: z.string().nullable(),
  status: checkoutStatusSchema,
  payerName: z.string().nullable(),
  checkoutUrl: z.url(),
  returnUrl: z.url().nullable(),
  expiresAt: z.iso.datetime({ offset: true }),
  paidAt: z.iso.datetime({ offset: true }).nullable(),
  refundedAt: z.iso.datetime({ offset: true }).nullable(),
  createdAt: z.iso.datetime({ offset: true }),
})

export const checkoutSessionPageSchema = pageSchema(checkoutSessionSchema)
