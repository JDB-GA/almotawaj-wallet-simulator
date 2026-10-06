import type { z } from 'zod'
import type { ALL_STATUSES_FILTER } from '../constants/merchant.constants'
import type { apiKeyCreatedSchema, apiKeySchema } from '../schemas/api-key.schema'
import type { checkoutSessionPageSchema, checkoutSessionSchema, checkoutStatusSchema } from '../schemas/checkout-session.schema'
import type { createApiKeySchema } from '../schemas/create-api-key.schema'
import type { createCheckoutSchema } from '../schemas/create-checkout.schema'
import type { webhookFormSchema, webhookSchema } from '../schemas/webhook.schema'

export type ApiKey = z.infer<typeof apiKeySchema>

export type ApiKeyCreated = z.infer<typeof apiKeyCreatedSchema>

export type CreateApiKeyValues = z.infer<typeof createApiKeySchema>

export type CheckoutStatus = z.infer<typeof checkoutStatusSchema>

export type CheckoutStatusFilter = CheckoutStatus | typeof ALL_STATUSES_FILTER

export type CheckoutSession = z.infer<typeof checkoutSessionSchema>

export type CheckoutSessionPage = z.infer<typeof checkoutSessionPageSchema>

export type Webhook = z.infer<typeof webhookSchema>

export type WebhookFormValues = z.infer<typeof webhookFormSchema>

export type CreateCheckoutInput = z.input<typeof createCheckoutSchema>

export type CreateCheckoutValues = z.output<typeof createCheckoutSchema>
