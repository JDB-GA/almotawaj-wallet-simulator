import type { z } from 'zod'
import type { chatAnswerSchema } from '../schemas/chatbot.schema'

export type ChatAnswer = z.infer<typeof chatAnswerSchema>

export type ChatRole = 'user' | 'assistant'

export interface ChatMessage {
  id: string
  role: ChatRole
  text: string
  sources: readonly string[]
  failed?: boolean
}
