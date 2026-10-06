import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { DISPLAY_NAME_MAX_LENGTH } from '../constants/profile.constants'
import { useBusinessNameForm } from '../hooks/useBusinessNameForm'
import type { Profile } from '../types/profile.types'

interface BusinessNameCardProps {
  profile: Profile
}

export function BusinessNameCard({ profile }: BusinessNameCardProps) {
  const { t } = useTranslation('profile')
  const { form, onSubmit, isPending, errorKey } = useBusinessNameForm(profile)

  return (
    <Card>
      <CardHeader>
        <CardTitle>
          <h2>{t('business.title')}</h2>
        </CardTitle>
        <CardDescription>{t('business.description')}</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={onSubmit} noValidate className="grid gap-4">
          <FieldGroup>
            <FormField control={form.control} name="displayName" label={t('business.name')}>
              {(props) => <Input {...props} autoComplete="organization" maxLength={DISPLAY_NAME_MAX_LENGTH} placeholder={t('business.placeholder')} />}
            </FormField>
            <FormErrorMessage errorKey={errorKey} />
          </FieldGroup>
          <Button type="submit" className="justify-self-start" disabled={isPending || !form.formState.isDirty} aria-busy={isPending}>
            {isPending && <Spinner data-icon="inline-start" />}
            {t('business.save')}
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}
