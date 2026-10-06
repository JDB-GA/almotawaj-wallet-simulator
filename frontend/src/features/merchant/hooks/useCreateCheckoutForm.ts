import type { SubmitEvent } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getErrorKey } from '@/lib/api/get-error-key'
import { checkoutSessionApi } from '../api/checkout-session.api'
import { MERCHANT_QUERY_KEYS } from '../constants/merchant.constants'
import { createCheckoutSchema } from '../schemas/create-checkout.schema'
import type { CheckoutSession, CreateCheckoutInput, CreateCheckoutValues } from '../types/merchant.types'
import { getCheckoutFieldErrors } from '../utils/get-checkout-field-errors'

const DEFAULT_VALUES: CreateCheckoutInput = { orderReference: '', amount: '', description: '', returnUrl: '' }

export function useCreateCheckoutForm(onCreated: (session: CheckoutSession) => void) {
  const queryClient = useQueryClient()
  const create = useMutation({ mutationFn: checkoutSessionApi.create })
  const form = useForm<CreateCheckoutInput, unknown, CreateCheckoutValues>({
    resolver: zodResolver(createCheckoutSchema),
    defaultValues: DEFAULT_VALUES,
    mode: 'onTouched',
  })

  const submit = form.handleSubmit((values) => {
    create.mutate(values, {
      onSuccess: (session) => {
        form.reset(DEFAULT_VALUES)
        void queryClient.invalidateQueries({ queryKey: MERCHANT_QUERY_KEYS.sessions })
        onCreated(session)
      },
      onError: (error) => {
        getCheckoutFieldErrors(error).forEach(({ field, key }) => {
          form.setError(field, { message: key }, { shouldFocus: true })
        })
      },
    })
  })

  const hasFieldErrors = create.isError && getCheckoutFieldErrors(create.error).length > 0

  return {
    form,
    onSubmit: (event: SubmitEvent<HTMLFormElement>) => {
      void submit(event)
    },
    isPending: create.isPending,
    errorKey: create.isError && !hasFieldErrors ? getErrorKey(create.error) : null,
  }
}
