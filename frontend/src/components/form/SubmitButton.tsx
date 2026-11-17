import type { PropsWithChildren } from 'react'
import { Button } from '@/components/ui/button'
import { Spinner } from '@/components/ui/spinner'
import { cn } from '@/lib/utils'

interface SubmitButtonProps extends PropsWithChildren {
  isPending: boolean
  className?: string
}

export function SubmitButton({ isPending, className, children }: SubmitButtonProps) {
  return (
    <Button type="submit" size="lg" className={cn('w-full', className)} disabled={isPending} aria-busy={isPending}>
      {isPending && <Spinner data-icon="inline-start" />}
      {children}
    </Button>
  )
}
