import type { QueryClient } from '@tanstack/react-query'
import { WALLET_QUERY_KEYS } from '@/features/wallet/constants/wallet.constants'
import { PAYOUT_QUERY_KEYS, VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'
import { VOUCHER_EVENTS } from '../constants/voucher-events.constants'
import { payoutProgressNotificationSchema, voucherSoldNotificationSchema, voucherStockNotificationSchema } from '../schemas/voucher.schema'
import type { PayoutBatch, PayoutProgress, VoucherPage } from '../types/voucher.types'
import { parseEvent } from './parse-event'

interface VoucherEventCallbacks {
  onVoucherSold: (title: string, remaining: number) => void
  onPayoutCompleted: (progress: PayoutProgress) => void
}

const VOUCHER_LIST_PREFIXES = [VOUCHER_QUERY_KEYS.available(0).slice(0, 2), VOUCHER_QUERY_KEYS.merchant(0).slice(0, 2)]

export function subscribeVoucherEvents(stream: EventSource, queryClient: QueryClient, callbacks: VoucherEventCallbacks): () => void {
  const onStockChanged = (event: Event) => {
    const stock = parseEvent(event, voucherStockNotificationSchema)
    if (!stock) return
    VOUCHER_LIST_PREFIXES.forEach((queryKey) =>
      queryClient.setQueriesData<VoucherPage>({ queryKey }, (page) =>
        page && {
          ...page,
          content: page.content.map((voucher) =>
            voucher.id === stock.voucherId
              ? { ...voucher, remaining: stock.remaining, status: stock.remaining === 0 ? 'SOLD_OUT' : voucher.status }
              : voucher,
          ),
        },
      ),
    )
  }

  const onVoucherSold = (event: Event) => {
    const sold = parseEvent(event, voucherSoldNotificationSchema)
    if (!sold) return
    callbacks.onVoucherSold(sold.title, sold.remaining)
    void queryClient.invalidateQueries({ queryKey: VOUCHER_QUERY_KEYS.all })
  }

  const onPayoutProgress = (event: Event) => {
    const progress = parseEvent(event, payoutProgressNotificationSchema)
    if (!progress) return
    queryClient.setQueryData<PayoutBatch>(PAYOUT_QUERY_KEYS.batch(progress.batchId), (batch) =>
      batch && { ...batch, paidCount: progress.paid, failedCount: progress.failed },
    )
    void queryClient.invalidateQueries({ queryKey: PAYOUT_QUERY_KEYS.batch(progress.batchId) })
  }

  const onPayoutCompleted = (event: Event) => {
    const progress = parseEvent(event, payoutProgressNotificationSchema)
    if (!progress) return
    callbacks.onPayoutCompleted(progress)
    void queryClient.invalidateQueries({ queryKey: PAYOUT_QUERY_KEYS.all })
    void queryClient.invalidateQueries({ queryKey: WALLET_QUERY_KEYS.all })
  }

  stream.addEventListener(VOUCHER_EVENTS.stockChanged, onStockChanged)
  stream.addEventListener(VOUCHER_EVENTS.sold, onVoucherSold)
  stream.addEventListener(VOUCHER_EVENTS.payoutProgress, onPayoutProgress)
  stream.addEventListener(VOUCHER_EVENTS.payoutCompleted, onPayoutCompleted)

  return () => {
    stream.removeEventListener(VOUCHER_EVENTS.stockChanged, onStockChanged)
    stream.removeEventListener(VOUCHER_EVENTS.sold, onVoucherSold)
    stream.removeEventListener(VOUCHER_EVENTS.payoutProgress, onPayoutProgress)
    stream.removeEventListener(VOUCHER_EVENTS.payoutCompleted, onPayoutCompleted)
  }
}
