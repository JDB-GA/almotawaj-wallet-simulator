import { PAYOUT_FAILURE_REASONS } from '../constants/voucher.constants'

type PayoutFailureReason = (typeof PAYOUT_FAILURE_REASONS)[number]

export const isPayoutFailureReason = (value: string): value is PayoutFailureReason =>
  PAYOUT_FAILURE_REASONS.some((reason) => reason === value)
