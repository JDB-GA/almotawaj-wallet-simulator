import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { CallbackGuide } from '../components/CallbackGuide'
import { ConfirmPaymentGuide } from '../components/ConfirmPaymentGuide'
import { CreateSessionGuide } from '../components/CreateSessionGuide'
import { ErrorsGuide } from '../components/ErrorsGuide'
import { GuideOverview } from '../components/GuideOverview'
import { ManagePaymentGuide } from '../components/ManagePaymentGuide'

export function DeveloperGuidePage() {
  const { t } = useTranslation(['common', 'developers'])

  return (
    <div className="mx-auto flex w-full max-w-4xl flex-col gap-4">
      <PageTitle title={t('areas.merchantDevelopers.title')} />
      <div className="flex flex-col gap-1">
        <h1 className="text-2xl font-semibold">{t('developers:heading')}</h1>
        <p className="text-sm text-muted-foreground">{t('developers:intro')}</p>
      </div>
      <GuideOverview />
      <CreateSessionGuide />
      <ConfirmPaymentGuide />
      <CallbackGuide />
      <ManagePaymentGuide />
      <ErrorsGuide />
    </div>
  )
}
