import { useTranslation } from 'react-i18next'
import { Badge } from '@/components/ui/badge'
import { KycDetailRow } from '@/features/kyc/components/KycDetailRow'
import { CHECKOUT_STATUS_BADGE_VARIANTS } from '@/features/merchant/constants/merchant.constants'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import type { Checkout } from '../types/checkout.types'
import { CheckoutCountdown } from './CheckoutCountdown'

interface CheckoutSummaryProps {
  checkout: Checkout
}

export function CheckoutSummary({ checkout }: CheckoutSummaryProps) {
  const { t } = useTranslation('merchant')
  const { language } = useLanguage()

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col items-center gap-2 rounded-xl bg-muted/50 p-6 text-center">
        <span className="text-sm text-muted-foreground">{t('checkout.amount')}</span>
        <bdi dir="ltr" className="text-3xl font-semibold tabular-nums">
          {formatMoney(checkout.amount, language)}
        </bdi>
        <Badge variant={CHECKOUT_STATUS_BADGE_VARIANTS[checkout.status]}>{t(`status.${checkout.status}`)}</Badge>
      </div>
      <dl className="grid gap-x-4 gap-y-6 sm:grid-cols-2">
        <KycDetailRow label={t('checkout.merchant')} value={checkout.merchantName} dir="ltr" />
        <KycDetailRow label={t('payments.order')} value={checkout.orderReference} dir="ltr" />
        {checkout.description && <KycDetailRow label={t('payments.descriptionLabel')} value={checkout.description} />}
      </dl>
      {checkout.status === 'PENDING' && <CheckoutCountdown checkout={checkout} />}
    </div>
  )
}
