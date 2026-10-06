import { useTranslation } from 'react-i18next'
import { Badge } from '@/components/ui/badge'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { GUIDE_ENDPOINTS } from '../constants/developers.constants'

export function EndpointsTable() {
  const { t } = useTranslation('developers')

  return (
    <Table className="stacked-table">
      <TableHeader>
        <TableRow>
          <TableHead>{t('endpoints.method')}</TableHead>
          <TableHead>{t('endpoints.path')}</TableHead>
          <TableHead>{t('endpoints.purpose')}</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {GUIDE_ENDPOINTS.map((endpoint) => (
          <TableRow key={endpoint.key}>
            <TableCell data-label={t('endpoints.method')}>
              <Badge variant={endpoint.method === 'GET' ? 'secondary' : 'default'}>{endpoint.method}</Badge>
            </TableCell>
            <TableCell data-label={t('endpoints.path')} className="font-mono text-xs">
              <bdi dir="ltr">{endpoint.path}</bdi>
            </TableCell>
            <TableCell data-label={t('endpoints.purpose')} className="whitespace-normal">
              {t(`endpoints.items.${endpoint.key}`)}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
