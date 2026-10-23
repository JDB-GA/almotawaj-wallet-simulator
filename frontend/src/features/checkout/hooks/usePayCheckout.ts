import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { WALLET_QUERY_KEYS } from '@/features/wallet/constants/wallet.constants'
import { getErrorKey } from '@/lib/api/get-error-key'
import { checkoutApi } from '../api/checkout.api'
import { CHECKOUT_QUERY_KEYS } from '../constants/checkout.constants'

export function usePayCheckout(sessionId: string) {
  const { t } = useTranslation(['merchant', 'errors'])
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (voucherId: string | null) => checkoutApi.pay(sessionId, voucherId),
    onSuccess: async (checkout) => {
      toast.success(t('merchant:checkout.paid'))
      queryClient.setQueryData(CHECKOUT_QUERY_KEYS.detail(sessionId), checkout)
      await queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEYS.all })
      await queryClient.invalidateQueries({ queryKey: CHECKOUT_QUERY_KEYS.vouchers(sessionId) })
    },
    onError: async (error) => {
      toast.error(t(`errors:${getErrorKey(error)}`))
      await queryClient.invalidateQueries({ queryKey: CHECKOUT_QUERY_KEYS.detail(sessionId) })
      await queryClient.invalidateQueries({ queryKey: CHECKOUT_QUERY_KEYS.vouchers(sessionId) })
    },
  })
}
