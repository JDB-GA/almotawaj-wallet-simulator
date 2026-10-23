import { requestJson } from '@/lib/api/http-client'
import { CHECKOUT_ENDPOINTS } from '../constants/checkout.constants'
import { checkoutSchema, usableVouchersSchema } from '../schemas/checkout.schema'
import type { Checkout, UsableVoucher } from '../types/checkout.types'

export const checkoutApi = {
  get: (sessionId: string, signal?: AbortSignal): Promise<Checkout> =>
    requestJson(CHECKOUT_ENDPOINTS.detail(sessionId), checkoutSchema, { signal }),

  vouchers: (sessionId: string, signal?: AbortSignal): Promise<UsableVoucher[]> =>
    requestJson(CHECKOUT_ENDPOINTS.vouchers(sessionId), usableVouchersSchema, { signal }),

  pay: (sessionId: string, voucherId: string | null): Promise<Checkout> =>
    requestJson(CHECKOUT_ENDPOINTS.pay(sessionId), checkoutSchema, { method: 'POST', body: { voucherId } }),
}
