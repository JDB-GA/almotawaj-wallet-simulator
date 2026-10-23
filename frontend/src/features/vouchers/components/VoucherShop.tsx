import { useState } from 'react'
import { TicketIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { PaginationControls } from '@/components/PaginationControls'
import { Skeleton } from '@/components/ui/skeleton'
import { EmptyState } from '@/features/merchant/components/EmptyState'
import { useAvailableVouchers } from '../hooks/useAvailableVouchers'
import { usePurchaseVoucher } from '../hooks/usePurchaseVoucher'
import { VoucherCard } from './VoucherCard'

export function VoucherShop() {
  const { t } = useTranslation('vouchers')
  const [page, setPage] = useState(0)
  const { data, isPending, isError, isPlaceholderData, refetch } = useAvailableVouchers(page)
  const { buy, pendingVoucherId } = usePurchaseVoucher()

  if (isPending) {
    return <Skeleton className="h-64 w-full rounded-xl" />
  }

  if (isError) {
    return <LoadErrorAlert message={t('shop.loadError')} onRetry={() => void refetch()} />
  }

  if (data.content.length === 0) {
    return <EmptyState icon={TicketIcon} title={t('shop.emptyTitle')} description={t('shop.emptyDescription')} />
  }

  return (
    <div className="flex flex-col gap-4" aria-busy={isPlaceholderData}>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {data.content.map((voucher) => (
          <VoucherCard key={voucher.id} voucher={voucher} isBuying={pendingVoucherId === voucher.id} onBuy={buy} />
        ))}
      </div>
      <PaginationControls page={data.page} totalPages={data.totalPages} disabled={isPlaceholderData} onPageChange={setPage} />
    </div>
  )
}
