import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { voucherApi } from '../api/voucher.api'
import { VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'

export function useAvailableVouchers(page: number) {
  return useQuery({
    queryKey: VOUCHER_QUERY_KEYS.available(page),
    queryFn: ({ signal }) => voucherApi.available(page, signal),
    placeholderData: keepPreviousData,
  })
}
