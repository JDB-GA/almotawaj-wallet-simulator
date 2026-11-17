import { useQuery } from '@tanstack/react-query'
import { chatbotDocumentsApi } from '../api/chatbot-documents.api'
import { CHATBOT_DOCUMENT_QUERY_KEYS } from '../constants/chatbot-documents.constants'

export function useChatbotDocuments() {
  return useQuery({
    queryKey: CHATBOT_DOCUMENT_QUERY_KEYS.list,
    queryFn: ({ signal }) => chatbotDocumentsApi.list(signal),
  })
}
