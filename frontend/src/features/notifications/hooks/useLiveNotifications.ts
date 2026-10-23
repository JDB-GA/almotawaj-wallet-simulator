import { useEffect } from 'react'
import { useQueryClient } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { env } from '@/config/env'
import { useCurrentUser } from '@/features/auth/hooks/useCurrentUser'
import { MERCHANT_QUERY_KEYS } from '@/features/merchant/constants/merchant.constants'
import { subscribeVoucherEvents } from '@/features/vouchers/utils/subscribe-voucher-events'
import { PAYMENT_REQUEST_QUERY_KEYS } from '@/features/wallet/constants/payment-request.constants'
import { WALLET_QUERY_KEYS } from '@/features/wallet/constants/wallet.constants'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { DEFAULT_LANGUAGE, isLanguage } from '@/i18n/languages'
import { NOTIFICATION_ENDPOINTS, NOTIFICATION_EVENTS } from '../constants/notification.constants'
import { parseMoneyReceivedNotification } from '../utils/parse-money-received-notification'
import { useNotificationSound } from './useNotificationSound'

export function useLiveNotifications() {
  const { data: user } = useCurrentUser()
  const queryClient = useQueryClient()
  const { i18n, t } = useTranslation(['wallet', 'vouchers'])
  const { play } = useNotificationSound()

  const userId = user?.id
  const canSubscribe = user?.role === 'CLIENT' || user?.role === 'MERCHANT'

  useEffect(() => {
    if (!userId || !canSubscribe) return

    const stream = new EventSource(`${env.apiUrl}${NOTIFICATION_ENDPOINTS.stream}`, { withCredentials: true })

    const refreshRequests = () =>
      queryClient.invalidateQueries({
        queryKey: PAYMENT_REQUEST_QUERY_KEYS.all,
      })

    const refreshWallet = () =>
      Promise.all([
        queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEYS.all }),
        queryClient.invalidateQueries({ queryKey: MERCHANT_QUERY_KEYS.sessions }),
      ])

    const refreshAll = () => {
      void Promise.all([refreshRequests(), refreshWallet()])
    }

    const onMoneyReceived = (event: Event) => {
      if (!(event instanceof MessageEvent)) return

      const notification = parseMoneyReceivedNotification(event.data)
      if (!notification) return

      const resolvedLanguage = i18n.resolvedLanguage ?? DEFAULT_LANGUAGE

      const language = isLanguage(resolvedLanguage) ? resolvedLanguage : DEFAULT_LANGUAGE

      play()

      toast.success(t('notifications.moneyReceived.title'), {
        description: t('notifications.moneyReceived.description', {
          amount: formatMoney(notification.amount, language),
          sender: notification.senderName,
        }),
      })

      refreshAll()
    }

    const onPaymentRequested = () => {
      play()
      toast.info(t('requests.newRequestReceived'))
      void refreshRequests()
    }

    const onPaymentRequestUpdated = () => {
      refreshAll()
    }

    stream.addEventListener('open', refreshAll)
    stream.addEventListener(NOTIFICATION_EVENTS.moneyReceived, onMoneyReceived)
    stream.addEventListener(NOTIFICATION_EVENTS.paymentRequested, onPaymentRequested)
    stream.addEventListener(NOTIFICATION_EVENTS.paymentRequestUpdated, onPaymentRequestUpdated)
    const unsubscribeVouchers = subscribeVoucherEvents(stream, queryClient, {
      onVoucherSold: (title, remaining) => {
        play()
        toast.success(t('vouchers:live.sold'), { description: t('vouchers:live.soldDescription', { title, remaining }) })
      },
      onPayoutCompleted: ({ paid, failed }) => {
        play()
        toast.success(t('vouchers:payouts.completed', { paid, failed }))
      },
    })

    return () => {
      stream.removeEventListener('open', refreshAll)
      stream.removeEventListener(NOTIFICATION_EVENTS.moneyReceived, onMoneyReceived)
      stream.removeEventListener(NOTIFICATION_EVENTS.paymentRequested, onPaymentRequested)
      stream.removeEventListener(NOTIFICATION_EVENTS.paymentRequestUpdated, onPaymentRequestUpdated)
      unsubscribeVouchers()
      stream.close()
    }
  }, [userId, canSubscribe, queryClient, i18n.resolvedLanguage, t, play])
}
