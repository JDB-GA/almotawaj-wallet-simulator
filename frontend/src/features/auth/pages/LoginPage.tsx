import { useTranslation } from 'react-i18next'
import { ROUTES } from '@/config/routes'
import { AuthFooterLink } from '../components/AuthFooterLink'
import { AuthLayout } from '../components/AuthLayout'
import { LoginForm } from '../components/LoginForm'
import { LoginShowcase } from '../components/LoginShowcase'

export function LoginPage() {
  const { t } = useTranslation('auth')

  return (
    <AuthLayout
      title={t('login.title')}
      description={t('login.description')}
      showcase={<LoginShowcase />}
      footer={<AuthFooterLink prompt={t('login.noAccount')} label={t('login.registerLink')} to={ROUTES.register} />}
    >
      <LoginForm />
    </AuthLayout>
  )
}
