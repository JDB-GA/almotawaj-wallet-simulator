import { z } from 'zod'
import { SIMULATION_MODES, SIMULATION_OUTCOMES } from '../constants/simulation.constants'

export const simulationModeSchema = z.enum(SIMULATION_MODES)
export const simulationOutcomeSchema = z.enum(SIMULATION_OUTCOMES)

export const simulationAttemptSchema = z.object({
  index: z.number().int(),
  thread: z.string().nullable(),
  startMs: z.number(),
  endMs: z.number(),
  outcome: simulationOutcomeSchema,
  detail: z.string().nullable(),
})

export const voucherRaceResultSchema = z.object({
  mode: simulationModeSchema,
  buyers: z.number().int(),
  stock: z.number().int(),
  winners: z.number().int(),
  remainingAfter: z.number().int(),
  oversold: z.number().int(),
  durationMs: z.number(),
  attempts: z.array(simulationAttemptSchema),
})

export const payoutRaceResultSchema = z.object({
  recipients: z.number().int(),
  amount: z.number(),
  startingBalance: z.number(),
  finalBalance: z.number(),
  paid: z.number().int(),
  failed: z.number().int(),
  threadsUsed: z.number().int(),
  durationMs: z.number(),
  attempts: z.array(simulationAttemptSchema),
})
