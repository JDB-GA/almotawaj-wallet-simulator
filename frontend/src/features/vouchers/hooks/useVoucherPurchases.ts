import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { voucherApi } from '../api/voucher.api'
import { VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'

export function useVoucherPurchases(page: number) {
  return useQuery({
    queryKey: VOUCHER_QUERY_KEYS.purchases(page),
    queryFn: ({ signal }) => voucherApi.purchases(page, signal),
    placeholderData: keepPreviousData,
  })
}
