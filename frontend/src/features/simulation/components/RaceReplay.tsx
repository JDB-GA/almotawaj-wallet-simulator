import { useEffect, useRef, useState } from 'react'
import { PauseIcon, PlayIcon, RotateCcwIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import {
  DEFAULT_REPLAY_SLOWDOWN,
  MS_DECIMALS,
  PERCENT,
  REPLAY_SCRUB_STEP,
  REPLAY_SLOWDOWNS,
  STEP_BADGE_CLASSES,
} from '../constants/simulation.constants'
import type { SimulationAttempt, SimulationStep, SimulationStepType } from '../types/simulation.types'

interface RaceReplayProps {
  attempts: readonly SimulationAttempt[]
  rowLabel: (attempt: SimulationAttempt) => string
  sharedLabel: string
  sharedStart: number
  sharedSteps: readonly SimulationStepType[]
  formatValue: (value: number) => string
}

const formatMs = (value: number) => value.toFixed(MS_DECIMALS)

const lastStepAt = (steps: readonly SimulationStep[], clock: number) => steps.filter((step) => step.atMs <= clock).at(-1)

export function RaceReplay({ attempts, rowLabel, sharedLabel, sharedStart, sharedSteps, formatValue }: RaceReplayProps) {
  const { t } = useTranslation('simulation')
  const total = Math.max(...attempts.map((attempt) => attempt.endMs), 1)
  const [clock, setClock] = useState(0)
  const [playing, setPlaying] = useState(true)
  const [slowdown, setSlowdown] = useState<number>(DEFAULT_REPLAY_SLOWDOWN)
  const frame = useRef<number | null>(null)
  const clockRef = useRef(0)

  useEffect(() => {
    if (!playing) {
      return
    }
    let previous = performance.now()
    const tick = (now: number) => {
      const elapsed = (now - previous) / slowdown
      previous = now
      clockRef.current = Math.min(clockRef.current + elapsed, total)
      setClock(clockRef.current)
      if (clockRef.current >= total) {
        setPlaying(false)
        return
      }
      frame.current = requestAnimationFrame(tick)
    }
    frame.current = requestAnimationFrame(tick)
    return () => {
      if (frame.current !== null) {
        cancelAnimationFrame(frame.current)
      }
    }
  }, [playing, slowdown, total])

  const sharedEvents = attempts
    .flatMap((attempt) => attempt.steps)
    .filter((step) => sharedSteps.includes(step.type) && step.value !== null && step.atMs <= clock)
    .sort((first, second) => first.atMs - second.atMs)
  const sharedValue = sharedEvents.at(-1)?.value ?? sharedStart

  return (
    <figure className="flex flex-col gap-3 rounded-lg border p-3">
      <figcaption className="flex flex-wrap items-center justify-between gap-2">
        <span className="text-sm font-medium">{t('replay.title')}</span>
        <bdi dir="ltr" className="text-xs tabular-nums text-muted-foreground">
          {formatMs(clock)} / {formatMs(total)} ms
        </bdi>
      </figcaption>

      <div className="flex flex-wrap items-center gap-2">
        <Button
          size="sm"
          variant="outline"
          aria-label={playing ? t('replay.pause') : t('replay.play')}
          onClick={() => {
            if (clock >= total) {
              clockRef.current = 0
              setClock(0)
            }
            setPlaying(!playing)
          }}
        >
          {playing ? <PauseIcon aria-hidden="true" /> : <PlayIcon aria-hidden="true" />}
        </Button>
        <Button
          size="sm"
          variant="outline"
          aria-label={t('replay.restart')}
          onClick={() => {
            clockRef.current = 0
            setClock(0)
            setPlaying(true)
          }}
        >
          <RotateCcwIcon aria-hidden="true" />
        </Button>
        <div className="flex gap-1" role="radiogroup" aria-label={t('replay.speed')}>
          {REPLAY_SLOWDOWNS.map((option) => (
            <Button
              key={option}
              size="sm"
              role="radio"
              aria-checked={slowdown === option}
              variant={slowdown === option ? 'default' : 'ghost'}
              onClick={() => {
                setSlowdown(option)
              }}
            >
              {t('replay.slower', { times: option })}
            </Button>
          ))}
        </div>
        <div className="ms-auto flex items-center gap-2 rounded-md border px-3 py-1">
          <span className="text-xs text-muted-foreground">{sharedLabel}</span>
          <bdi dir="ltr" className="text-lg font-semibold tabular-nums">
            {formatValue(sharedValue)}
          </bdi>
        </div>
      </div>

      <input
        type="range"
        className="w-full accent-primary"
        min={0}
        max={total}
        step={REPLAY_SCRUB_STEP}
        value={clock}
        aria-label={t('replay.scrub')}
        onChange={(event) => {
          setPlaying(false)
          clockRef.current = Number(event.target.value)
          setClock(clockRef.current)
        }}
      />

      <ol className="flex flex-col gap-1">
        {attempts.map((attempt) => {
          const step = lastStepAt(attempt.steps, clock)
          const started = clock >= attempt.startMs
          const done = clock >= attempt.endMs
          const progress = started ? Math.min((clock - attempt.startMs) / Math.max(attempt.endMs - attempt.startMs, 1), 1) : 0
          return (
            <li key={attempt.index} className="grid grid-cols-[7rem_1fr_8rem] items-center gap-2 text-xs">
              <bdi dir="ltr" className="truncate font-mono text-muted-foreground">
                {rowLabel(attempt)}
              </bdi>
              <div className="relative h-2 rounded bg-muted" dir="ltr">
                <div
                  className={cn('absolute top-0 h-full rounded bg-primary/60 transition-none', done && 'bg-primary')}
                  style={{ width: `${(progress * PERCENT).toString()}%` }}
                />
              </div>
              {step ? (
                <span className={cn('truncate rounded px-2 py-0.5 text-center font-medium', STEP_BADGE_CLASSES[step.type])}>
                  {t(`step.${step.type}`)}
                  {step.value !== null && (
                    <bdi dir="ltr" className="ms-1 tabular-nums">
                      {formatValue(step.value)}
                    </bdi>
                  )}
                </span>
              ) : (
                <span className="text-center text-muted-foreground">{started ? t('replay.running') : t('replay.waiting')}</span>
              )}
            </li>
          )
        })}
      </ol>
    </figure>
  )
}
