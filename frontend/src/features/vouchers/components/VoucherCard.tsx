import { TicketIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Spinner } from '@/components/ui/spinner'
import { formatDate } from '../utils/format-date'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import type { Voucher } from '../types/voucher.types'
import { StockMeter } from './StockMeter'

interface VoucherCardProps {
  voucher: Voucher
  isBuying: boolean
  onBuy: (voucherId: string) => void
}

export function VoucherCard({ voucher, isBuying, onBuy }: VoucherCardProps) {
  const { t } = useTranslation('vouchers')
  const { language } = useLanguage()
  const saving = voucher.faceValue - voucher.price
  const soldOut = voucher.remaining === 0

  return (
    <Card className="flex flex-col">
      <CardHeader>
        <CardTitle className="flex items-start justify-between gap-2">
          <bdi dir="auto">{voucher.title}</bdi>
          <TicketIcon className="size-5 shrink-0 text-muted-foreground" aria-hidden="true" />
        </CardTitle>
        <CardDescription>
          <bdi dir="auto">{voucher.merchantName}</bdi>
        </CardDescription>
      </CardHeader>
      <CardContent className="flex flex-1 flex-col gap-3">
        <div className="flex items-baseline gap-2">
          <bdi dir="ltr" className="text-2xl font-semibold tabular-nums">
            {formatMoney(voucher.price, language)}
          </bdi>
          {saving > 0 && (
            <bdi dir="ltr" className="text-sm text-muted-foreground line-through tabular-nums">
              {formatMoney(voucher.faceValue, language)}
            </bdi>
          )}
        </div>
        {saving > 0 && <Badge variant="secondary">{t('shop.save', { amount: formatMoney(saving, language) })}</Badge>}
        <StockMeter remaining={voucher.remaining} total={voucher.totalQuantity} />
        <p className="text-xs text-muted-foreground">
          {t('shop.expires', { date: formatDate(voucher.expiresAt, language) })} · {t('shop.limit', { count: voucher.perClientLimit })}
        </p>
      </CardContent>
      <CardFooter>
        <Button className="w-full" disabled={soldOut || isBuying} aria-busy={isBuying} onClick={() => { onBuy(voucher.id); }}>
          {isBuying && <Spinner data-icon="inline-start" />}
          {soldOut ? t('stock.soldOut') : t('shop.buy')}
        </Button>
      </CardFooter>
    </Card>
  )
}
