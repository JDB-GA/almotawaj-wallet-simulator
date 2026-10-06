import { z } from 'zod'
import { AMOUNT_PATTERN, TOP_UP_MAX, TOP_UP_MIN, TRANSFER_NOTE_MAX_LENGTH } from '@/features/wallet/constants/wallet.constants'
import { validationKey } from '@/i18n/keys'
import { HTTP_URL_PATTERN, ORDER_REFERENCE_MAX_LENGTH, ORDER_REFERENCE_PATTERN, URL_MAX_LENGTH } from '../constants/merchant.constants'

export const createCheckoutSchema = z.object({
  orderReference: z
    .string()
    .trim()
    .min(1, validationKey('orderReferenceRequired'))
    .max(ORDER_REFERENCE_MAX_LENGTH, validationKey('orderReferenceInvalid'))
    .regex(ORDER_REFERENCE_PATTERN, validationKey('orderReferenceInvalid')),
  amount: z
    .string()
    .trim()
    .min(1, validationKey('amountRequired'))
    .regex(AMOUNT_PATTERN, validationKey('amountInvalid'))
    .transform(Number)
    .pipe(z.number().min(TOP_UP_MIN, validationKey('amountTooSmall')).max(TOP_UP_MAX, validationKey('amountTooLarge'))),
  description: z.string().trim().max(TRANSFER_NOTE_MAX_LENGTH, validationKey('noteTooLong')),
  returnUrl: z
    .string()
    .trim()
    .max(URL_MAX_LENGTH, validationKey('urlInvalid'))
    .refine((value) => value === '' || HTTP_URL_PATTERN.test(value), validationKey('urlInvalid')),
})
