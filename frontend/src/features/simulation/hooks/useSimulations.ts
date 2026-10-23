import { useMutation } from '@tanstack/react-query'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { getErrorKey } from '@/lib/api/get-error-key'
import { simulationApi } from '../api/simulation.api'

export function useSimulations() {
  const { t } = useTranslation('errors')
  const onError = (error: Error) => toast.error(t(getErrorKey(error)))

  return {
    voucherRace: useMutation({ mutationFn: simulationApi.voucherRace, onError }),
    payoutRace: useMutation({ mutationFn: simulationApi.payoutRace, onError }),
  }
}
