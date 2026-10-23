import type { SubmitEvent } from 'react'
import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { getErrorKey } from '@/lib/api/get-error-key'
import { voucherApi } from '../api/voucher.api'
import { VOUCHER_QUERY_KEYS } from '../constants/voucher.constants'
import { createVoucherSchema } from '../schemas/voucher-form.schema'
import type { CreateVoucherInput, CreateVoucherValues } from '../types/voucher.types'
import { defaultExpiry } from '../utils/default-expiry'

const emptyValues = (): CreateVoucherInput => ({
  title: '',
  price: '',
  faceValue: '',
  totalQuantity: '',
  perClientLimit: '1',
  expiresAt: defaultExpiry(),
})

export function useCreateVoucherForm(onCreated: () => void) {
  const queryClient = useQueryClient()
  const create = useMutation({ mutationFn: voucherApi.create })
  const form = useForm<CreateVoucherInput, unknown, CreateVoucherValues>({
    resolver: zodResolver(createVoucherSchema),
    defaultValues: emptyValues(),
    mode: 'onTouched',
  })

  const submit = form.handleSubmit((values) => {
    create.mutate(values, {
      onSuccess: () => {
        form.reset(emptyValues())
        void queryClient.invalidateQueries({ queryKey: VOUCHER_QUERY_KEYS.all })
        onCreated()
      },
    })
  })

  return {
    form,
    onSubmit: (event: SubmitEvent<HTMLFormElement>) => {
      void submit(event)
    },
    isPending: create.isPending,
    errorKey: create.isError ? getErrorKey(create.error) : null,
  }
}
