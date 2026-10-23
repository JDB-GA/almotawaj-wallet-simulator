import { cn } from '@/lib/utils'

interface StatTileProps {
  label: string
  value: string
  tone?: 'default' | 'good' | 'bad'
}

export function StatTile({ label, value, tone = 'default' }: StatTileProps) {
  return (
    <div className="flex flex-col gap-1 rounded-lg border p-3">
      <span className="text-xs text-muted-foreground">{label}</span>
      <bdi
        dir="ltr"
        className={cn('text-xl font-semibold tabular-nums', tone === 'good' && 'text-primary', tone === 'bad' && 'text-destructive')}
      >
        {value}
      </bdi>
    </div>
  )
}
