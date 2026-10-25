import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { getErrorKey } from '@/lib/api/get-error-key'
import { voucherApi } from '../api/voucher.api'
import { VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'
import type { Voucher } from '../types/voucher.types'

export function useToggleVoucher() {
  const { t } = useTranslation(['vouchers', 'errors'])
  const queryClient = useQueryClient()

  const toggle = useMutation({
    mutationFn: (voucher: Voucher) =>
      voucherApi.update({
        voucherId: voucher.id,
        payload: {
          title: voucher.title,
          price: voucher.price,
          status: voucher.status === 'PAUSED' ? 'ACTIVE' : 'PAUSED',
          saleEndsAt: voucher.saleEndsAt,
          expiresAt: voucher.expiresAt,
          version: voucher.version,
        },
      }),
    onSuccess: (updated) => toast.success(t(updated.status === 'PAUSED' ? 'vouchers:manage.paused' : 'vouchers:manage.resumed')),
    onError: (error) => toast.error(t(`errors:${getErrorKey(error)}`)),
    onSettled: () => queryClient.invalidateQueries({ queryKey: VOUCHER_QUERY_KEYS.all }),
  })

  return { toggle: toggle.mutate, pendingVoucherId: toggle.isPending ? toggle.variables.id : null }
}
