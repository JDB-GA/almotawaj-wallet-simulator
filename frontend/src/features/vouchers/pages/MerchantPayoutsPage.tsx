import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { PayoutBatchesList } from '../components/PayoutBatchesList'
import { PayoutForm } from '../components/PayoutForm'
import { PayoutProgressCard } from '../components/PayoutProgressCard'

export function MerchantPayoutsPage() {
  const { t } = useTranslation(['common', 'vouchers'])
  const [selectedBatchId, setSelectedBatchId] = useState<string | null>(null)

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.merchantPayouts.title')} />
      <Card>
        <CardHeader>
          <CardTitle>
            <h1>{t('areas.merchantPayouts.heading')}</h1>
          </CardTitle>
          <CardDescription>{t('vouchers:payouts.description')}</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <PayoutForm onStarted={(batch) => { setSelectedBatchId(batch.id); }} />
          {selectedBatchId && <PayoutProgressCard batchId={selectedBatchId} />}
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>
            <h2>{t('vouchers:payouts.history')}</h2>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <PayoutBatchesList onSelect={setSelectedBatchId} />
        </CardContent>
      </Card>
    </div>
  )
}
