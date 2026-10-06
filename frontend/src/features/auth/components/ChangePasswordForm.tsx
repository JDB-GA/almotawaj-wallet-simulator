import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { SubmitButton } from '@/components/form/SubmitButton'
import { FieldGroup } from '@/components/ui/field'
import { useChangePasswordForm } from '../hooks/useChangePasswordForm'
import { PasswordInput } from './PasswordInput'

export function ChangePasswordForm() {
  const { t } = useTranslation('auth')
  const { form, onSubmit, isPending, errorKey } = useChangePasswordForm()

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        <FormField control={form.control} name="currentPassword" label={t('change.currentPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="current-password" placeholder={t('fields.passwordPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="newPassword" label={t('change.newPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.newPasswordPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="confirmPassword" label={t('fields.confirmPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.confirmPasswordPlaceholder')} />}
        </FormField>
        <FormErrorMessage errorKey={errorKey} />
        <SubmitButton isPending={isPending}>{t('change.submit')}</SubmitButton>
      </FieldGroup>
    </form>
  )
}
