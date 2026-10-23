import type { z } from 'zod'
import type {
  payoutRaceResultSchema,
  simulationAttemptSchema,
  simulationModeSchema,
  simulationOutcomeSchema,
  simulationStepSchema,
  simulationStepTypeSchema,
  voucherRaceResultSchema,
} from '../schemas/simulation.schema'

export type SimulationMode = z.infer<typeof simulationModeSchema>
export type SimulationOutcome = z.infer<typeof simulationOutcomeSchema>
export type SimulationStepType = z.infer<typeof simulationStepTypeSchema>
export type SimulationStep = z.infer<typeof simulationStepSchema>
export type SimulationAttempt = z.infer<typeof simulationAttemptSchema>
export type VoucherRaceResult = z.infer<typeof voucherRaceResultSchema>
export type PayoutRaceResult = z.infer<typeof payoutRaceResultSchema>

export interface VoucherRaceInput {
  mode: SimulationMode
  buyers: number
  stock: number
}

export interface PayoutRaceInput {
  recipients: number
  amount: number
  merchantBalance: number
}
