import type { PropsWithChildren, ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { AppLogo } from '@/components/AppLogo'
import { LanguageSwitcher } from '@/components/LanguageSwitcher'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

interface AuthLayoutProps extends PropsWithChildren {
  title: string
  description: string
  footer: ReactNode
  showcase?: ReactNode
}

function AuthBackdrop() {
  return (
    <div aria-hidden="true" className="pointer-events-none absolute inset-0">
      <div className="absolute inset-0 bg-dot-grid text-foreground/[0.05]" />
      <div className="absolute -top-40 -start-40 size-[36rem] rounded-full bg-primary/25 blur-3xl motion-safe:animate-drift" />
      <div className="absolute -end-32 -bottom-48 size-[40rem] rounded-full bg-primary/20 blur-3xl motion-safe:animate-drift-delayed" />
      <div className="absolute top-1/3 start-1/2 size-[24rem] rounded-full bg-primary/10 blur-3xl" />
    </div>
  )
}

function AuthBrand() {
  const { t } = useTranslation()

  return (
    <div className="flex items-center justify-between">
      <span className="flex items-center gap-2 font-medium">
        <AppLogo />
        {t('appName')}
      </span>
      <LanguageSwitcher />
    </div>
  )
}

export function AuthLayout({ title, description, footer, showcase, children }: AuthLayoutProps) {
  if (showcase) {
    return (
      <main className="relative flex min-h-svh items-center justify-center overflow-hidden bg-background p-4 md:p-8">
        <PageTitle title={title} />
        <AuthBackdrop />
        <div className="dark relative grid w-full max-w-7xl overflow-hidden rounded-[2.25rem] bg-background p-3 text-foreground shadow-[0_40px_120px_-20px] shadow-primary/30 ring-1 ring-white/10 lg:min-h-[min(52rem,calc(100svh-4rem))] lg:grid-cols-2">
          <div className="flex flex-col p-6 md:p-10">
            <AuthBrand />
            <div className="flex flex-1 items-center justify-center py-12">
              <div className="flex w-full max-w-md flex-col gap-8 motion-safe:animate-in motion-safe:fade-in motion-safe:slide-in-from-bottom-4 motion-safe:duration-700">
                <div className="flex flex-col gap-2">
                  <h1 className="text-3xl font-medium tracking-tight">{title}</h1>
                  <p className="text-muted-foreground">{description}</p>
                </div>
                {children}
                <p className="text-center text-sm text-muted-foreground">{footer}</p>
              </div>
            </div>
          </div>
          {showcase}
        </div>
      </main>
    )
  }

  return (
    <main className="flex min-h-svh flex-col items-center justify-center bg-linear-to-b from-secondary to-background p-4 md:p-10">
      <PageTitle title={title} />
      <div className="flex w-full max-w-sm flex-col gap-6">
        <AuthBrand />
        <Card>
          <CardHeader className="text-center">
            <CardTitle className="text-xl">
              <h1>{title}</h1>
            </CardTitle>
            <CardDescription>{description}</CardDescription>
          </CardHeader>
          <CardContent>{children}</CardContent>
        </Card>
        <p className="text-center text-sm text-muted-foreground">{footer}</p>
      </div>
    </main>
  )
}
