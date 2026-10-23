import { requestJson } from '@/lib/api/http-client'
import { SIMULATION_ENDPOINTS } from '../constants/simulation.constants'
import { payoutRaceResultSchema, voucherRaceResultSchema } from '../schemas/simulation.schema'
import type { PayoutRaceInput, PayoutRaceResult, VoucherRaceInput, VoucherRaceResult } from '../types/simulation.types'

export const simulationApi = {
  voucherRace: (input: VoucherRaceInput): Promise<VoucherRaceResult> =>
    requestJson(SIMULATION_ENDPOINTS.voucherRace, voucherRaceResultSchema, { method: 'POST', body: input }),

  payoutRace: (input: PayoutRaceInput): Promise<PayoutRaceResult> =>
    requestJson(SIMULATION_ENDPOINTS.payoutRace, payoutRaceResultSchema, { method: 'POST', body: input }),
}
