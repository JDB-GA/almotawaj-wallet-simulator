import { useTranslation } from 'react-i18next'
import { Link } from 'react-router'
import { Button } from '@/components/ui/button'
import { env } from '@/config/env'
import { ROUTES } from '@/config/routes'
import { KycDetailRow } from '@/features/kyc/components/KycDetailRow'
import { GUIDE_HEADER, GUIDE_STEPS } from '../constants/developers.constants'
import { GuideSection } from './GuideSection'

export function GuideOverview() {
  const { t } = useTranslation('developers')

  return (
    <GuideSection title={t('overview.title')} description={t('overview.description')}>
      <ol className="flex list-decimal flex-col gap-2 ps-5 text-sm">
        {GUIDE_STEPS.map((step) => (
          <li key={step}>{t(`overview.steps.${step}`)}</li>
        ))}
      </ol>
      <dl className="grid gap-x-4 gap-y-4 sm:grid-cols-2">
        <KycDetailRow label={t('overview.baseUrl')} value={env.apiUrl} dir="ltr" />
        <KycDetailRow label={t('overview.authentication')} value={t('overview.header', { header: GUIDE_HEADER })} />
      </dl>
      <Button asChild variant="outline" className="self-start">
        <Link to={ROUTES.merchantApiKeys}>{t('overview.manageKeys')}</Link>
      </Button>
    </GuideSection>
  )
}
