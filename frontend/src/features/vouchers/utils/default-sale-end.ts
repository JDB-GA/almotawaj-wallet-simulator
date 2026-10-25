import { DEFAULT_VOUCHER_SALE_DAYS } from '../constants/voucher.constants'

const DATETIME_LOCAL_LENGTH = 16
const MS_PER_DAY = 86_400_000
const MS_PER_MINUTE = 60_000

export const defaultSaleEnd = (): string => {
  const saleEnd = new Date(Date.now() + DEFAULT_VOUCHER_SALE_DAYS * MS_PER_DAY)
  return new Date(saleEnd.getTime() - saleEnd.getTimezoneOffset() * MS_PER_MINUTE).toISOString().slice(0, DATETIME_LOCAL_LENGTH)
}
