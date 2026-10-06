import { useTranslation } from 'react-i18next'
import { Link } from 'react-router'
import { ConfirmActionDialog } from '@/components/ConfirmActionDialog'
import { LoadErrorAlert } from '@/components/LoadErrorAlert'
import { Button } from '@/components/ui/button'
import { CardContent, CardFooter } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { ROUTES } from '@/config/routes'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { isApiError } from '@/lib/api/api-error'
import { HTTP_STATUS } from '@/lib/api/http.constants'
import { useCheckout } from '../hooks/useCheckout'
import { usePayCheckout } from '../hooks/usePayCheckout'
import { useReturnToMerchant } from '../hooks/useReturnToMerchant'
import { CheckoutSummary } from './CheckoutSummary'

interface CheckoutContentProps {
  sessionId: string
}

export function CheckoutContent({ sessionId }: CheckoutContentProps) {
  const { t } = useTranslation('merchant')
  const { language } = useLanguage()
  const { data, isPending, isError, error, refetch } = useCheckout(sessionId)
  const pay = usePayCheckout(sessionId)
  const merchantSite = useReturnToMerchant(data, pay.isSuccess)

  if (isPending) {
    return (
      <CardContent>
        <Skeleton className="h-56 w-full rounded-xl" />
      </CardContent>
    )
  }

  if (isError) {
    const isMissing = isApiError(error) && (error.status === HTTP_STATUS.notFound || error.status === HTTP_STATUS.badRequest)
    return (
      <CardContent>
        <LoadErrorAlert
          message={isMissing ? t('checkout.notFound') : t('checkout.loadError')}
          onRetry={() => {
            void refetch()
          }}
        />
      </CardContent>
    )
  }

  const values = { amount: formatMoney(data.amount, language), merchant: data.merchantName }

  return (
    <>
      <CardContent>
        <CheckoutSummary checkout={data} />
        {pay.isSuccess && merchantSite && (
          <p className="mt-4 text-center text-sm text-muted-foreground" role="status">
            {t('checkout.returning', { site: merchantSite.host })}
          </p>
        )}
      </CardContent>
      <CardFooter className="justify-end gap-2">
        {merchantSite?.url ? (
          <Button asChild variant="outline">
            <a href={merchantSite.url} rel="noreferrer">
              {t('checkout.returnTo', { site: merchantSite.host })}
            </a>
          </Button>
        ) : (
          <Button asChild variant="outline">
            <Link to={ROUTES.wallet}>{t('checkout.backToWallet')}</Link>
          </Button>
        )}
        {data.status === 'PENDING' && (
          <ConfirmActionDialog
            disabled={pay.isPending}
            trigger={t('checkout.pay', values)}
            title={t('checkout.confirmTitle')}
            description={t('checkout.confirmDescription', values)}
            confirmLabel={t('checkout.confirm')}
            cancelLabel={t('cancel')}
            onConfirm={() => {
              pay.mutate()
            }}
          />
        )}
      </CardFooter>
    </>
  )
}
