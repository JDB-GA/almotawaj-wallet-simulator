import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardAction, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ApiKeysList } from '../components/ApiKeysList'
import { CreateApiKeyDialog } from '../components/CreateApiKeyDialog'
import { WebhookCard } from '../components/WebhookCard'
import { API_KEY_HEADER } from '../constants/merchant.constants'

export function MerchantApiKeysPage() {
  const { t } = useTranslation(['common', 'merchant'])

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.merchantApiKeys.title')} />
      <Card>
        <CardHeader>
          <CardTitle>
            <h1>{t('areas.merchantApiKeys.heading')}</h1>
          </CardTitle>
          <CardDescription>{t('merchant:keys.description', { header: API_KEY_HEADER })}</CardDescription>
          <CardAction>
            <CreateApiKeyDialog />
          </CardAction>
        </CardHeader>
        <CardContent>
          <ApiKeysList />
        </CardContent>
      </Card>
      <WebhookCard />
    </div>
  )
}
