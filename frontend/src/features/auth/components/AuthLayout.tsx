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
  illustration?: ReactNode
}

export function AuthLayout({ title, description, footer, illustration, children }: AuthLayoutProps) {
  const { t } = useTranslation()

  return (
    <main className="flex min-h-svh flex-col items-center justify-center bg-linear-to-b from-secondary to-background p-4 md:p-10">
      <PageTitle title={title} />
      <div className="flex w-full max-w-sm flex-col gap-6">
        <div className="flex items-center justify-between">
          <span className="flex items-center gap-2 font-medium">
            <AppLogo />
            {t('appName')}
          </span>
          <LanguageSwitcher />
        </div>
        <Card>
          <CardHeader className="text-center">
            {illustration}
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
