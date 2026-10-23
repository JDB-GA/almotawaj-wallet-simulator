import { useState } from 'react'
import { PlayIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Spinner } from '@/components/ui/spinner'
import { BUYERS_MAX, BUYERS_MIN, DEFAULT_BUYERS, DEFAULT_STOCK, MS_DECIMALS, SIMULATION_MODES, STOCK_MAX, STOCK_MIN, VOUCHER_SHARED_STEPS } from '../constants/simulation.constants'
import { useSimulations } from '../hooks/useSimulations'
import type { SimulationMode } from '../types/simulation.types'
import { NumberField } from './NumberField'
import { RaceReplay } from './RaceReplay'
import { StatTile } from './StatTile'
import { ThreadTimeline } from './ThreadTimeline'

export function VoucherRacePanel() {
  const { t } = useTranslation('simulation')
  const { voucherRace } = useSimulations()
  const [mode, setMode] = useState<SimulationMode>('SAFE')
  const [buyers, setBuyers] = useState(DEFAULT_BUYERS)
  const [stock, setStock] = useState(DEFAULT_STOCK)
  const result = voucherRace.data

  return (
    <div className="flex flex-col gap-4">
      <p className="text-sm text-muted-foreground">{t('voucher.intro')}</p>
      <div className="flex flex-wrap gap-2" role="radiogroup" aria-label={t('voucher.mode')}>
        {SIMULATION_MODES.map((option) => (
          <Button
            key={option}
            type="button"
            role="radio"
            aria-checked={mode === option}
            variant={mode === option ? (option === 'UNSAFE' ? 'destructive' : 'default') : 'outline'}
            onClick={() => {
              setMode(option)
            }}
          >
            {t(`mode.${option}`)}
          </Button>
        ))}
      </div>
      <p className="text-sm">{t(`modeHint.${mode}`)}</p>
      <div className="grid gap-4 sm:grid-cols-[1fr_1fr_auto] sm:items-end">
        <NumberField label={t('voucher.buyers')} value={buyers} min={BUYERS_MIN} max={BUYERS_MAX} onChange={setBuyers} />
        <NumberField label={t('voucher.stock')} value={stock} min={STOCK_MIN} max={STOCK_MAX} onChange={setStock} />
        <Button
          disabled={voucherRace.isPending}
          aria-busy={voucherRace.isPending}
          onClick={() => {
            voucherRace.mutate({ mode, buyers, stock })
          }}
        >
          {voucherRace.isPending ? <Spinner data-icon="inline-start" /> : <PlayIcon data-icon="inline-start" aria-hidden="true" />}
          {t('run')}
        </Button>
      </div>

      {result && (
        <section className="flex flex-col gap-4" aria-live="polite">
          <div className="grid grid-cols-2 gap-2 sm:grid-cols-5">
            <StatTile label={t('voucher.stockLabel')} value={String(result.stock)} />
            <StatTile label={t('voucher.buyersLabel')} value={String(result.buyers)} />
            <StatTile label={t('voucher.winners')} value={String(result.winners)} tone={result.oversold > 0 ? 'bad' : 'good'} />
            <StatTile label={t('voucher.oversold')} value={String(result.oversold)} tone={result.oversold > 0 ? 'bad' : 'good'} />
            <StatTile label={t('duration')} value={`${result.durationMs.toFixed(MS_DECIMALS)} ms`} />
          </div>
          <Alert variant={result.oversold > 0 ? 'destructive' : 'default'}>
            <AlertTitle>{result.oversold > 0 ? t('voucher.oversoldTitle') : t('voucher.safeTitle')}</AlertTitle>
            <AlertDescription>
              {result.oversold > 0
                ? t('voucher.oversoldBody', { winners: result.winners, stock: result.stock })
                : t('voucher.safeBody', { winners: result.winners, losers: result.buyers - result.winners })}
            </AlertDescription>
          </Alert>
          <ThreadTimeline
            attempts={result.attempts}
            rowLabel={(attempt) => attempt.thread ?? t('voucher.buyer', { number: attempt.index + 1 })}
          />
          <RaceReplay
            key={result.durationMs}
            attempts={result.attempts}
            rowLabel={(attempt) => attempt.thread ?? t('voucher.buyer', { number: attempt.index + 1 })}
            sharedLabel={t('voucher.stockLabel')}
            sharedStart={result.stock}
            sharedSteps={VOUCHER_SHARED_STEPS}
            formatValue={String}
          />
        </section>
      )}
    </div>
  )
}
