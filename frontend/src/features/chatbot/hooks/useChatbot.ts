import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { chatbotApi } from '../api/chatbot.api'
import type { ChatMessage } from '../types/chatbot.types'

const createMessage = (message: Omit<ChatMessage, 'id'>): ChatMessage => ({ id: crypto.randomUUID(), ...message })

export function useChatbot() {
  const { t } = useTranslation('chatbot')
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const mutation = useMutation({ mutationFn: chatbotApi.ask })

  const ask = (question: string) => {
    const text = question.trim()
    if (!text || mutation.isPending) return
    setMessages((current) => [...current, createMessage({ role: 'user', text, sources: [] })])
    mutation.mutate(text, {
      onSuccess: ({ answer, sources }) => { setMessages((current) => [...current, createMessage({ role: 'assistant', text: answer, sources })]); },
      onError: () => { setMessages((current) => [...current, createMessage({ role: 'assistant', text: t('error'), sources: [], failed: true })]); },
    })
  }

  return { messages, ask, isThinking: mutation.isPending, reset: () => { setMessages([]); } }
}
