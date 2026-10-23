import { useTranslation } from 'react-i18next'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { PAYOUT_BATCH_BADGE_VARIANTS, PAYOUT_ITEM_BADGE_VARIANTS, PAYOUT_PROCESSING_ERROR } from '../constants/voucher.constants'
import { isPayoutFailureReason } from '../utils/is-payout-failure-reason'
import { usePayoutBatch } from '../hooks/usePayoutBatch'

interface PayoutProgressCardProps {
  batchId: string
}

const PERCENT = 100

export function PayoutProgressCard({ batchId }: PayoutProgressCardProps) {
  const { t } = useTranslation('vouchers')
  const { language } = useLanguage()
  const { data: batch } = usePayoutBatch(batchId)

  if (!batch) {
    return <Skeleton className="h-40 w-full rounded-xl" />
  }

  const done = batch.paidCount + batch.failedCount
  const paidPercent = batch.itemCount > 0 ? (batch.paidCount / batch.itemCount) * PERCENT : 0
  const failedPercent = batch.itemCount > 0 ? (batch.failedCount / batch.itemCount) * PERCENT : 0

  return (
    <section className="flex flex-col gap-3 rounded-xl border p-4" aria-live="polite">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="font-medium">{t('payouts.progressTitle')}</h2>
        <Badge variant={PAYOUT_BATCH_BADGE_VARIANTS[batch.status]}>{t(`payoutStatus.${batch.status}`)}</Badge>
      </div>
      <div
        className="flex h-2.5 w-full overflow-hidden rounded-full bg-muted"
        role="progressbar"
        aria-valuemin={0}
        aria-valuemax={batch.itemCount}
        aria-valuenow={done}
        aria-label={t('payouts.progressTitle')}
      >
        <div className="h-full bg-primary transition-[width]" style={{ width: `${paidPercent.toString()}%` }} />
        <div className="h-full bg-destructive transition-[width]" style={{ width: `${failedPercent.toString()}%` }} />
      </div>
      <p className="text-sm text-muted-foreground tabular-nums">
        {t('payouts.progress', { done, total: batch.itemCount, paid: batch.paidCount, failed: batch.failedCount })}
      </p>
      {batch.items.length > 0 && (
        <Table className="stacked-table">
          <TableHeader>
            <TableRow>
              <TableHead>{t('payouts.recipient')}</TableHead>
              <TableHead>{t('payouts.result')}</TableHead>
              <TableHead className="text-end">{t('payouts.amountColumn')}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {batch.items.map((item) => (
              <TableRow key={item.id}>
                <TableCell data-label={t('payouts.recipient')}>
                  <bdi dir="ltr">{item.recipientName}</bdi>
                </TableCell>
                <TableCell data-label={t('payouts.result')}>
                  <div className="flex flex-col gap-1">
                    <Badge variant={PAYOUT_ITEM_BADGE_VARIANTS[item.status]}>{t(`payoutItemStatus.${item.status}`)}</Badge>
                    {item.failureReason && (
                      <span className="text-xs text-muted-foreground">
                        {t(`failure.${isPayoutFailureReason(item.failureReason) ? item.failureReason : PAYOUT_PROCESSING_ERROR}`)}
                      </span>
                    )}
                  </div>
                </TableCell>
                <TableCell data-label={t('payouts.amountColumn')} className="text-end">
                  <bdi dir="ltr" className="tabular-nums">
                    {formatMoney(item.amount, language)}
                  </bdi>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </section>
  )
}
