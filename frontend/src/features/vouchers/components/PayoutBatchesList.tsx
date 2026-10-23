import { useState } from 'react'
import { BanknoteIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { PaginationControls } from '@/components/PaginationControls'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatDateTime } from '@/features/kyc/utils/format-kyc-date'
import { EmptyState } from '@/features/merchant/components/EmptyState'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { PAYOUT_BATCH_BADGE_VARIANTS } from '../constants/voucher.constants'
import { usePayouts } from '../hooks/usePayouts'

interface PayoutBatchesListProps {
  onSelect: (batchId: string) => void
}

export function PayoutBatchesList({ onSelect }: PayoutBatchesListProps) {
  const { t } = useTranslation('vouchers')
  const { language } = useLanguage()
  const [page, setPage] = useState(0)
  const { data, isPending, isError, isPlaceholderData, refetch } = usePayouts(page)

  if (isPending) {
    return <Skeleton className="h-40 w-full rounded-xl" />
  }

  if (isError) {
    return <LoadErrorAlert message={t('payouts.loadError')} onRetry={() => void refetch()} />
  }

  if (data.content.length === 0) {
    return <EmptyState icon={BanknoteIcon} title={t('payouts.emptyTitle')} description={t('payouts.emptyDescription')} />
  }

  return (
    <div className="flex flex-col gap-4" aria-busy={isPlaceholderData}>
      <Table className="stacked-table">
        <TableHeader>
          <TableRow>
            <TableHead>{t('payouts.date')}</TableHead>
            <TableHead>{t('payouts.results')}</TableHead>
            <TableHead>{t('payouts.status')}</TableHead>
            <TableHead className="text-end">{t('payouts.total')}</TableHead>
            <TableHead className="text-end">
              <span className="sr-only">{t('manage.actions')}</span>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.content.map((batch) => (
            <TableRow key={batch.id}>
              <TableCell data-label={t('payouts.date')} className="whitespace-nowrap">
                {formatDateTime(batch.createdAt, language)}
              </TableCell>
              <TableCell data-label={t('payouts.results')} className="tabular-nums">
                {t('payouts.summary', { paid: batch.paidCount, failed: batch.failedCount, total: batch.itemCount })}
              </TableCell>
              <TableCell data-label={t('payouts.status')}>
                <Badge variant={PAYOUT_BATCH_BADGE_VARIANTS[batch.status]}>{t(`payoutStatus.${batch.status}`)}</Badge>
              </TableCell>
              <TableCell data-label={t('payouts.total')} className="text-end">
                <bdi dir="ltr" className="font-medium tabular-nums">
                  {formatMoney(batch.totalAmount, language)}
                </bdi>
              </TableCell>
              <TableCell className="text-end">
                <Button variant="ghost" size="sm" onClick={() => { onSelect(batch.id); }}>
                  {t('payouts.view')}
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <PaginationControls page={data.page} totalPages={data.totalPages} disabled={isPlaceholderData} onPageChange={setPage} />
    </div>
  )
}
