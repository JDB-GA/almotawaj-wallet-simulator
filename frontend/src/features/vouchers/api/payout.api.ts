import { requestJson } from '@/lib/api/http-client'
import { CREATED_AT_DESC, PAYOUTS_PAGE_SIZE, VOUCHER_ENDPOINTS } from '../constants/voucher.constants'
import { payoutBatchPageSchema, payoutBatchSchema } from '../schemas/voucher.schema'
import type { PayoutBatch, PayoutBatchPage, PayoutFormValues } from '../types/voucher.types'

export const payoutApi = {
  list: (page: number, signal?: AbortSignal): Promise<PayoutBatchPage> =>
    requestJson(
      `${VOUCHER_ENDPOINTS.payouts}?${new URLSearchParams({ page: String(page), size: String(PAYOUTS_PAGE_SIZE), sort: CREATED_AT_DESC })}`,
      payoutBatchPageSchema,
      { signal },
    ),

  get: (batchId: string, signal?: AbortSignal): Promise<PayoutBatch> =>
    requestJson(VOUCHER_ENDPOINTS.payout(batchId), payoutBatchSchema, { signal }),

  create: (values: PayoutFormValues): Promise<PayoutBatch> =>
    requestJson(VOUCHER_ENDPOINTS.payouts, payoutBatchSchema, { method: 'POST', body: values }),
}
