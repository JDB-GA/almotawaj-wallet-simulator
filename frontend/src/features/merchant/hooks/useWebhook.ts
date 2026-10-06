import { useQuery } from '@tanstack/react-query'
import { webhookApi } from '../api/webhook.api'
import { MERCHANT_QUERY_KEYS } from '../constants/merchant.constants'

export function useWebhook() {
  return useQuery({
    queryKey: MERCHANT_QUERY_KEYS.webhook,
    queryFn: ({ signal }) => webhookApi.get(signal),
  })
}
