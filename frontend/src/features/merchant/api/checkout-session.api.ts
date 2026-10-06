import { requestJson } from '@/lib/api/http-client'
import { ALL_STATUSES_FILTER, MERCHANT_ENDPOINTS, SESSIONS_PAGE_SIZE, SESSIONS_SORT } from '../constants/merchant.constants'
import { checkoutSessionPageSchema, checkoutSessionSchema } from '../schemas/checkout-session.schema'
import type { CheckoutSession, CheckoutSessionPage, CheckoutStatusFilter, CreateCheckoutValues } from '../types/merchant.types'

const toSearchParams = (status: CheckoutStatusFilter, page: number): string => {
  const params = new URLSearchParams({ page: String(page), size: String(SESSIONS_PAGE_SIZE), sort: SESSIONS_SORT })
  if (status !== ALL_STATUSES_FILTER) {
    params.set('status', status)
  }
  return params.toString()
}

export const checkoutSessionApi = {
  list: (status: CheckoutStatusFilter, page: number, signal?: AbortSignal): Promise<CheckoutSessionPage> =>
    requestJson(`${MERCHANT_ENDPOINTS.sessions}?${toSearchParams(status, page)}`, checkoutSessionPageSchema, { signal }),

  create: ({ description, returnUrl, ...payload }: CreateCheckoutValues): Promise<CheckoutSession> =>
    requestJson(MERCHANT_ENDPOINTS.sessions, checkoutSessionSchema, {
      method: 'POST',
      body: { ...payload, description: description || null, returnUrl: returnUrl || null },
    }),

  cancel: (sessionId: string): Promise<CheckoutSession> =>
    requestJson(MERCHANT_ENDPOINTS.cancel(sessionId), checkoutSessionSchema, { method: 'POST' }),

  refund: (sessionId: string): Promise<CheckoutSession> =>
    requestJson(MERCHANT_ENDPOINTS.refund(sessionId), checkoutSessionSchema, { method: 'POST' }),
}
