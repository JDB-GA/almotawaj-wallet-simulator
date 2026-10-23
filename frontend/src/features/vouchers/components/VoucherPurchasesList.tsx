import { useState } from 'react'
import { TicketCheckIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { CopyButton } from '@/components/CopyButton'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { PaginationControls } from '@/components/PaginationControls'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatDateTime } from '@/features/kyc/utils/format-kyc-date'
import { EmptyState } from '@/features/merchant/components/EmptyState'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { VOUCHER_PURCHASE_BADGE_VARIANTS } from '../constants/voucher.constants'
import { useVoucherPurchases } from '../hooks/useVoucherPurchases'

export function VoucherPurchasesList() {
  const { t } = useTranslation('vouchers')
  const { language } = useLanguage()
  const [page, setPage] = useState(0)
  const { data, isPending, isError, isPlaceholderData, refetch } = useVoucherPurchases(page)

  if (isPending) {
    return <Skeleton className="h-64 w-full rounded-xl" />
  }

  if (isError) {
    return <LoadErrorAlert message={t('mine.loadError')} onRetry={() => void refetch()} />
  }

  if (data.content.length === 0) {
    return <EmptyState icon={TicketCheckIcon} title={t('mine.emptyTitle')} description={t('mine.emptyDescription')} />
  }

  return (
    <div className="flex flex-col gap-4" aria-busy={isPlaceholderData}>
      <Table className="stacked-table">
        <TableHeader>
          <TableRow>
            <TableHead>{t('mine.date')}</TableHead>
            <TableHead>{t('mine.voucher')}</TableHead>
            <TableHead>{t('mine.code')}</TableHead>
            <TableHead>{t('mine.status')}</TableHead>
            <TableHead className="text-end">{t('mine.paid')}</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.content.map((purchase) => (
            <TableRow key={purchase.id}>
              <TableCell data-label={t('mine.date')} className="whitespace-nowrap">
                {formatDateTime(purchase.createdAt, language)}
              </TableCell>
              <TableCell data-label={t('mine.voucher')}>
                <div className="flex flex-col">
                  <bdi dir="auto" className="font-medium">
                    {purchase.title}
                  </bdi>
                  <bdi dir="auto" className="text-xs text-muted-foreground">
                    {purchase.merchantName}
                  </bdi>
                </div>
              </TableCell>
              <TableCell data-label={t('mine.code')}>
                <div className="flex items-center gap-1">
                  <bdi dir="ltr" className="font-mono text-xs font-medium">
                    {purchase.code}
                  </bdi>
                  <CopyButton value={purchase.code} label={t('mine.copy')} successMessage={t('mine.copied')} failureMessage={t('mine.copyFailed')} />
                </div>
              </TableCell>
              <TableCell data-label={t('mine.status')}>
                <div className="flex flex-col gap-1">
                  <Badge variant={VOUCHER_PURCHASE_BADGE_VARIANTS[purchase.status]}>{t(`purchaseStatus.${purchase.status}`)}</Badge>
                  {purchase.redeemedAt && (
                    <span className="text-xs text-muted-foreground">{formatDateTime(purchase.redeemedAt, language)}</span>
                  )}
                </div>
              </TableCell>
              <TableCell data-label={t('mine.paid')} className="text-end">
                <bdi dir="ltr" className="font-medium tabular-nums">
                  {formatMoney(purchase.price, language)}
                </bdi>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <PaginationControls page={data.page} totalPages={data.totalPages} disabled={isPlaceholderData} onPageChange={setPage} />
    </div>
  )
}
