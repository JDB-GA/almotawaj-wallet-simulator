import { useTranslation } from 'react-i18next'
import { FormField } from '@/components/form/FormField'
import { FieldDescription, FieldGroup, FieldLegend, FieldSet } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { AREA_MAX_LENGTH, BLOCK_MAX_LENGTH, BUILDING_MAX_LENGTH, ROAD_MAX_LENGTH } from '../constants/kyc.constants'
import type { KycFormControl } from '../types/kyc.types'

interface KycAddressFieldsProps {
  control: KycFormControl
}

export function KycAddressFields({ control }: KycAddressFieldsProps) {
  const { t } = useTranslation('kyc')

  return (
    <FieldSet>
      <FieldLegend>{t('form.addressTitle')}</FieldLegend>
      <FieldDescription>{t('form.addressDescription')}</FieldDescription>
      <FieldGroup className="grid gap-4 sm:grid-cols-2 md:grid-cols-4">
        <FormField control={control} name="block" label={t('fields.block')}>
          {(props) => <Input {...props} dir="ltr" inputMode="numeric" maxLength={BLOCK_MAX_LENGTH} placeholder={t('fields.blockPlaceholder')} />}
        </FormField>
        <FormField control={control} name="road" label={t('fields.road')}>
          {(props) => <Input {...props} dir="ltr" inputMode="numeric" maxLength={ROAD_MAX_LENGTH} placeholder={t('fields.roadPlaceholder')} />}
        </FormField>
        <FormField control={control} name="building" label={t('fields.building')}>
          {(props) => <Input {...props} dir="ltr" maxLength={BUILDING_MAX_LENGTH} placeholder={t('fields.buildingPlaceholder')} />}
        </FormField>
        <FormField control={control} name="flat" label={t('fields.flat')}>
          {(props) => <Input {...props} dir="ltr" maxLength={BUILDING_MAX_LENGTH} placeholder={t('fields.flatPlaceholder')} />}
        </FormField>
      </FieldGroup>
      <FormField control={control} name="area" label={t('fields.area')}>
        {(props) => (
          <Input {...props} autoComplete="address-level2" maxLength={AREA_MAX_LENGTH} placeholder={t('fields.areaPlaceholder')} />
        )}
      </FormField>
    </FieldSet>
  )
}
