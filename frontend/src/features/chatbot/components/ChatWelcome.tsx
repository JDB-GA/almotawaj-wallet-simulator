import { BotIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { CHATBOT_SUGGESTIONS } from '../constants/chatbot.constants'

interface ChatWelcomeProps {
  onAsk: (question: string) => void
}

export function ChatWelcome({ onAsk }: ChatWelcomeProps) {
  const { t } = useTranslation('chatbot')

  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-3 text-center">
      <BotIcon className="size-10 text-primary" aria-hidden="true" />
      <p className="font-medium">{t('welcome.title')}</p>
      <p className="text-sm text-muted-foreground">{t('welcome.description')}</p>
      <div className="flex flex-col gap-2 pt-2">
        {CHATBOT_SUGGESTIONS.map((key) => (
          <Button key={key} variant="outline" size="sm" className="whitespace-normal" onClick={() => { onAsk(t(`suggestions.${key}`)); }}>
            {t(`suggestions.${key}`)}
          </Button>
        ))}
      </div>
    </div>
  )
}
