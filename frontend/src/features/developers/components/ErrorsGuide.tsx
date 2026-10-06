import { useTranslation } from 'react-i18next'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { GUIDE_ERRORS, GUIDE_RULES } from '../constants/developers.constants'
import { getCodeSamples } from '../utils/build-code-samples'
import { CodeBlock } from './CodeBlock'
import { GuideSection } from './GuideSection'

export function ErrorsGuide() {
  const { t } = useTranslation('developers')
  const samples = getCodeSamples()

  return (
    <GuideSection title={t('errors.title')} description={t('errors.description')}>
      <CodeBlock title="409 Conflict" code={samples.errorResponse} />
      <Table className="stacked-table">
        <TableHeader>
          <TableRow>
            <TableHead>{t('errors.status')}</TableHead>
            <TableHead>{t('errors.code')}</TableHead>
            <TableHead>{t('errors.meaning')}</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {GUIDE_ERRORS.map((error) => (
            <TableRow key={error.code}>
              <TableCell data-label={t('errors.status')}>{error.status}</TableCell>
              <TableCell data-label={t('errors.code')} className="font-mono text-xs">
                <bdi dir="ltr">{error.code}</bdi>
              </TableCell>
              <TableCell data-label={t('errors.meaning')} className="whitespace-normal">
                {t(`errors.items.${error.code}`)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <h3 className="text-sm font-semibold">{t('rules.title')}</h3>
      <ul className="flex list-disc flex-col gap-2 ps-5 text-sm">
        {GUIDE_RULES.map((rule) => (
          <li key={rule}>{t(`rules.items.${rule}`)}</li>
        ))}
      </ul>
    </GuideSection>
  )
}
