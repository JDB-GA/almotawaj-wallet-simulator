import { useTranslation } from 'react-i18next'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { useChatbotDocuments } from '../hooks/useChatbotDocuments'
import { ChatbotDocumentEmpty } from './ChatbotDocumentEmpty'
import { ChatbotDocumentRow } from './ChatbotDocumentRow'

export function ChatbotDocumentList() {
  const { t } = useTranslation('chatbotDocuments')
  const { data, isPending, isError, refetch } = useChatbotDocuments()

  if (isPending) {
    return <Skeleton className="h-64 w-full rounded-xl" />
  }

  if (isError) {
    return <LoadErrorAlert message={t('loadError')} onRetry={() => { void refetch() }} />
  }

  if (data.length === 0) {
    return <ChatbotDocumentEmpty />
  }

  return (
    <Table className="stacked-table">
      <TableHeader>
        <TableRow>
          <TableHead>{t('table.name')}</TableHead>
          <TableHead>{t('table.size')}</TableHead>
          <TableHead>{t('table.state')}</TableHead>
          <TableHead>{t('table.uploaded')}</TableHead>
          <TableHead />
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.map((document) => (
          <ChatbotDocumentRow key={document.id} document={document} />
        ))}
      </TableBody>
    </Table>
  )
}
