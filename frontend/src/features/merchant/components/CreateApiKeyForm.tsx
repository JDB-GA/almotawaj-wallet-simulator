import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { DialogClose, DialogFooter } from '@/components/ui/dialog'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { API_KEY_NAME_MAX_LENGTH } from '../constants/merchant.constants'
import { useCreateApiKeyForm } from '../hooks/useCreateApiKeyForm'
import type { ApiKeyCreated } from '../types/merchant.types'

interface CreateApiKeyFormProps {
  onCreated: (created: ApiKeyCreated) => void
}

export function CreateApiKeyForm({ onCreated }: CreateApiKeyFormProps) {
  const { t } = useTranslation('merchant')
  const { form, onSubmit, isPending, errorKey } = useCreateApiKeyForm(onCreated)

  return (
    <form onSubmit={onSubmit} noValidate className="grid gap-4">
      <FieldGroup>
        <FormField control={form.control} name="name" label={t('keys.name')} description={t('keys.nameHint')}>
          {(props) => <Input {...props} autoComplete="off" maxLength={API_KEY_NAME_MAX_LENGTH} placeholder={t('keys.namePlaceholder')} />}
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
          {t('keys.create')}
        </Button>
      </DialogFooter>
    </form>
  )
}
