import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { payoutApi } from '../api/payout.api'
import { PAYOUT_QUERY_KEYS } from '../constants/voucher.constants'

export function usePayouts(page: number) {
  return useQuery({
    queryKey: PAYOUT_QUERY_KEYS.page(page),
    queryFn: ({ signal }) => payoutApi.list(page, signal),
    placeholderData: keepPreviousData,
  })
}
