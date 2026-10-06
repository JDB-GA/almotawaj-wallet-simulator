import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useWebhook } from '../hooks/useWebhook'
import { WebhookForm } from './WebhookForm'

export function WebhookCard() {
  const { t } = useTranslation('merchant')
  const { data, isPending, isError, refetch } = useWebhook()

  const renderBody = () => {
    if (isPending) {
      return <Skeleton className="h-32 w-full rounded-xl" />
    }
    if (isError) {
      return (
        <LoadErrorAlert
          message={t('webhook.loadError')}
          onRetry={() => {
            void refetch()
          }}
        />
      )
    }
    return <WebhookForm webhook={data} />
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>
          <h2>{t('webhook.title')}</h2>
        </CardTitle>
        <CardDescription>{t('webhook.description')}</CardDescription>
      </CardHeader>
      <CardContent>{renderBody()}</CardContent>
    </Card>
  )
}
