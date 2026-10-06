import { requestJson, requestVoid } from '@/lib/api/http-client'
import { MERCHANT_ENDPOINTS } from '../constants/merchant.constants'
import { webhookSchema } from '../schemas/webhook.schema'
import type { Webhook, WebhookFormValues } from '../types/merchant.types'

export const webhookApi = {
  get: (signal?: AbortSignal): Promise<Webhook> => requestJson(MERCHANT_ENDPOINTS.webhook, webhookSchema, { signal }),

  set: (payload: WebhookFormValues): Promise<Webhook> =>
    requestJson(MERCHANT_ENDPOINTS.webhook, webhookSchema, { method: 'PUT', body: payload }),

  remove: (): Promise<void> => requestVoid(MERCHANT_ENDPOINTS.webhook, { method: 'DELETE' }),
}
