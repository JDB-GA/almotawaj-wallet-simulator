import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { MobileNumberInput } from '@/features/auth/components/MobileNumberInput'
import { useUserContactForm } from '../hooks/useUserContactForm'
import type { AdminUser } from '../types/user-management.types'

interface UserContactCardProps {
  user: AdminUser
  disabled: boolean
}

export function UserContactCard({ user, disabled }: UserContactCardProps) {
  const { t } = useTranslation('users')
  const { form, onSubmit, isPending, errorKey } = useUserContactForm(user)

  return (
    <Card>
      <CardHeader>
        <CardTitle>
          <h2>{t('contact.title')}</h2>
        </CardTitle>
        <CardDescription>{t('contact.description')}</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} noValidate>
          <fieldset disabled={disabled || isPending}>
            <FieldGroup>
              <FormField control={form.control} name="email" label={t('contact.email')}>
                {(props) => <Input {...props} type="email" dir="ltr" autoComplete="off" placeholder={t('contact.emailPlaceholder')} />}
              </FormField>
              <FormField control={form.control} name="mobileNumber" label={t('contact.mobileNumber')}>
                {(props) => <MobileNumberInput {...props} placeholder={t('contact.mobilePlaceholder')} />}
              </FormField>
              <FormErrorMessage errorKey={errorKey} />
              <Button type="submit" className="self-end" disabled={!form.formState.isDirty} aria-busy={isPending}>
                {isPending && <Spinner data-icon="inline-start" />}
                {t('contact.save')}
              </Button>
            </FieldGroup>
          </fieldset>
        </form>
      </CardContent>
    </Card>
  )
}
