import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { ChatbotDocumentList } from '../components/ChatbotDocumentList'
import { ChatbotDocumentUpload } from '../components/ChatbotDocumentUpload'

export function ChatbotDocumentsPage() {
  const { t } = useTranslation()
  const { t: tDocuments } = useTranslation('chatbotDocuments')

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.chatbotDocuments.title')} />
      <Card>
        <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex flex-col gap-1">
            <CardTitle>
              <h1>{t('areas.chatbotDocuments.heading')}</h1>
            </CardTitle>
            <CardDescription>{tDocuments('description')}</CardDescription>
          </div>
          <ChatbotDocumentUpload />
        </CardHeader>
        <CardContent>
          <ChatbotDocumentList />
        </CardContent>
      </Card>
    </div>
  )
}
