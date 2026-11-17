import { useRef } from 'react'
import { UploadIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { CHATBOT_DOCUMENT_ACCEPT } from '../constants/chatbot-documents.constants'
import { useUploadChatbotDocument } from '../hooks/useUploadChatbotDocument'

export function ChatbotDocumentUpload() {
  const { t } = useTranslation('chatbotDocuments')
  const inputRef = useRef<HTMLInputElement>(null)
  const upload = useUploadChatbotDocument()

  return (
    <>
      <input
        ref={inputRef}
        type="file"
        accept={CHATBOT_DOCUMENT_ACCEPT}
        className="hidden"
        onChange={(event) => {
          const file = event.target.files?.[0]
          event.target.value = ''
          if (!file) return
          upload.mutate(file, {
            onSuccess: () => toast.success(t('upload.success', { name: file.name })),
            onError: () => toast.error(t('upload.failure')),
          })
        }}
      />
      <Button disabled={upload.isPending} onClick={() => inputRef.current?.click()}>
        <UploadIcon aria-hidden="true" />
        {upload.isPending ? t('upload.pending') : t('upload.action')}
      </Button>
    </>
  )
}
