import { useTranslation } from 'react-i18next'
import { REQUEST_FIELDS, RESPONSE_FIELDS } from '../constants/developers.constants'
import { getCodeSamples } from '../utils/build-code-samples'
import { CodeBlock } from './CodeBlock'
import { FieldsTable } from './FieldsTable'
import { GuideSection } from './GuideSection'

export function CreateSessionGuide() {
  const { t } = useTranslation('developers')
  const samples = getCodeSamples()

  const requestRows = REQUEST_FIELDS.map((field) => ({
    name: field.name,
    detail: `${field.type} · ${field.required ? t('fields.required') : t('fields.optional')}`,
    description: t(`request.${field.name}`),
  }))
  const responseRows = RESPONSE_FIELDS.map((name) => ({ name, detail: t(`response.types.${name}`), description: t(`response.${name}`) }))

  return (
    <GuideSection title={t('create.title')} description={t('create.description')}>
      <FieldsTable rows={requestRows} />
      <CodeBlock title="cURL" code={samples.createCurl} />
      <CodeBlock title="Node.js" code={samples.createNode} />
      <h3 className="text-sm font-semibold">{t('create.responseTitle')}</h3>
      <CodeBlock title="201 Created" code={samples.createResponse} />
      <FieldsTable rows={responseRows} />
      <h3 className="text-sm font-semibold">{t('create.returnTitle')}</h3>
      <p className="text-sm text-muted-foreground">{t('create.returnDescription')}</p>
      <CodeBlock title={t('create.returnExample')} code={samples.returnExample} />
    </GuideSection>
  )
}
