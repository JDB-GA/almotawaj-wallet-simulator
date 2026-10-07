import { z } from 'zod'
import { validationKey } from '@/i18n/keys'
import { HTTP_URL_PATTERN, URL_MAX_LENGTH } from '../constants/merchant.constants'

export const webhookSchema = z.object({
  url: z.url().nullable(),
  signingSecret: z.string().nullable(),
  updatedAt: z.iso.datetime({ offset: true }).nullable(),
})

export const webhookFormSchema = z.object({
  url: z
    .string()
    .trim()
    .min(1, validationKey('callbackUrlRequired'))
    .max(URL_MAX_LENGTH, validationKey('callbackUrlInvalid'))
    .regex(HTTP_URL_PATTERN, validationKey('callbackUrlInvalid')),
})
