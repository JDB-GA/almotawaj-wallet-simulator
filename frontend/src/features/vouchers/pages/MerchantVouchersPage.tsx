import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardAction, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { CreateVoucherDialog } from '../components/CreateVoucherDialog'
import { MerchantVouchersList } from '../components/MerchantVouchersList'

export function MerchantVouchersPage() {
  const { t } = useTranslation(['common', 'vouchers'])

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.merchantVouchers.title')} />
      <Card>
        <CardHeader>
          <CardTitle>
            <h1>{t('areas.merchantVouchers.heading')}</h1>
          </CardTitle>
          <CardDescription>{t('vouchers:manage.description')}</CardDescription>
          <CardAction>
            <CreateVoucherDialog />
          </CardAction>
        </CardHeader>
        <CardContent>
          <MerchantVouchersList />
        </CardContent>
      </Card>
    </div>
  )
}
