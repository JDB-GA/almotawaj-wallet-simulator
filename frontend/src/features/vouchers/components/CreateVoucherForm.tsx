import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { DialogClose, DialogFooter } from '@/components/ui/dialog'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { AMOUNT_PLACEHOLDER, CURRENCY } from '@/features/wallet/constants/wallet.constants'
import { QUANTITY_PLACEHOLDER, VOUCHER_TITLE_MAX_LENGTH } from '../constants/voucher.constants'
import { useCreateVoucherForm } from '../hooks/useCreateVoucherForm'

interface CreateVoucherFormProps {
  onCreated: () => void
}

export function CreateVoucherForm({ onCreated }: CreateVoucherFormProps) {
  const { t } = useTranslation('vouchers')
  const { form, onSubmit, isPending, errorKey } = useCreateVoucherForm(onCreated)

  return (
    <form onSubmit={onSubmit} noValidate className="grid gap-4">
      <FieldGroup>
        <FormField control={form.control} name="title" label={t('form.title')}>
          {(props) => <Input {...props} maxLength={VOUCHER_TITLE_MAX_LENGTH} placeholder={t('form.titlePlaceholder')} />}
        </FormField>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField control={form.control} name="price" label={t('form.price', { currency: CURRENCY })}>
            {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
          </FormField>
          <FormField control={form.control} name="faceValue" label={t('form.faceValue', { currency: CURRENCY })}>
            {(props) => <Input {...props} dir="ltr" inputMode="decimal" autoComplete="off" placeholder={AMOUNT_PLACEHOLDER} />}
          </FormField>
          <FormField control={form.control} name="totalQuantity" label={t('form.quantity')} description={t('form.quantityHint')}>
            {(props) => <Input {...props} dir="ltr" inputMode="numeric" autoComplete="off" placeholder={QUANTITY_PLACEHOLDER} />}
          </FormField>
          <FormField control={form.control} name="perClientLimit" label={t('form.perClientLimit')}>
            {(props) => <Input {...props} dir="ltr" inputMode="numeric" autoComplete="off" />}
          </FormField>
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <FormField control={form.control} name="saleEndsAt" label={t('form.saleEndsAt')}>
            {(props) => <Input {...props} type="datetime-local" dir="ltr" />}
          </FormField>
          <FormField control={form.control} name="expiresAt" label={t('form.expiresAt')} description={t('form.expiresAtHint')}>
            {(props) => <Input {...props} type="datetime-local" dir="ltr" />}
          </FormField>
        </div>
        <FormErrorMessage errorKey={errorKey} />
      </FieldGroup>
      <DialogFooter>
        <DialogClose asChild>
          <Button type="button" variant="outline">
            {t('form.cancel')}
          </Button>
        </DialogClose>
        <Button type="submit" disabled={isPending} aria-busy={isPending}>
          {isPending && <Spinner data-icon="inline-start" />}
          {t('form.create')}
        </Button>
      </DialogFooter>
    </form>
  )
}
