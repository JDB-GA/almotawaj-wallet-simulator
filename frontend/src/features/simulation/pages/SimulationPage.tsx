import { useTranslation } from 'react-i18next'
import { PageTitle } from '@/components/PageTitle'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { PayoutRacePanel } from '../components/PayoutRacePanel'
import { VoucherRacePanel } from '../components/VoucherRacePanel'
import { PAYOUT_TAB, VOUCHER_TAB } from '../constants/simulation.constants'

export function SimulationPage() {
  const { t } = useTranslation(['common', 'simulation'])

  return (
    <div className="mx-auto flex w-full max-w-5xl flex-col gap-4">
      <PageTitle title={t('areas.simulation.title')} />
      <Card>
        <CardHeader>
          <CardTitle>
            <h1>{t('areas.simulation.heading')}</h1>
          </CardTitle>
          <CardDescription>{t('simulation:description')}</CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs defaultValue={VOUCHER_TAB}>
            <TabsList>
              <TabsTrigger value={VOUCHER_TAB}>{t('simulation:tabs.voucher')}</TabsTrigger>
              <TabsTrigger value={PAYOUT_TAB}>{t('simulation:tabs.payout')}</TabsTrigger>
            </TabsList>
            <TabsContent value={VOUCHER_TAB} className="pt-4">
              <VoucherRacePanel />
            </TabsContent>
            <TabsContent value={PAYOUT_TAB} className="pt-4">
              <PayoutRacePanel />
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>
    </div>
  )
}
