import { useTranslation } from 'react-i18next'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { useRegisterForm } from '../hooks/useRegisterForm'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { MobileNumberInput } from './MobileNumberInput'
import { PasswordInput } from './PasswordInput'
import { SubmitButton } from '@/components/form/SubmitButton'

export function RegisterForm() {
  const { t } = useTranslation('auth')
  const { form, onSubmit, isPending, errorKey } = useRegisterForm()

  return (
    <form onSubmit={onSubmit} noValidate>
      <FieldGroup>
        <FormField control={form.control} name="email" label={t('fields.email')}>
          {(props) => (
            <Input {...props} type="email" dir="ltr" autoComplete="email" placeholder={t('fields.emailPlaceholder')} />
          )}
        </FormField>
        <FormField control={form.control} name="mobileNumber" label={t('fields.mobileNumber')}>
          {(props) => <MobileNumberInput {...props} placeholder={t('fields.mobilePlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="password" label={t('fields.password')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.newPasswordPlaceholder')} />}
        </FormField>
        <FormField control={form.control} name="confirmPassword" label={t('fields.confirmPassword')}>
          {(props) => <PasswordInput {...props} autoComplete="new-password" placeholder={t('fields.confirmPasswordPlaceholder')} />}
        </FormField>
        <FormErrorMessage errorKey={errorKey} />
        <SubmitButton isPending={isPending}>{t('register.submit')}</SubmitButton>
      </FieldGroup>
    </form>
  )
}
