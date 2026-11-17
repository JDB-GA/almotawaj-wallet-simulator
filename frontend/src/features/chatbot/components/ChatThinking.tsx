import { useTranslation } from 'react-i18next'

export function ChatThinking() {
  const { t } = useTranslation('chatbot')

  return (
    <div className="flex items-center gap-1 self-start rounded-2xl rounded-es-sm bg-muted px-3 py-3" role="status" aria-label={t('thinking')}>
      <span className="size-1.5 animate-bounce rounded-full bg-muted-foreground [animation-delay:-0.3s]" />
      <span className="size-1.5 animate-bounce rounded-full bg-muted-foreground [animation-delay:-0.15s]" />
      <span className="size-1.5 animate-bounce rounded-full bg-muted-foreground" />
    </div>
  )
}
