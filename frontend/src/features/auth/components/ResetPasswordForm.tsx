import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { SubmitButton } from '@/components/form/SubmitButton'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { useResetPasswordForm } from '../hooks/useResetPasswordForm'
import { OtpCodeInput } from './OtpCodeInput'
import { PasswordInput } from './PasswordInput'
import { ResendResetCodeButton } from './ResendResetCodeButton'

export function ResetPasswordForm() {
  const { t } = useTranslation('auth')
  const { form, onSubmit, isPending, errorKey } = useResetPasswordForm()

  const getValidEmail = async (): Promise<string | null> =>
    (await form.trigger('email', { shouldFocus: true })) ? form.getValues('email') : null

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        <FormField control={form.control} name="email" label={t('fields.email')}>
          {(props) => <Input {...props} type="email" dir="ltr" autoComplete="email" placeholder={t('fields.emailPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="code" label={t('reset.codeLabel')}>
          {(props) => <OtpCodeInput {...props} disabled={isPending} />}
        </FormField>
        <FormField control={form.control} name="newPassword" label={t('reset.newPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.newPasswordPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="confirmPassword" label={t('fields.confirmPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.confirmPasswordPlaceholder')} />}
        </FormField>
        <FormErrorMessage errorKey={errorKey} />
        <SubmitButton isPending={isPending}>{t('reset.submit')}</SubmitButton>
        <ResendResetCodeButton getEmail={getValidEmail} />
      </FieldGroup>
    </form>
  )
}
