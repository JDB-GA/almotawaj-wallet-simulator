import type { SubmitEvent } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { useForm, useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { DialogClose, DialogFooter } from '@/components/ui/dialog'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { Textarea } from '@/components/ui/textarea'
import { AMOUNT_PLACEHOLDER, CURRENCY, TRANSFER_NOTE_MAX_LENGTH } from '../constants/wallet.constants'
import { usePaymentRequestActions } from '../hooks/usePaymentRequestActions'
import { transferSchema } from '../schemas/transfer.schema'
import type { TransferFormInput, TransferFormValues } from '../types/transfer.types'
import { getTransferFieldErrors } from '../utils/get-transfer-field-errors'
import { RecipientCombobox } from './RecipientCombobox'
import { RecipientPreview } from './RecipientPreview'

interface RequestMoneyFormProps {
  onSuccess: () => void
}

const DEFAULT_VALUES: TransferFormInput = {
  recipient: '',
  amount: '',
  note: '',
}

export function RequestMoneyForm({ onSuccess }: RequestMoneyFormProps) {
  const { t } = useTranslation('wallet')
  const { create } = usePaymentRequestActions()
  const form = useForm<TransferFormInput, unknown, TransferFormValues>({
    resolver: zodResolver(transferSchema),
    defaultValues: DEFAULT_VALUES,
    mode: 'onTouched',
  })

  const recipient = useWatch({ control: form.control, name: 'recipient' })

  const submit = form.handleSubmit((values) => {
    create.mutate(values, {
      onSuccess: () => {
        form.reset(DEFAULT_VALUES)
        onSuccess()
      },
      onError: (error) => {
        getTransferFieldErrors(error).forEach(({ field, key }) => {
          form.setError(field, { message: key }, { shouldFocus: true })
        })
      },
    })
  })

  const onSubmit = (event: SubmitEvent<HTMLFormElement>) => {
    void submit(event)
  }

  return (
    <form onSubmit={onSubmit} noValidate className="grid gap-4">
      <FieldGroup>
        <FormField control={form.control} name="recipient" label={t('requests.payer')} description={t('transfer.recipientHint')}>
          {(props) => <RecipientCombobox {...props} />}
        </FormField>
        <RecipientPreview value={recipient} />
        <FormField control={form.control} name="amount" label={t('topUp.amount', { currency: CURRENCY })}>
          {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
        </FormField>
        <FormField control={form.control} name="note" label={t('transfer.note')}>
          {(props) => <Textarea {...props} rows={2} maxLength={TRANSFER_NOTE_MAX_LENGTH} placeholder={t('transfer.notePlaceholder')} />}
        </FormField>
        {create.isError && getTransferFieldErrors(create.error).length === 0 && <FormErrorMessage errorKey="unexpected" />}
      </FieldGroup>
      <DialogFooter>
        <DialogClose asChild>
          <Button type="button" variant="outline">
            {t('topUp.cancel')}
          </Button>
        </DialogClose>
        <Button type="submit" disabled={create.isPending} aria-busy={create.isPending}>
          {create.isPending && <Spinner data-icon="inline-start" />}
          {t('requests.submit')}
        </Button>
      </DialogFooter>
    </form>
  )
}
