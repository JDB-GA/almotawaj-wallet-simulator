import { useEffect } from 'react'
import { RETURN_DELAY_MS } from '../constants/checkout.constants'
import type { Checkout } from '../types/checkout.types'
import { getReturnHost } from '../utils/get-return-host'

export function useReturnToMerchant(checkout: Checkout | undefined, shouldRedirect: boolean) {
  const returnUrl = checkout?.returnUrl ?? null
  const host = getReturnHost(returnUrl)

  useEffect(() => {
    if (!shouldRedirect || returnUrl === null || host === null) {
      return
    }
    const timer = setTimeout(() => {
      window.location.assign(returnUrl)
    }, RETURN_DELAY_MS)
    return () => {
      clearTimeout(timer)
    }
  }, [shouldRedirect, returnUrl, host])

  return host === null ? null : { host, url: returnUrl }
}
