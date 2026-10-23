import { TicketIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import { cn } from '@/lib/utils'
import type { UsableVoucher } from '../types/checkout.types'

interface CheckoutVoucherPickerProps {
  vouchers: readonly UsableVoucher[]
  selectedId: string | null
  onSelect: (voucherId: string | null) => void
}

export function CheckoutVoucherPicker({ vouchers, selectedId, onSelect }: CheckoutVoucherPickerProps) {
  const { t } = useTranslation('merchant')
  const { language } = useLanguage()
  const options = [null, ...vouchers]

  return (
    <fieldset className="flex flex-col gap-2">
      <legend className="mb-2 text-sm font-medium">{t('checkout.voucher.title')}</legend>
      {options.map((voucher) => {
        const id = voucher?.id ?? null
        const checked = selectedId === id
        return (
          <label
            key={id ?? 'none'}
            className={cn(
              'flex cursor-pointer items-center gap-3 rounded-lg border p-3 text-sm transition-colors',
              checked && 'border-primary bg-primary/5',
            )}
          >
            <input
              type="radio"
              name="voucher"
              className="accent-primary"
              checked={checked}
              onChange={() => {
                onSelect(id)
              }}
            />
            {voucher ? (
              <>
                <TicketIcon className="size-4 text-primary" aria-hidden="true" />
                <span className="flex min-w-0 flex-1 flex-col">
                  <span className="truncate font-medium">{voucher.title}</span>
                  <bdi dir="ltr" className="truncate font-mono text-xs text-muted-foreground">
                    {voucher.code}
                  </bdi>
                </span>
                <bdi dir="ltr" className="tabular-nums">
                  {formatMoney(voucher.faceValue, language)}
                </bdi>
              </>
            ) : (
              <span>{t('checkout.voucher.none')}</span>
            )}
          </label>
        )
      })}
    </fieldset>
  )
}
