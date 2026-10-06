import { ArrowDownLeftIcon, ArrowUpRightIcon, ReceiptTextIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import type { TransactionsQuery } from '@/features/wallet/types/wallet.types'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { useTransactionStatistics } from '../hooks/useTransactionStatistics'
import { StatisticCard } from './StatisticCard'

interface TransactionStatisticsCardsProps {
  query: TransactionsQuery
}

export function TransactionStatisticsCards({ query }: TransactionStatisticsCardsProps) {
  const { t } = useTranslation('statistics')
  const { language } = useLanguage()
  const { data } = useTransactionStatistics(query)
  const money = (amount: number | undefined) => (amount === undefined ? undefined : <bdi dir="ltr">{formatMoney(amount, language)}</bdi>)

  return (
    <section aria-label={t('transactions.summary')} className="grid shrink-0 grid-cols-[repeat(auto-fit,minmax(13rem,1fr))] gap-4">
      <StatisticCard label={t('transactions.count')} icon={ReceiptTextIcon} value={data?.totalTransactions.toLocaleString(language)} />
      <StatisticCard label={t('transactions.credited')} icon={ArrowDownLeftIcon} value={money(data?.totalCredited)} />
      <StatisticCard label={t('transactions.debited')} icon={ArrowUpRightIcon} value={money(data?.totalDebited)} />
    </section>
  )
}
