import { useMutation, useQueryClient } from '@tanstack/react-query'
import { chatbotDocumentsApi } from '../api/chatbot-documents.api'
import { CHATBOT_DOCUMENT_QUERY_KEYS } from '../constants/chatbot-documents.constants'

export function useDeleteChatbotDocument() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: chatbotDocumentsApi.remove,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHATBOT_DOCUMENT_QUERY_KEYS.list }),
  })
}
