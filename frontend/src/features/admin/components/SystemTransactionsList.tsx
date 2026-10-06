import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { PaginationControls } from '@/components/PaginationControls'
import { Skeleton } from '@/components/ui/skeleton'
import { TransactionsEmpty } from '@/features/wallet/components/TransactionsEmpty'
import type { TransactionsQuery } from '@/features/wallet/types/wallet.types'
import { useSystemTransactions } from '../hooks/useSystemTransactions'
import { SystemTransactionsTable } from './SystemTransactionsTable'

interface SystemTransactionsListProps {
  query: TransactionsQuery
  hasFilters: boolean
  onPageChange: (page: number) => void
}

export function SystemTransactionsList({ query, hasFilters, onPageChange }: SystemTransactionsListProps) {
  const { t } = useTranslation('statistics')
  const { data, isPending, isError, isPlaceholderData, refetch } = useSystemTransactions(query)

  if (isPending) {
    return <Skeleton className="h-64 w-full rounded-xl" />
  }

  if (isError) {
    return (
      <LoadErrorAlert
        message={t('transactions.loadError')}
        onRetry={() => {
          void refetch()
        }}
      />
    )
  }

  if (data.content.length === 0) {
    return <TransactionsEmpty filtered={hasFilters} />
  }

  return (
    <div className="flex flex-col gap-4 lg:min-h-0 lg:flex-1" aria-busy={isPlaceholderData}>
      <div className="lg:min-h-0 lg:flex-1 lg:overflow-y-auto">
        <SystemTransactionsTable transactions={data.content} />
      </div>
      <PaginationControls page={data.page} totalPages={data.totalPages} disabled={isPlaceholderData} onPageChange={onPageChange} />
    </div>
  )
}
