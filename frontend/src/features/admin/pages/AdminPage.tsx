import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { useCurrentUser } from '@/features/auth/hooks/useCurrentUser'
import { WelcomeCard } from '@/features/home/components/WelcomeCard'
import { CleanSeedDataAction } from '../components/CleanSeedDataAction'
import { SystemTransactionsCard } from '../components/SystemTransactionsCard'
import { UserStatisticsCards } from '../components/UserStatisticsCards'

export function AdminPage() {
  const { t } = useTranslation()
  const { data: user } = useCurrentUser()

  if (!user) {
    return null
  }

  if (!user.permissions.includes('STATISTICS_VIEW')) {
    return (
      <>
        <PageTitle title={t('areas.admin.title')} />
        <WelcomeCard heading={t('areas.admin.heading')} />
      </>
    )
  }

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-col gap-4 lg:h-full lg:min-h-0">
      <PageTitle title={t('areas.admin.title')} />
      <div className="flex shrink-0 flex-wrap items-center justify-between gap-2">
        <h1 className="text-2xl font-semibold">{t('areas.admin.heading')}</h1>
        {user.role === 'SUPER_ADMIN' && <CleanSeedDataAction />}
      </div>
      <UserStatisticsCards />
      <SystemTransactionsCard />
    </div>
  )
}
