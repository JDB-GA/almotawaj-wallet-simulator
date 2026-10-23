import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { LOW_STOCK_THRESHOLD } from '../constants/voucher.constants'

interface StockMeterProps {
  remaining: number
  total: number
}

const PERCENT = 100

export function StockMeter({ remaining, total }: StockMeterProps) {
  const { t } = useTranslation('vouchers')
  const percent = total > 0 ? Math.round((remaining / total) * PERCENT) : 0
  const isLow = remaining > 0 && remaining <= LOW_STOCK_THRESHOLD

  return (
    <div className="flex flex-col gap-1">
      <div
        className="h-1.5 w-full overflow-hidden rounded-full bg-muted"
        role="meter"
        aria-valuemin={0}
        aria-valuemax={total}
        aria-valuenow={remaining}
        aria-label={t('stock.label')}
      >
        <div className={cn('h-full rounded-full bg-primary transition-[width]', isLow && 'bg-destructive')} style={{ width: `${percent.toString()}%` }} />
      </div>
      <p className={cn('text-xs text-muted-foreground tabular-nums', isLow && 'font-medium text-destructive')}>
        {remaining === 0 ? t('stock.soldOut') : t('stock.left', { remaining, total })}
      </p>
    </div>
  )
}
