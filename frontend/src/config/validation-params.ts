import {
  EMAIL_MAX_LENGTH,
  MOBILE_NUMBER_LENGTH,
  OTP_LENGTH,
  PASSWORD_MAX_LENGTH,
  PASSWORD_MIN_LENGTH,
} from '@/features/auth/constants/auth.constants'
import {
  AREA_MAX_LENGTH,
  CPR_LENGTH,
  FULL_NAME_MAX_LENGTH,
  MAX_FILE_SIZE_MB,
  MINIMUM_AGE_YEARS,
} from '@/features/kyc/constants/kyc.constants'
import { REJECTION_REASON_MAX_LENGTH } from '@/features/kyc-review/constants/kyc-review.constants'
import { API_KEY_NAME_MAX_LENGTH, ORDER_REFERENCE_MAX_LENGTH } from '@/features/merchant/constants/merchant.constants'
import { DISPLAY_NAME_MAX_LENGTH, DISPLAY_NAME_MIN_LENGTH } from '@/features/profile/constants/profile.constants'
import {
  PAYOUT_ITEMS_MAX,
  VOUCHER_PER_CLIENT_MAX,
  VOUCHER_PER_CLIENT_MIN,
  VOUCHER_QUANTITY_MAX,
  VOUCHER_QUANTITY_MIN,
  VOUCHER_TITLE_MAX_LENGTH,
} from '@/features/vouchers/constants/voucher.constants'
import { TOP_UP_MAX, TOP_UP_MIN, TRANSFER_NOTE_MAX_LENGTH } from '@/features/wallet/constants/wallet.constants'

export const VALIDATION_PARAMS = {
  mobileLength: MOBILE_NUMBER_LENGTH,
  emailMax: EMAIL_MAX_LENGTH,
  passwordMin: PASSWORD_MIN_LENGTH,
  passwordMax: PASSWORD_MAX_LENGTH,
  otpLength: OTP_LENGTH,
  fullNameMax: FULL_NAME_MAX_LENGTH,
  cprLength: CPR_LENGTH,
  areaMax: AREA_MAX_LENGTH,
  minimumAge: MINIMUM_AGE_YEARS,
  maxFileSizeMb: MAX_FILE_SIZE_MB,
  rejectionReasonMax: REJECTION_REASON_MAX_LENGTH,
  topUpMin: TOP_UP_MIN,
  topUpMax: TOP_UP_MAX,
  noteMax: TRANSFER_NOTE_MAX_LENGTH,
  apiKeyNameMax: API_KEY_NAME_MAX_LENGTH,
  orderReferenceMax: ORDER_REFERENCE_MAX_LENGTH,
  businessNameMin: DISPLAY_NAME_MIN_LENGTH,
  businessNameMax: DISPLAY_NAME_MAX_LENGTH,
  voucherTitleMax: VOUCHER_TITLE_MAX_LENGTH,
  voucherQuantityMin: VOUCHER_QUANTITY_MIN,
  voucherQuantityMax: VOUCHER_QUANTITY_MAX,
  voucherLimitMin: VOUCHER_PER_CLIENT_MIN,
  voucherLimitMax: VOUCHER_PER_CLIENT_MAX,
  payoutItemsMax: PAYOUT_ITEMS_MAX,
} as const
