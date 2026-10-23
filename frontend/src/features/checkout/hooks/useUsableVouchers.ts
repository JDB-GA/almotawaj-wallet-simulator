import { useQuery } from '@tanstack/react-query'
import { checkoutApi } from '../api/checkout.api'
import { CHECKOUT_QUERY_KEYS } from '../constants/checkout.constants'

export function useUsableVouchers(sessionId: string, enabled: boolean) {
  return useQuery({
    queryKey: CHECKOUT_QUERY_KEYS.vouchers(sessionId),
    queryFn: ({ signal }) => checkoutApi.vouchers(sessionId, signal),
    enabled,
  })
}
