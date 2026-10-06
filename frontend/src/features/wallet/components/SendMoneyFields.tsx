import { type Control, useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { FormField } from '@/components/form/FormField'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { useLanguage } from '@/hooks/useLanguage'
import { AMOUNT_PLACEHOLDER, CURRENCY, TRANSFER_NOTE_MAX_LENGTH } from '../constants/wallet.constants'
import type { TransferFormInput, TransferFormValues, TransferOptions } from '../types/transfer.types'
import { formatMoney } from '../utils/format-money'
import { RecipientCombobox } from './RecipientCombobox'
import { RecipientPreview } from './RecipientPreview'

interface SendMoneyFieldsProps {
  control: Control<TransferFormInput, unknown, TransferFormValues>
  options: TransferOptions
}

export function SendMoneyFields({ control, options }: SendMoneyFieldsProps) {
  const { t } = useTranslation('wallet')
  const { language } = useLanguage()
  const recipient = useWatch({ control, name: 'recipient' })
  const limits = {
    balance: formatMoney(options.balance, language),
    max: formatMoney(options.maxAmount, language),
    remaining: formatMoney(options.remainingToday, language),
  }

  return (
    <>
      <FormField control={control} name="recipient" label={t('transfer.recipient')} description={t('transfer.recipientHint')}>
        {(props) => <RecipientCombobox {...props} />}
      </FormField>
      <RecipientPreview value={recipient} />
      <FormField control={control} name="amount" label={t('topUp.amount', { currency: CURRENCY })} description={t('transfer.limits', limits)}>
        {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
      </FormField>
      <FormField control={control} name="note" label={t('transfer.note')}>
        {(props) => <Textarea {...props} rows={2} maxLength={TRANSFER_NOTE_MAX_LENGTH} placeholder={t('transfer.notePlaceholder')} />}
      </FormField>
    </>
  )
}
