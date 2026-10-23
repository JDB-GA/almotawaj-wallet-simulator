import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { MIN_BAR_PERCENT, MS_DECIMALS, OUTCOME_BAR_CLASSES, PERCENT } from '../constants/simulation.constants'
import type { SimulationAttempt } from '../types/simulation.types'

interface ThreadTimelineProps {
  attempts: readonly SimulationAttempt[]
  rowLabel: (attempt: SimulationAttempt) => string
}

const formatMs = (value: number) => value.toFixed(MS_DECIMALS)

export function ThreadTimeline({ attempts, rowLabel }: ThreadTimelineProps) {
  const { t } = useTranslation('simulation')
  const total = Math.max(...attempts.map((attempt) => attempt.endMs), 1)

  return (
    <figure className="flex flex-col gap-2">
      <figcaption className="flex justify-between text-xs text-muted-foreground">
        <span>{t('timeline.title')}</span>
        <bdi dir="ltr" className="tabular-nums">
          0 – {formatMs(total)} ms
        </bdi>
      </figcaption>
      <ol className="flex flex-col gap-1">
        {attempts.map((attempt) => {
          const left = (attempt.startMs / total) * PERCENT
          const width = Math.max(((attempt.endMs - attempt.startMs) / total) * PERCENT, MIN_BAR_PERCENT)
          return (
            <li key={attempt.index} className="grid grid-cols-[7rem_1fr_5.5rem] items-center gap-2 text-xs">
              <bdi dir="ltr" className="truncate font-mono text-muted-foreground">
                {rowLabel(attempt)}
              </bdi>
              <div className="relative h-4 rounded bg-muted" dir="ltr">
                <div
                  className={cn('absolute top-0 h-full rounded', OUTCOME_BAR_CLASSES[attempt.outcome])}
                  style={{ left: `${left.toString()}%`, width: `${width.toString()}%` }}
                  title={`${formatMs(attempt.startMs)} → ${formatMs(attempt.endMs)} ms`}
                />
              </div>
              <span className={cn('truncate', (attempt.outcome === 'WON' || attempt.outcome === 'PAID') && 'font-medium text-primary')}>
                {t(`outcome.${attempt.outcome}`)}
              </span>
            </li>
          )
        })}
      </ol>
    </figure>
  )
}
