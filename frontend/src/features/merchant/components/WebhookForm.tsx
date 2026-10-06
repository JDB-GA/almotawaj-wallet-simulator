import { useTranslation } from 'react-i18next'
import { FormErrorMessage } from '@/components/form/FormErrorMessage'
import { FormField } from '@/components/form/FormField'
import { Button } from '@/components/ui/button'
import { FieldGroup } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { Spinner } from '@/components/ui/spinner'
import { URL_MAX_LENGTH } from '../constants/merchant.constants'
import { useWebhookForm } from '../hooks/useWebhookForm'
import type { Webhook } from '../types/merchant.types'
import { CopyableValue } from './CopyableValue'

interface WebhookFormProps {
  webhook: Webhook
}

export function WebhookForm({ webhook }: WebhookFormProps) {
  const { t } = useTranslation('merchant')
  const { form, onSubmit, handleRemove, isPending, errorKey } = useWebhookForm(webhook)

  return (
    <form onSubmit={onSubmit} noValidate className="grid gap-4">
      <FieldGroup>
        <FormField control={form.control} name="url" label={t('webhook.url')} description={t('webhook.urlHint')}>
          {(props) => (
            <Input
              {...props}
              type="url"
              dir="ltr"
              autoComplete="off"
              maxLength={URL_MAX_LENGTH}
              placeholder={t('webhook.urlPlaceholder')}
            />
          )}
        </FormField>
        <FormErrorMessage errorKey={errorKey} />
      </FieldGroup>
      {webhook.signingSecret && (
        <div className="flex flex-col gap-2">
          <p className="text-sm font-medium">{t('webhook.secret')}</p>
          <CopyableValue value={webhook.signingSecret} />
          <p className="text-xs text-muted-foreground">{t('webhook.secretHint')}</p>
        </div>
      )}
      <div className="flex flex-wrap gap-2">
        <Button type="submit" disabled={isPending || !form.formState.isDirty} aria-busy={isPending}>
          {isPending && <Spinner data-icon="inline-start" />}
          {t('webhook.save')}
        </Button>
        {webhook.url && (
          <Button type="button" variant="ghost" disabled={isPending} onClick={handleRemove}>
            {t('webhook.remove')}
          </Button>
        )}
      </div>
    </form>
  )
}
