import { useEffect, useRef } from 'react'
import type { ChatMessage } from '../types/chatbot.types'
import { ChatMessageBubble } from './ChatMessageBubble'
import { ChatThinking } from './ChatThinking'

interface ChatMessageListProps {
  messages: readonly ChatMessage[]
  isThinking: boolean
}

export function ChatMessageList({ messages, isThinking }: ChatMessageListProps) {
  const endRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages, isThinking])

  return (
    <div className="flex flex-1 flex-col gap-3" aria-live="polite">
      {messages.map((message) => (
        <ChatMessageBubble key={message.id} message={message} />
      ))}
      {isThinking && <ChatThinking />}
      <div ref={endRef} />
    </div>
  )
}
