import { useQuery } from '@tanstack/react-query'
import { payoutApi } from '../api/payout.api'
import { PAYOUT_QUERY_KEYS } from '../constants/voucher.constants'

export function usePayoutBatch(batchId: string | null) {
  return useQuery({
    queryKey: PAYOUT_QUERY_KEYS.batch(batchId ?? ''),
    queryFn: ({ signal }) => payoutApi.get(batchId ?? '', signal),
    enabled: batchId !== null,
  })
}
