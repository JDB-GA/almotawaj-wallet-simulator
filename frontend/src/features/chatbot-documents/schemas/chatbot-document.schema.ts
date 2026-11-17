import { z } from 'zod'
import { CHATBOT_DOCUMENT_STATES } from '../constants/chatbot-documents.constants'

export const chatbotDocumentSchema = z.object({
  id: z.string(),
  name: z.string(),
  sizeBytes: z.number(),
  state: z.enum(CHATBOT_DOCUMENT_STATES),
  createdAt: z.iso.datetime({ offset: true }).nullable(),
})

export const chatbotDocumentListSchema = z.array(chatbotDocumentSchema)
