import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { DialogClose, DialogFooter } from '@/components/ui/dialog'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { Textarea } from '@/components/ui/textarea'
import { AMOUNT_PLACEHOLDER, CURRENCY, TRANSFER_NOTE_MAX_LENGTH } from '@/features/wallet/constants/wallet.constants'
import { ORDER_REFERENCE_MAX_LENGTH, URL_MAX_LENGTH } from '../constants/merchant.constants'
import { useCreateCheckoutForm } from '../hooks/useCreateCheckoutForm'
import type { CheckoutSession } from '../types/merchant.types'

interface CreateCheckoutFormProps {
  onCreated: (session: CheckoutSession) => void
}

export function CreateCheckoutForm({ onCreated }: CreateCheckoutFormProps) {
  const { t } = useTranslation('merchant')
  const { form, onSubmit, isPending, errorKey } = useCreateCheckoutForm(onCreated)

  return (
    <form onSubmit={onSubmit} noValidate className="grid gap-4">
      <FieldGroup>
        <FormField control={form.control} name="orderReference" label={t('payments.order')} description={t('payments.orderHint')}>
          {(props) => (
            <Input
              {...props}
              dir="ltr"
              autoComplete="off"
              maxLength={ORDER_REFERENCE_MAX_LENGTH}
              placeholder={t('payments.orderPlaceholder')}
            />
          )}
        </FormField>
        <FormField control={form.control} name="amount" label={t('payments.amountIn', { currency: CURRENCY })}>
          {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
        </FormField>
        <FormField control={form.control} name="description" label={t('payments.descriptionLabel')}>
          {(props) => (
            <Textarea {...props} rows={2} maxLength={TRANSFER_NOTE_MAX_LENGTH} placeholder={t('payments.descriptionPlaceholder')} />
          )}
        </FormField>
        <FormField control={form.control} name="returnUrl" label={t('payments.returnUrl')} description={t('payments.returnUrlHint')}>
          {(props) => (
            <Input
              {...props}
              type="url"
              dir="ltr"
              autoComplete="off"
              maxLength={URL_MAX_LENGTH}
              placeholder={t('payments.returnUrlPlaceholder')}
            />
          )}
        </FormField>
        <FormErrorMessage errorKey={errorKey} />
      </FieldGroup>
      <DialogFooter>
        <DialogClose asChild>
          <Button type="button" variant="outline">
            {t('cancel')}
          </Button>
        </DialogClose>
        <Button type="submit" disabled={isPending} aria-busy={isPending}>
          {isPending && <Spinner data-icon="inline-start" />}
          {t('payments.create')}
        </Button>
      </DialogFooter>
    </form>
  )
}
