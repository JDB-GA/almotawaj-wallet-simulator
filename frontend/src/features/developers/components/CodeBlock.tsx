import { useTranslation } from 'react-i18next'
import { CopyButton } from '@/components/CopyButton'

interface CodeBlockProps {
  title: string
  code: string
}

export function CodeBlock({ title, code }: CodeBlockProps) {
  const { t } = useTranslation('merchant')

  return (
    <figure className="overflow-hidden rounded-lg border bg-muted/50">
      <figcaption className="flex items-center justify-between border-b ps-3 pe-1 text-xs font-medium text-muted-foreground">
        {title}
        <CopyButton value={code} label={t('copy.label')} successMessage={t('copy.copied')} failureMessage={t('copy.failed')} />
      </figcaption>
      <pre dir="ltr" className="overflow-x-auto p-3 text-start font-mono text-xs leading-relaxed">
        <code>{code}</code>
      </pre>
    </figure>
  )
}
