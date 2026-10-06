import { useTranslation } from 'react-i18next'
import { Link } from 'react-router'
import { Button } from '@/components/ui/button'
import { ROUTES } from '@/config/routes'
import { CALLBACK_EVENTS, EVENT_HEADER, SIGNATURE_HEADER } from '../constants/developers.constants'
import { getCodeSamples } from '../utils/build-code-samples'
import { CodeBlock } from './CodeBlock'
import { GuideSection } from './GuideSection'

export function CallbackGuide() {
  const { t } = useTranslation('developers')
  const samples = getCodeSamples()

  return (
    <GuideSection title={t('callback.title')} description={t('callback.description')}>
      <ul className="flex list-disc flex-col gap-2 ps-5 text-sm">
        <li>{t('callback.events', { header: EVENT_HEADER })}</li>
        <li>{t('callback.signature', { header: SIGNATURE_HEADER })}</li>
        <li>{t('callback.retries')}</li>
      </ul>
      <p className="flex flex-wrap gap-2">
        {CALLBACK_EVENTS.map((event) => (
          <code key={event} dir="ltr" className="rounded-md border bg-muted/50 px-2 py-1 font-mono text-xs">
            {event}
          </code>
        ))}
      </p>
      <CodeBlock title={t('callback.bodyTitle')} code={samples.callbackBody} />
      <CodeBlock title={t('callback.verifyTitle')} code={samples.callbackVerify} />
      <Button asChild variant="outline" className="self-start">
        <Link to={ROUTES.merchantApiKeys}>{t('callback.configure')}</Link>
      </Button>
    </GuideSection>
  )
}
