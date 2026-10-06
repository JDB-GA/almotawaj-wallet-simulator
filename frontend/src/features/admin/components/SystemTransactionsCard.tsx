import { useTranslation } from 'react-i18next'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { TransactionsFilters } from '@/features/wallet/components/TransactionsFilters'
import { useTransactionsFilters } from '@/features/wallet/hooks/useTransactionsFilters'
import { SystemTransactionsList } from './SystemTransactionsList'
import { TransactionStatisticsCards } from './TransactionStatisticsCards'

export function SystemTransactionsCard() {
  const { t } = useTranslation('statistics')
  const { query, hasFilters, update, setPage, reset } = useTransactionsFilters()

  return (
    <Card className="lg:min-h-0 lg:flex-1">
      <CardHeader className="flex shrink-0 flex-col gap-4">
        <CardTitle>
          <h2>{t('transactions.title')}</h2>
        </CardTitle>
        <TransactionsFilters query={query} hasFilters={hasFilters} onChange={update} onReset={reset} />
      </CardHeader>
      <CardContent className="flex flex-col gap-4 lg:min-h-0 lg:flex-1">
        <TransactionStatisticsCards query={query} />
        <SystemTransactionsList query={query} hasFilters={hasFilters} onPageChange={setPage} />
      </CardContent>
    </Card>
  )
}
