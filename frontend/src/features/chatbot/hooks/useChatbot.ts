import { useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { chatbotApi } from '../api/chatbot.api'
import type { ChatMessage } from '../types/chatbot.types'

const createMessage = (message: Omit<ChatMessage, 'id'>): ChatMessage => ({ id: crypto.randomUUID(), ...message })

export function useChatbot() {
  const { t } = useTranslation('chatbot')
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [isStreaming, setIsStreaming] = useState(false)
  const abortRef = useRef<AbortController | null>(null)

  const updateMessage = (id: string, update: (message: ChatMessage) => ChatMessage) => {
    setMessages((current) => current.map((message) => (message.id === id ? update(message) : message)))
  }

  const ask = async (question: string) => {
    const text = question.trim()
    if (!text || isStreaming) return
    const answer = createMessage({ role: 'assistant', text: '', sources: [] })
    setMessages((current) => [...current, createMessage({ role: 'user', text, sources: [] }), answer])
    setIsStreaming(true)
    const controller = new AbortController()
    abortRef.current = controller
    try {
      await chatbotApi.stream(
        text,
        {
          onDelta: (delta) => {
            updateMessage(answer.id, (message) => ({ ...message, text: message.text + delta }))
          },
          onDone: (sources) => {
            updateMessage(answer.id, (message) => ({ ...message, sources }))
          },
        },
        controller.signal,
      )
    } catch {
      if (!controller.signal.aborted) {
        updateMessage(answer.id, (message) => ({ ...message, text: message.text || t('error'), failed: !message.text }))
      }
    } finally {
      setIsStreaming(false)
    }
  }

  const reset = () => {
    abortRef.current?.abort()
    setMessages([])
  }

  return {
    messages,
    ask: (question: string) => {
      void ask(question)
    },
    isStreaming,
    reset,
  }
}
