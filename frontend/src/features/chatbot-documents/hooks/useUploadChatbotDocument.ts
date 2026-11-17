import { useMutation, useQueryClient } from '@tanstack/react-query'
import { chatbotDocumentsApi } from '../api/chatbot-documents.api'
import { CHATBOT_DOCUMENT_QUERY_KEYS } from '../constants/chatbot-documents.constants'

export function useUploadChatbotDocument() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: chatbotDocumentsApi.upload,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHATBOT_DOCUMENT_QUERY_KEYS.list }),
  })
}
