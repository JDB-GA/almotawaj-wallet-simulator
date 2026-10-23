import type { z } from 'zod'
import type { checkoutSchema, usableVoucherSchema } from '../schemas/checkout.schema'

export type Checkout = z.infer<typeof checkoutSchema>
export type UsableVoucher = z.infer<typeof usableVoucherSchema>
