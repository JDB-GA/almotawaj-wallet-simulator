import { PlusIcon, Trash2Icon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { FormFieldError } from '@/components/form/FormFieldError'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { AMOUNT_PLACEHOLDER, CURRENCY } from '@/features/wallet/constants/wallet.constants'
import { PAYOUT_ITEMS_MAX } from '../constants/voucher.constants'
import { useCreatePayoutForm } from '../hooks/useCreatePayoutForm'
import type { PayoutBatch } from '../types/voucher.types'

interface PayoutFormProps {
  onStarted: (batch: PayoutBatch) => void
}

export function PayoutForm({ onStarted }: PayoutFormProps) {
  const { t } = useTranslation('vouchers')
  const { form, lines, addLine, onSubmit, isPending, errorKey } = useCreatePayoutForm(onStarted)
  const listError = form.formState.errors.items?.root?.message ?? form.formState.errors.items?.message

  return (
    <form onSubmit={onSubmit} noValidate className="flex flex-col gap-3">
      {lines.fields.map((line, index) => (
        <div key={line.id} className="grid grid-cols-[1fr_8rem_auto] items-start gap-2">
          <FormField control={form.control} name={`items.${index.toString()}.recipient` as `items.${number}.recipient`} label={t('payouts.recipient')}>
            {(props) => <Input {...props} dir="ltr" autoComplete="off" placeholder={t('payouts.recipientPlaceholder')} />}
          </FormField>
          <FormField control={form.control} name={`items.${index.toString()}.amount` as `items.${number}.amount`} label={t('payouts.amount', { currency: CURRENCY })}>
            {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
          </FormField>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            className="mt-6"
            disabled={lines.fields.length === 1}
            aria-label={t('payouts.remove')}
            onClick={() => { lines.remove(index); }}
          >
            <Trash2Icon aria-hidden="true" />
          </Button>
        </div>
      ))}
      <FormFieldError id="payout-items-error" message={listError} />
      <FormErrorMessage errorKey={errorKey} />
      <div className="flex flex-wrap justify-between gap-2">
        <Button type="button" variant="outline" disabled={lines.fields.length >= PAYOUT_ITEMS_MAX} onClick={addLine}>
          <PlusIcon data-icon="inline-start" aria-hidden="true" />
          {t('payouts.addRecipient')}
        </Button>
        <Button type="submit" disabled={isPending} aria-busy={isPending}>
          {isPending && <Spinner data-icon="inline-start" />}
          {t('payouts.start', { count: lines.fields.length })}
        </Button>
      </div>
    </form>
  )
}
