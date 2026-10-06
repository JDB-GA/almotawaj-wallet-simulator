import type { SubmitEvent } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { getErrorKey } from '@/lib/api/get-error-key'
import { webhookApi } from '../api/webhook.api'
import { MERCHANT_QUERY_KEYS } from '../constants/merchant.constants'
import { webhookFormSchema } from '../schemas/webhook.schema'
import type { Webhook, WebhookFormValues } from '../types/merchant.types'

export function useWebhookForm(webhook: Webhook) {
  const { t } = useTranslation(['merchant', 'errors'])
  const queryClient = useQueryClient()
  const save = useMutation({ mutationFn: webhookApi.set })
  const remove = useMutation({ mutationFn: webhookApi.remove })
  const form = useForm<WebhookFormValues>({
    resolver: zodResolver(webhookFormSchema),
    defaultValues: { url: webhook.url ?? '' },
    mode: 'onTouched',
  })

  const submit = form.handleSubmit((values) => {
    save.mutate(values, {
      onSuccess: (saved) => {
        toast.success(t('merchant:webhook.saved'))
        queryClient.setQueryData(MERCHANT_QUERY_KEYS.webhook, saved)
        form.reset({ url: saved.url ?? '' })
      },
    })
  })

  const handleRemove = () => {
    remove.mutate(undefined, {
      onSuccess: () => {
        toast.success(t('merchant:webhook.removed'))
        form.reset({ url: '' })
        void queryClient.invalidateQueries({ queryKey: MERCHANT_QUERY_KEYS.webhook })
      },
      onError: (error) => toast.error(t(`errors:${getErrorKey(error)}`)),
    })
  }

  return {
    form,
    onSubmit: (event: SubmitEvent<HTMLFormElement>) => {
      void submit(event)
    },
    handleRemove,
    isPending: save.isPending || remove.isPending,
    errorKey: save.isError ? getErrorKey(save.error) : null,
  }
}
