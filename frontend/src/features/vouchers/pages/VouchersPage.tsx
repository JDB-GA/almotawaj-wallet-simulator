import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { VoucherPurchasesList } from '../components/VoucherPurchasesList'
import { VoucherShop } from '../components/VoucherShop'
import { MINE_TAB, SHOP_TAB } from '../constants/voucher.constants'

export function VouchersPage() {
  const { t } = useTranslation(['common', 'vouchers'])

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.vouchers.title')} />
      <Card>
        <CardHeader>
          <CardTitle>
            <h1>{t('areas.vouchers.heading')}</h1>
          </CardTitle>
          <CardDescription>{t('vouchers:shop.description')}</CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue={SHOP_TAB}>
            <TabsList>
              <TabsTrigger value={SHOP_TAB}>{t('vouchers:tabs.shop')}</TabsTrigger>
              <TabsTrigger value={MINE_TAB}>{t('vouchers:tabs.mine')}</TabsTrigger>
            </TabsList>
            <TabsContent value={SHOP_TAB} className="pt-4">
              <VoucherShop />
            </TabsContent>
            <TabsContent value={MINE_TAB} className="pt-4">
              <VoucherPurchasesList />
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>
    </div>
  )
}
