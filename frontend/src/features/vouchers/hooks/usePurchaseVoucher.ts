import { useRef } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { WALLET_QUERY_KEYS } from '@/features/wallet/constants/wallet.constants'
import { getErrorKey } from '@/lib/api/get-error-key'
import { voucherApi } from '../api/voucher.api'
import { NETWORK_ERROR_KEY, VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'

export function usePurchaseVoucher() {
  const { t } = useTranslation(['vouchers', 'errors'])
  const queryClient = useQueryClient()
  const attemptKeys = useRef(new Map<string, string>())

  const purchase = useMutation({
    mutationFn: voucherApi.purchase,
    onSuccess: async (bought, { voucherId }) => {
      attemptKeys.current.delete(voucherId)
      toast.success(t('vouchers:shop.bought', { title: bought.title }), { description: bought.code })
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: VOUCHER_QUERY_KEYS.all }),
        queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEYS.all }),
      ])
    },
    onError: (error, { voucherId }) => {
      if (getErrorKey(error) !== NETWORK_ERROR_KEY) {
        attemptKeys.current.delete(voucherId)
      }
      toast.error(t(`errors:${getErrorKey(error)}`))
      void queryClient.invalidateQueries({ queryKey: VOUCHER_QUERY_KEYS.all })
    },
  })

  const buy = (voucherId: string) => {
    const key = attemptKeys.current.get(voucherId) ?? crypto.randomUUID()
    attemptKeys.current.set(voucherId, key)
    purchase.mutate({ voucherId, idempotencyKey: key })
  }

  return { buy, pendingVoucherId: purchase.isPending ? purchase.variables.voucherId : null }
}
