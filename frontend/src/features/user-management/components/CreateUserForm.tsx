import { useWatch } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { SubmitButton } from '@/components/form/SubmitButton'
import { Field, FieldDescription, FieldGroup, FieldTitle } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { MobileNumberInput } from '@/features/auth/components/MobileNumberInput'
import type { User } from '@/features/auth/types/user.types'
import { useCreateUserForm } from '../hooks/useCreateUserForm'
import { getCreatableRoles } from '../utils/get-creatable-roles'
import { PermissionCheckboxes } from './PermissionCheckboxes'
import { RoleSelect } from './RoleSelect'

interface CreateUserFormProps {
  actor: User
}

export function CreateUserForm({ actor }: CreateUserFormProps) {
  const { t } = useTranslation('users')
  const { form, onSubmit, isPending, errorKey } = useCreateUserForm()
  const role = useWatch({ control: form.control, name: 'role' })
  const permissions = useWatch({ control: form.control, name: 'permissions' })
  const showPermissions = actor.role === 'SUPER_ADMIN' && role === 'ADMIN'

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        <FormField control={form.control} name="email" label={t('create.email')}>
          {(props) => <Input {...props} type="email" dir="ltr" autoComplete="off" placeholder={t('contact.emailPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="mobileNumber" label={t('create.mobileNumber')}>
          {(props) => <MobileNumberInput {...props} placeholder={t('contact.mobilePlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="role" label={t('create.role')}>
          {(props) => <RoleSelect {...props} roles={getCreatableRoles(actor)} />}
        </FormField>
        {showPermissions && (
          <Field>
            <FieldTitle>{t('create.permissions')}</FieldTitle>
            <FieldDescription>{t('create.permissionsHint')}</FieldDescription>
            <PermissionCheckboxes
              value={permissions}
              disabled={isPending}
              onChange={(next) => {
                form.setValue('permissions', next, { shouldDirty: true })
              }}
            />
          </Field>
        )}
        <FormErrorMessage errorKey={errorKey} />
        <SubmitButton isPending={isPending}>{t('create.submit')}</SubmitButton>
      </FieldGroup>
    </form>
  )
}
