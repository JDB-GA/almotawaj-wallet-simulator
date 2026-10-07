import { useEffect } from 'react'
import { TimerIcon } from 'lucide-react'
import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { useCountdown } from '@/hooks/useCountdown'
import { CHECKOUT_QUERY_KEYS } from '../constants/checkout.constants'
import type { Checkout } from '../types/checkout.types'
import { formatRemaining, secondsUntil } from '../utils/format-remaining'

interface CheckoutCountdownProps {
  checkout: Checkout
}

export function CheckoutCountdown({ checkout }: CheckoutCountdownProps) {
  const { t } = useTranslation('merchant')
  const queryClient = useQueryClient()
  const { remaining } = useCountdown(secondsUntil(checkout.expiresAt))

  useEffect(() => {
    if (remaining === 0) {
      void queryClient.invalidateQueries({ queryKey: CHECKOUT_QUERY_KEYS.detail(checkout.id) })
    }
  }, [remaining, queryClient, checkout.id])

  return (
    <p className="flex items-center justify-center gap-2 text-sm text-muted-foreground" role="timer" aria-live="off">
      <TimerIcon className="size-4" aria-hidden="true" />
      {t('checkout.expiresIn')}
      <bdi dir="ltr" className="font-mono font-medium text-foreground tabular-nums">
        {formatRemaining(remaining)}
      </bdi>
    </p>
  )
}
