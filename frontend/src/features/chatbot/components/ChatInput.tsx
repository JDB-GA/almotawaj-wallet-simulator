import { useState } from 'react'
import { SendHorizontalIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { CHATBOT_QUESTION_MAX } from '../constants/chatbot.constants'

interface ChatInputProps {
  disabled: boolean
  onAsk: (question: string) => void
}

export function ChatInput({ disabled, onAsk }: ChatInputProps) {
  const { t } = useTranslation('chatbot')
  const [value, setValue] = useState('')

  const submit = () => {
    if (!value.trim() || disabled) return
    onAsk(value)
    setValue('')
  }

  return (
    <form
      className="flex items-end gap-2"
      onSubmit={(event) => {
        event.preventDefault()
        submit()
      }}
    >
      <Textarea
        dir="auto"
        value={value}
        maxLength={CHATBOT_QUESTION_MAX}
        rows={1}
        placeholder={t('placeholder')}
        aria-label={t('placeholder')}
        className="max-h-32 min-h-9 resize-none"
        onChange={(event) => { setValue(event.target.value); }}
        onKeyDown={(event) => {
          if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault()
            submit()
          }
        }}
      />
      <Button type="submit" size="icon-lg" disabled={disabled || !value.trim()} aria-label={t('send')}>
        <SendHorizontalIcon className="rtl:rotate-180" aria-hidden="true" />
      </Button>
    </form>
  )
}
