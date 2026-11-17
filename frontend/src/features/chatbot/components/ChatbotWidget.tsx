import { useEffect, useState } from 'react'
import { BotIcon, Maximize2Icon, Minimize2Icon, RotateCcwIcon, XIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { useChatbot } from '../hooks/useChatbot'
import { ChatInput } from './ChatInput'
import { ChatMessageList } from './ChatMessageList'
import { ChatWelcome } from './ChatWelcome'

export function ChatbotWidget() {
  const { t } = useTranslation('chatbot')
  const { messages, ask, isStreaming, reset } = useChatbot()
  const [open, setOpen] = useState(false)
  const [expanded, setExpanded] = useState(false)

  useEffect(() => {
    if (!open) return
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setOpen(false)
    }
    window.addEventListener('keydown', onKeyDown)
    return () => {
      window.removeEventListener('keydown', onKeyDown)
    }
  }, [open])

  return (
    <>
      {open && expanded && (
        <div
          className="fixed inset-0 z-40 hidden bg-black/30 backdrop-blur-[1px] sm:block"
          aria-hidden="true"
          onClick={() => {
            setExpanded(false)
          }}
        />
      )}
      <section
        role="dialog"
        aria-label={t('title')}
        hidden={!open}
        className={cn(
          'fixed inset-0 z-50 flex flex-col overflow-hidden bg-popover text-popover-foreground shadow-2xl sm:rounded-2xl sm:border',
          expanded
            ? 'sm:inset-x-6 sm:inset-y-6 sm:mx-auto sm:max-w-4xl'
            : 'sm:inset-auto sm:end-6 sm:bottom-6 sm:h-[min(640px,calc(100dvh-3rem))] sm:w-[400px]',
        )}
      >
        <header className="flex items-center gap-2 border-b px-4 py-3">
          <span className="flex size-9 items-center justify-center rounded-full bg-primary/10 text-primary">
            <BotIcon className="size-5" aria-hidden="true" />
          </span>
          <div className="min-w-0 flex-1">
            <h2 className="truncate text-sm font-semibold">{t('title')}</h2>
            <p className="truncate text-xs text-muted-foreground">{t('description')}</p>
          </div>
          {messages.length > 0 && (
            <Button variant="ghost" size="icon-sm" onClick={reset} aria-label={t('reset')} title={t('reset')}>
              <RotateCcwIcon aria-hidden="true" />
            </Button>
          )}
          <Button
            variant="ghost"
            size="icon-sm"
            className="hidden sm:inline-flex"
            onClick={() => {
              setExpanded((value) => !value)
            }}
            aria-label={expanded ? t('collapse') : t('expand')}
            title={expanded ? t('collapse') : t('expand')}
            aria-pressed={expanded}
          >
            {expanded ? <Minimize2Icon aria-hidden="true" /> : <Maximize2Icon aria-hidden="true" />}
          </Button>
          <Button
            variant="ghost"
            size="icon-sm"
            onClick={() => {
              setOpen(false)
            }}
            aria-label={t('close')}
            title={t('close')}
          >
            <XIcon aria-hidden="true" />
          </Button>
        </header>
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto p-4">
          <div className={cn('mx-auto flex w-full flex-1 flex-col', expanded && 'max-w-3xl')}>
            {messages.length === 0 ? <ChatWelcome onAsk={ask} /> : <ChatMessageList messages={messages} isStreaming={isStreaming} />}
          </div>
        </div>
        <div className="border-t p-4">
          <div className={cn('mx-auto w-full', expanded && 'max-w-3xl')}>
            <ChatInput disabled={isStreaming} onAsk={ask} />
          </div>
        </div>
      </section>
      {!open && (
        <Button
          size="icon-lg"
          className="fixed end-4 bottom-4 z-50 size-14 rounded-full shadow-lg md:end-6 md:bottom-6"
          onClick={() => {
            setOpen(true)
          }}
          aria-label={t('open')}
          aria-expanded={false}
        >
          <BotIcon className="size-6" aria-hidden="true" />
        </Button>
      )}
    </>
  )
}
