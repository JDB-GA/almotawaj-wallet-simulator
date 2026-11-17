import { FileTextIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import type { ChatMessage } from '../types/chatbot.types'

interface ChatMessageBubbleProps {
  message: ChatMessage
}

export function ChatMessageBubble({ message }: ChatMessageBubbleProps) {
  const { t } = useTranslation('chatbot')
  const isUser = message.role === 'user'

  return (
    <div className={cn('flex flex-col gap-1', isUser ? 'items-end' : 'items-start')}>
      <div
        dir="auto"
        className={cn(
          'max-w-[85%] rounded-2xl px-3 py-2 text-sm whitespace-pre-wrap',
          isUser && 'rounded-ee-sm bg-primary text-primary-foreground',
          !isUser && !message.failed && 'rounded-es-sm bg-muted',
          message.failed && 'rounded-es-sm bg-destructive/10 text-destructive',
        )}
      >
        {message.text}
      </div>
      {message.sources.length > 0 && (
        <div className="flex max-w-[85%] flex-wrap items-center gap-1 text-xs text-muted-foreground">
          <span>{t('sources')}</span>
          {message.sources.map((source) => (
            <span key={source} className="inline-flex items-center gap-1 rounded-md border px-1.5 py-0.5">
              <FileTextIcon className="size-3" aria-hidden="true" />
              {source}
            </span>
          ))}
        </div>
      )}
    </div>
  )
}
