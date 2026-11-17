import { z } from 'zod'

export const chatAnswerSchema = z.object({
  answer: z.string(),
  sources: z.array(z.string()),
})
