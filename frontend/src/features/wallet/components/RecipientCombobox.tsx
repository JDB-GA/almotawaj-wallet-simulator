import type { Ref } from 'react'
import { useTranslation } from 'react-i18next'
import { Input } from '@/components/ui/input'
import { RECIPIENT_MAX_LENGTH } from '../constants/wallet.constants'
import { useListboxNavigation } from '../hooks/useListboxNavigation'
import { useRecipientSuggestions } from '../hooks/useRecipientSuggestions'
import { RecipientSuggestionOption } from './RecipientSuggestionOption'

interface RecipientComboboxProps {
  id: string
  name: string
  value: string
  ref: Ref<HTMLInputElement>
  onChange: (value: string) => void
  onBlur: () => void
  'aria-invalid': boolean
  'aria-describedby': string | undefined
}

export function RecipientCombobox({ id, value, onChange, onBlur, ...inputProps }: RecipientComboboxProps) {
  const { t } = useTranslation('wallet')
  const suggestions = useRecipientSuggestions(value)
  const listboxId = `${id}-suggestions`
  const { isOpen, activeIndex, setOpen, setActiveIndex, select, onKeyDown } = useListboxNavigation(
    suggestions,
    (suggestion) => {
      onChange(suggestion.iban)
    },
  )
  const optionId = (index: number) => `${listboxId}-${String(index)}`

  return (
    <div className="relative">
      <Input
        {...inputProps}
        id={id}
        value={value}
        dir="ltr"
        role="combobox"
        autoComplete="off"
        spellCheck={false}
        maxLength={RECIPIENT_MAX_LENGTH}
        placeholder={t('transfer.recipientPlaceholder')}
        aria-autocomplete="list"
        aria-expanded={isOpen}
        aria-controls={listboxId}
        aria-activedescendant={isOpen && activeIndex >= 0 ? optionId(activeIndex) : undefined}
        onChange={(event) => {
          onChange(event.target.value)
          setOpen(true)
          setActiveIndex(-1)
        }}
        onFocus={() => {
          setOpen(true)
        }}
        onBlur={() => {
          setOpen(false)
          onBlur()
        }}
        onKeyDown={onKeyDown}
      />
      {isOpen && (
        <ul id={listboxId} role="listbox" className="absolute inset-x-0 top-full z-50 mt-1 rounded-md border bg-popover p-1 shadow-md">
          {suggestions.map((suggestion, index) => (
            <RecipientSuggestionOption
              key={suggestion.iban}
              id={optionId(index)}
              suggestion={suggestion}
              active={index === activeIndex}
              onSelect={() => {
                select(suggestion)
              }}
              onHover={() => {
                setActiveIndex(index)
              }}
            />
          ))}
        </ul>
      )}
    </div>
  )
}
