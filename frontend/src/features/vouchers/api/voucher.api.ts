import { requestJson } from '@/lib/api/http-client'
import {
  CREATED_AT_DESC,
  IDEMPOTENCY_HEADER,
  PURCHASES_PAGE_SIZE,
  VOUCHER_ENDPOINTS,
  VOUCHERS_PAGE_SIZE,
} from '../constants/voucher.constants'
import { voucherPageSchema, voucherPurchasePageSchema, voucherPurchaseSchema, voucherSchema } from '../schemas/voucher.schema'
import type {
  CreateVoucherValues,
  UpdateVoucherPayload,
  Voucher,
  VoucherPage,
  VoucherPurchase,
  VoucherPurchasePage,
} from '../types/voucher.types'

const pageParams = (page: number, size: number): string =>
  new URLSearchParams({ page: String(page), size: String(size), sort: CREATED_AT_DESC }).toString()

export const voucherApi = {
  available: (page: number, signal?: AbortSignal): Promise<VoucherPage> =>
    requestJson(`${VOUCHER_ENDPOINTS.available}?${pageParams(page, VOUCHERS_PAGE_SIZE)}`, voucherPageSchema, { signal }),

  purchases: (page: number, signal?: AbortSignal): Promise<VoucherPurchasePage> =>
    requestJson(`${VOUCHER_ENDPOINTS.purchases}?${pageParams(page, PURCHASES_PAGE_SIZE)}`, voucherPurchasePageSchema, { signal }),

  purchase: ({ voucherId, idempotencyKey }: { voucherId: string; idempotencyKey: string }): Promise<VoucherPurchase> =>
    requestJson(VOUCHER_ENDPOINTS.purchase(voucherId), voucherPurchaseSchema, {
      method: 'POST',
      headers: { [IDEMPOTENCY_HEADER]: idempotencyKey },
    }),

  mine: (page: number, signal?: AbortSignal): Promise<VoucherPage> =>
    requestJson(`${VOUCHER_ENDPOINTS.merchantVouchers}?${pageParams(page, VOUCHERS_PAGE_SIZE)}`, voucherPageSchema, { signal }),

  create: (values: CreateVoucherValues): Promise<Voucher> =>
    requestJson(VOUCHER_ENDPOINTS.merchantVouchers, voucherSchema, {
      method: 'POST',
      body: {
        ...values,
        saleEndsAt: new Date(values.saleEndsAt).toISOString(),
        expiresAt: values.expiresAt ? new Date(values.expiresAt).toISOString() : null,
      },
    }),

  update: ({ voucherId, payload }: { voucherId: string; payload: UpdateVoucherPayload }): Promise<Voucher> =>
    requestJson(VOUCHER_ENDPOINTS.merchantVoucher(voucherId), voucherSchema, { method: 'PUT', body: payload }),
}
