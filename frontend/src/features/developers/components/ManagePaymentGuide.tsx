import { useTranslation } from 'react-i18next'
import { getCodeSamples } from '../utils/build-code-samples'
import { CodeBlock } from './CodeBlock'
import { EndpointsTable } from './EndpointsTable'
import { GuideSection } from './GuideSection'

export function ManagePaymentGuide() {
  const { t } = useTranslation('developers')
  const samples = getCodeSamples()

  return (
    <GuideSection title={t('manage.title')} description={t('manage.description')}>
      <CodeBlock title={t('manage.cancel')} code={samples.cancelCurl} />
      <CodeBlock title={t('manage.refund')} code={samples.refundCurl} />
      <h3 className="text-sm font-semibold">{t('endpoints.title')}</h3>
      <EndpointsTable />
    </GuideSection>
  )
}
