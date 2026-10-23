import { useState } from 'react'
import { PlayIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Spinner } from '@/components/ui/spinner'
import { TOP_UP_MAX } from '@/features/wallet/constants/wallet.constants'
import { formatMoney } from '@/features/wallet/utils/format-money'
import { useLanguage } from '@/hooks/useLanguage'
import {
  DEFAULT_MERCHANT_BALANCE,
  DEFAULT_PAYOUT_AMOUNT,
  DEFAULT_RECIPIENTS,
  MS_DECIMALS,
  PAYOUT_AMOUNT_MAX,
  PAYOUT_AMOUNT_MIN,
  PAYOUT_SHARED_STEPS,
  RECIPIENTS_MAX,
  RECIPIENTS_MIN,
} from '../constants/simulation.constants'
import { useSimulations } from '../hooks/useSimulations'
import { NumberField } from './NumberField'
import { RaceReplay } from './RaceReplay'
import { StatTile } from './StatTile'
import { ThreadTimeline } from './ThreadTimeline'

export function PayoutRacePanel() {
  const { t } = useTranslation('simulation')
  const { language } = useLanguage()
  const { payoutRace } = useSimulations()
  const [recipients, setRecipients] = useState(DEFAULT_RECIPIENTS)
  const [amount, setAmount] = useState(DEFAULT_PAYOUT_AMOUNT)
  const [merchantBalance, setMerchantBalance] = useState(DEFAULT_MERCHANT_BALANCE)
  const result = payoutRace.data

  return (
    <div className="flex flex-col gap-4">
      <p className="text-sm text-muted-foreground">{t('payout.intro')}</p>
      <div className="grid gap-4 sm:grid-cols-[1fr_1fr_1fr_auto] sm:items-end">
        <NumberField label={t('payout.recipients')} value={recipients} min={RECIPIENTS_MIN} max={RECIPIENTS_MAX} onChange={setRecipients} />
        <NumberField label={t('payout.amount')} value={amount} min={PAYOUT_AMOUNT_MIN} max={PAYOUT_AMOUNT_MAX} onChange={setAmount} />
        <NumberField label={t('payout.balance')} value={merchantBalance} min={0} max={TOP_UP_MAX} onChange={setMerchantBalance} />
        <Button
          disabled={payoutRace.isPending}
          aria-busy={payoutRace.isPending}
          onClick={() => {
            payoutRace.mutate({ recipients, amount, merchantBalance })
          }}
        >
          {payoutRace.isPending ? <Spinner data-icon="inline-start" /> : <PlayIcon data-icon="inline-start" aria-hidden="true" />}
          {t('run')}
        </Button>
      </div>

      {result && (
        <section className="flex flex-col gap-4" aria-live="polite">
          <div className="grid grid-cols-2 gap-2 sm:grid-cols-5">
            <StatTile label={t('payout.startBalance')} value={formatMoney(result.startingBalance, language)} />
            <StatTile label={t('payout.endBalance')} value={formatMoney(result.finalBalance, language)} tone={result.finalBalance < 0 ? 'bad' : 'good'} />
            <StatTile label={t('payout.paid')} value={String(result.paid)} tone="good" />
            <StatTile label={t('payout.failed')} value={String(result.failed)} tone={result.failed > 0 ? 'bad' : 'default'} />
            <StatTile label={t('payout.threads')} value={String(result.threadsUsed)} />
          </div>
          <Alert>
            <AlertTitle>{t('payout.resultTitle')}</AlertTitle>
            <AlertDescription>
              {t('payout.resultBody', {
                paid: result.paid,
                failed: result.failed,
                threads: result.threadsUsed,
                duration: result.durationMs.toFixed(MS_DECIMALS),
              })}
            </AlertDescription>
          </Alert>
          <ThreadTimeline
            attempts={result.attempts}
            rowLabel={(attempt) => `#${String(attempt.index + 1)} ${attempt.thread ?? ''}`}
          />
          <RaceReplay
            key={result.durationMs}
            attempts={result.attempts}
            rowLabel={(attempt) => `#${String(attempt.index + 1)} ${attempt.thread ?? ''}`}
            sharedLabel={t('payout.balance')}
            sharedStart={result.startingBalance}
            sharedSteps={PAYOUT_SHARED_STEPS}
            formatValue={(value) => formatMoney(value, language)}
          />
        </section>
      )}
    </div>
  )
}
