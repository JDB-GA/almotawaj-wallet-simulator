import { BotIcon, RotateCcwIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from '@/components/ui/sheet'
import { useChatbot } from '../hooks/useChatbot'
import { ChatInput } from './ChatInput'
import { ChatMessageList } from './ChatMessageList'
import { ChatWelcome } from './ChatWelcome'

export function ChatbotWidget() {
  const { t } = useTranslation('chatbot')
  const { messages, ask, isThinking, reset } = useChatbot()

  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button size="icon-lg" className="fixed end-4 bottom-4 z-40 size-12 rounded-full shadow-lg md:end-6 md:bottom-6" aria-label={t('open')}>
          <BotIcon className="size-6" aria-hidden="true" />
        </Button>
      </SheetTrigger>
      <SheetContent className="w-full gap-0 sm:max-w-md">
        <SheetHeader className="border-b">
          <div className="flex items-center gap-2 pe-8">
            <BotIcon className="size-5 text-primary" aria-hidden="true" />
            <SheetTitle className="flex-1">{t('title')}</SheetTitle>
            {messages.length > 0 && (
              <Button variant="ghost" size="icon-sm" onClick={reset} aria-label={t('reset')}>
                <RotateCcwIcon aria-hidden="true" />
              </Button>
            )}
          </div>
          <SheetDescription>{t('description')}</SheetDescription>
        </SheetHeader>
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto p-4">
          {messages.length === 0 ? <ChatWelcome onAsk={ask} /> : <ChatMessageList messages={messages} isThinking={isThinking} />}
        </div>
        <div className="border-t p-4">
          <ChatInput disabled={isThinking} onAsk={ask} />
        </div>
      </SheetContent>
    </Sheet>
  )
}
