import { z } from 'zod'

export const chatAnswerSchema = z.object({
  answer: z.string(),
  sources: z.array(z.string()),
})

export const chatDeltaSchema = z.object({
  text: z.string(),
})

export const chatDoneSchema = z.object({
  sources: z.array(z.string()),
})
