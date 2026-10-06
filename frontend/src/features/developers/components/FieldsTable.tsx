import { useTranslation } from 'react-i18next'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'

interface FieldRow {
  name: string
  detail: string
  description: string
}

interface FieldsTableProps {
  rows: readonly FieldRow[]
}

export function FieldsTable({ rows }: FieldsTableProps) {
  const { t } = useTranslation('developers')

  return (
    <Table className="stacked-table">
      <TableHeader>
        <TableRow>
          <TableHead>{t('fields.name')}</TableHead>
          <TableHead>{t('fields.detail')}</TableHead>
          <TableHead>{t('fields.description')}</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {rows.map((row) => (
          <TableRow key={row.name}>
            <TableCell data-label={t('fields.name')} className="font-mono text-xs">
              <bdi dir="ltr">{row.name}</bdi>
            </TableCell>
            <TableCell data-label={t('fields.detail')}>{row.detail}</TableCell>
            <TableCell data-label={t('fields.description')} className="whitespace-normal">
              {row.description}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
