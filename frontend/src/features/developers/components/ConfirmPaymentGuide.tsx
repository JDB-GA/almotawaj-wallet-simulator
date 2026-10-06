import { useTranslation } from 'react-i18next'
import { Badge } from '@/components/ui/badge'
import { CHECKOUT_STATUS_BADGE_VARIANTS, CHECKOUT_STATUSES } from '@/features/merchant/constants/merchant.constants'
import { getCodeSamples } from '../utils/build-code-samples'
import { CodeBlock } from './CodeBlock'
import { GuideSection } from './GuideSection'

export function ConfirmPaymentGuide() {
  const { t } = useTranslation(['developers', 'merchant'])
  const samples = getCodeSamples()

  return (
    <GuideSection title={t('developers:confirm.title')} description={t('developers:confirm.description')}>
      <CodeBlock title="cURL" code={samples.confirmCurl} />
      <CodeBlock title="200 OK" code={samples.confirmResponse} />
      <h3 className="text-sm font-semibold">{t('developers:confirm.statusesTitle')}</h3>
      <dl className="flex flex-col gap-3 text-sm">
        {CHECKOUT_STATUSES.map((status) => (
          <div key={status} className="flex flex-wrap items-center gap-2">
            <dt className="flex items-center gap-2">
              <code className="font-mono text-xs">{status}</code>
              <Badge variant={CHECKOUT_STATUS_BADGE_VARIANTS[status]}>{t(`merchant:status.${status}`)}</Badge>
            </dt>
            <dd className="text-muted-foreground">{t(`developers:confirm.statuses.${status}`)}</dd>
          </div>
        ))}
      </dl>
    </GuideSection>
  )
}
