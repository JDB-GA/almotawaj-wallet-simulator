import { useState } from 'react'
import { TicketIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { PaginationControls } from '@/components/PaginationControls'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { Spinner } from '@/components/ui/spinner'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { formatDate } from '../utils/format-date'
import { EmptyState } from '@/features/merchant/components/EmptyState'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { VOUCHER_BADGE_VARIANTS } from '../constants/voucher.constants'
import { useMerchantVouchers } from '../hooks/useMerchantVouchers'
import { useToggleVoucher } from '../hooks/useToggleVoucher'
import { StockMeter } from './StockMeter'

export function MerchantVouchersList() {
  const { t } = useTranslation('vouchers')
  const { language } = useLanguage()
  const [page, setPage] = useState(0)
  const { data, isPending, isError, isPlaceholderData, refetch } = useMerchantVouchers(page)
  const { toggle, pendingVoucherId } = useToggleVoucher()

  if (isPending) {
    return <Skeleton className="h-64 w-full rounded-xl" />
  }

  if (isError) {
    return <LoadErrorAlert message={t('manage.loadError')} onRetry={() => void refetch()} />
  }

  if (data.content.length === 0) {
    return <EmptyState icon={TicketIcon} title={t('manage.emptyTitle')} description={t('manage.emptyDescription')} />
  }

  return (
    <div className="flex flex-col gap-4" aria-busy={isPlaceholderData}>
      <Table className="stacked-table">
        <TableHeader>
          <TableRow>
            <TableHead>{t('manage.voucher')}</TableHead>
            <TableHead>{t('manage.stock')}</TableHead>
            <TableHead>{t('manage.status')}</TableHead>
            <TableHead className="text-end">{t('manage.price')}</TableHead>
            <TableHead className="text-end">
              <span className="sr-only">{t('manage.actions')}</span>
            </TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.content.map((voucher) => {
            const canToggle = voucher.status === 'ACTIVE' || voucher.status === 'PAUSED'
            const isToggling = pendingVoucherId === voucher.id
            return (
              <TableRow key={voucher.id}>
                <TableCell data-label={t('manage.voucher')}>
                  <div className="flex flex-col">
                    <bdi dir="auto" className="font-medium">
                      {voucher.title}
                    </bdi>
                    <span className="text-xs text-muted-foreground">
                      {t('shop.expires', { date: formatDate(voucher.expiresAt, language) })} · v{voucher.version}
                    </span>
                  </div>
                </TableCell>
                <TableCell data-label={t('manage.stock')} className="min-w-36">
                  <StockMeter remaining={voucher.remaining} total={voucher.totalQuantity} />
                </TableCell>
                <TableCell data-label={t('manage.status')}>
                  <Badge variant={VOUCHER_BADGE_VARIANTS[voucher.status]}>{t(`status.${voucher.status}`)}</Badge>
                </TableCell>
                <TableCell data-label={t('manage.price')} className="text-end">
                  <bdi dir="ltr" className="font-medium tabular-nums">
                    {formatMoney(voucher.price, language)}
                  </bdi>
                </TableCell>
                <TableCell className="text-end">
                  {canToggle && (
                    <Button variant="outline" size="sm" disabled={isToggling} aria-busy={isToggling} onClick={() => { toggle(voucher); }}>
                      {isToggling && <Spinner data-icon="inline-start" />}
                      {voucher.status === 'PAUSED' ? t('manage.resume') : t('manage.pause')}
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
      <PaginationControls page={data.page} totalPages={data.totalPages} disabled={isPlaceholderData} onPageChange={setPage} />
    </div>
  )
}
