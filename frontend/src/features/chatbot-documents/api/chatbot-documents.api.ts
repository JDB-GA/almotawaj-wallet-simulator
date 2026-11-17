import { requestJson, requestVoid } from '@/lib/api/http-client'
import { CHATBOT_DOCUMENT_ENDPOINTS, CHATBOT_DOCUMENT_FIELD } from '../constants/chatbot-documents.constants'
import { chatbotDocumentListSchema } from '../schemas/chatbot-document.schema'
import type { ChatbotDocument } from '../types/chatbot-document.types'

export const chatbotDocumentsApi = {
  list: (signal?: AbortSignal): Promise<ChatbotDocument[]> =>
    requestJson(CHATBOT_DOCUMENT_ENDPOINTS.list, chatbotDocumentListSchema, { signal }),
  upload: (file: File): Promise<void> => {
    const body = new FormData()
    body.append(CHATBOT_DOCUMENT_FIELD, file)
    return requestVoid(CHATBOT_DOCUMENT_ENDPOINTS.list, { method: 'POST', body })
  },
  remove: (id: string): Promise<void> => requestVoid(CHATBOT_DOCUMENT_ENDPOINTS.item(id), { method: 'DELETE' }),
}
