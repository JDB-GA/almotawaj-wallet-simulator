import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { voucherApi } from '../api/voucher.api'
import { VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'

export function useMerchantVouchers(page: number) {
  return useQuery({
    queryKey: VOUCHER_QUERY_KEYS.merchant(page),
    queryFn: ({ signal }) => voucherApi.mine(page, signal),
    placeholderData: keepPreviousData,
  })
}
