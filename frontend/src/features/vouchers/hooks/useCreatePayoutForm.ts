import type { SubmitEvent } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useFieldArray, useForm } from 'react-hook-form'
import { getErrorKey } from '@/lib/api/get-error-key'
import { payoutApi } from '../api/payout.api'
import { PAYOUT_QUERY_KEYS } from '../constants/voucher.constants'
import { payoutFormSchema } from '../schemas/voucher-form.schema'
import type { PayoutBatch, PayoutFormInput, PayoutFormValues } from '../types/voucher.types'

const EMPTY_LINE = { recipient: '', amount: '' }
const emptyValues = (): PayoutFormInput => ({ items: [{ ...EMPTY_LINE }] })

export function useCreatePayoutForm(onStarted: (batch: PayoutBatch) => void) {
  const queryClient = useQueryClient()
  const create = useMutation({ mutationFn: payoutApi.create })
  const form = useForm<PayoutFormInput, unknown, PayoutFormValues>({
    resolver: zodResolver(payoutFormSchema),
    defaultValues: emptyValues(),
    mode: 'onTouched',
  })
  const lines = useFieldArray({ control: form.control, name: 'items' })

  const submit = form.handleSubmit((values) => {
    create.mutate(values, {
      onSuccess: (batch) => {
        form.reset(emptyValues())
        queryClient.setQueryData(PAYOUT_QUERY_KEYS.batch(batch.id), batch)
        void queryClient.invalidateQueries({ queryKey: PAYOUT_QUERY_KEYS.all })
        onStarted(batch)
      },
    })
  })

  return {
    form,
    lines,
    addLine: () => { lines.append({ ...EMPTY_LINE }); },
    onSubmit: (event: SubmitEvent<HTMLFormElement>) => {
      void submit(event)
    },
    isPending: create.isPending,
    errorKey: create.isError ? getErrorKey(create.error) : null,
  }
}
