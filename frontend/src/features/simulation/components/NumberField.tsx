import { useId } from 'react'
import { Field, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'

interface NumberFieldProps {
  label: string
  value: number
  min: number
  max: number
  step?: number
  onChange: (value: number) => void
}

export function NumberField({ label, value, min, max, step = 1, onChange }: NumberFieldProps) {
  const id = useId()

  return (
    <Field>
      <FieldLabel htmlFor={id}>{label}</FieldLabel>
      <Input
        id={id}
        type="number"
        dir="ltr"
        inputMode="decimal"
        min={min}
        max={max}
        step={step}
        value={value}
        onChange={(event) => {
          const next = Number(event.target.value)
          onChange(Number.isNaN(next) ? min : Math.min(max, Math.max(min, next)))
        }}
      />
    </Field>
  )
}
