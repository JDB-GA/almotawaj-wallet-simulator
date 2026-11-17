import { Trash2Icon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { ConfirmActionDialog } from '@/components/ConfirmActionDialog'
import { Badge } from '@/components/ui/badge'
import { TableCell, TableRow } from '@/components/ui/table'
import { BYTES_PER_KB } from '../constants/chatbot-documents.constants'
import { useDeleteChatbotDocument } from '../hooks/useDeleteChatbotDocument'
import type { ChatbotDocument } from '../types/chatbot-document.types'

interface ChatbotDocumentRowProps {
  document: ChatbotDocument
}

export function ChatbotDocumentRow({ document }: ChatbotDocumentRowProps) {
  const { t, i18n } = useTranslation('chatbotDocuments')
  const remove = useDeleteChatbotDocument()
  const size = new Intl.NumberFormat(i18n.resolvedLanguage, { maximumFractionDigits: 1 }).format(document.sizeBytes / BYTES_PER_KB)
  const createdAt = document.createdAt
    ? new Intl.DateTimeFormat(i18n.resolvedLanguage, { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(document.createdAt))
    : ''

  return (
    <TableRow>
      <TableCell data-label={t('table.name')} className="font-medium">{document.name}</TableCell>
      <TableCell data-label={t('table.size')}>{t('table.sizeValue', { size })}</TableCell>
      <TableCell data-label={t('table.state')}>
        <Badge variant={document.state === 'STATE_FAILED' ? 'destructive' : 'secondary'}>{t(`states.${document.state}`)}</Badge>
      </TableCell>
      <TableCell data-label={t('table.uploaded')}>{createdAt}</TableCell>
      <TableCell className="text-end">
        <ConfirmActionDialog
          trigger={<Trash2Icon aria-label={t('delete.action')} />}
          title={t('delete.confirmTitle')}
          description={t('delete.confirmDescription', { name: document.name })}
          confirmLabel={t('delete.action')}
          cancelLabel={t('delete.cancel')}
          destructive
          disabled={remove.isPending}
          onConfirm={() => {
            remove.mutate(document.id, {
              onSuccess: () => toast.success(t('delete.success')),
              onError: () => toast.error(t('delete.failure')),
            })
          }}
        />
      </TableCell>
    </TableRow>
  )
}
