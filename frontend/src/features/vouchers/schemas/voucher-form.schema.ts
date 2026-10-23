import { z } from 'zod'
import { AMOUNT_PATTERN, TOP_UP_MAX, TOP_UP_MIN } from '@/features/wallet/constants/wallet.constants'
import { validationKey } from '@/i18n/keys'
import {
  PAYOUT_ITEMS_MAX,
  VOUCHER_PER_CLIENT_MAX,
  VOUCHER_PER_CLIENT_MIN,
  VOUCHER_QUANTITY_MAX,
  VOUCHER_QUANTITY_MIN,
  VOUCHER_TITLE_MAX_LENGTH,
  WHOLE_NUMBER_PATTERN,
} from '../constants/voucher.constants'

const amount = z
  .string()
  .trim()
  .min(1, validationKey('amountRequired'))
  .regex(AMOUNT_PATTERN, validationKey('amountInvalid'))
  .transform(Number)
  .pipe(z.number().min(TOP_UP_MIN, validationKey('amountTooSmall')).max(TOP_UP_MAX, validationKey('amountTooLarge')))

const wholeNumber = (min: number, max: number, key: Parameters<typeof validationKey>[0]) =>
  z
    .string()
    .trim()
    .regex(WHOLE_NUMBER_PATTERN, validationKey(key))
    .transform(Number)
    .pipe(z.number().min(min, validationKey(key)).max(max, validationKey(key)))

export const createVoucherSchema = z
  .object({
    title: z.string().trim().min(1, validationKey('voucherTitleRequired')).max(VOUCHER_TITLE_MAX_LENGTH, validationKey('voucherTitleTooLong')),
    price: amount,
    faceValue: amount,
    totalQuantity: wholeNumber(VOUCHER_QUANTITY_MIN, VOUCHER_QUANTITY_MAX, 'voucherQuantityInvalid'),
    perClientLimit: wholeNumber(VOUCHER_PER_CLIENT_MIN, VOUCHER_PER_CLIENT_MAX, 'voucherLimitInvalid'),
    expiresAt: z
      .string()
      .min(1, validationKey('dateRequired'))
      .refine((value) => new Date(value).getTime() > Date.now(), validationKey('voucherExpiryInPast')),
  })
  .refine((values) => values.price <= values.faceValue, { path: ['price'], message: validationKey('voucherPriceAboveFaceValue') })

export const payoutFormSchema = z.object({
  items: z
    .array(
      z.object({
        recipient: z.string().trim().min(1, validationKey('recipientRequired')),
        amount,
      }),
    )
    .min(1, validationKey('payoutItemsRequired'))
    .max(PAYOUT_ITEMS_MAX, validationKey('payoutItemsTooMany')),
})
