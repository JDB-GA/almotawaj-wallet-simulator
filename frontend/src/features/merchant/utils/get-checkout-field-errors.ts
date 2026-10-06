import { getApiFieldErrors } from '@/features/wallet/utils/get-api-field-errors'
import type { CreateCheckoutInput } from '../types/merchant.types'

type CheckoutField = keyof CreateCheckoutInput

const CHECKOUT_FIELDS: readonly CheckoutField[] = ['orderReference', 'amount', 'description', 'returnUrl']

const CHECKOUT_CODE_ERRORS = {
  ORDER_ALREADY_EXISTS: { field: 'orderReference', key: 'orderReferenceUsed' },
} as const

export const getCheckoutFieldErrors = (error: unknown) =>
  getApiFieldErrors<CheckoutField>(error, CHECKOUT_CODE_ERRORS, CHECKOUT_FIELDS)
