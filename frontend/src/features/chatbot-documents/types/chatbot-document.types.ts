import type { z } from 'zod'
import type { chatbotDocumentSchema } from '../schemas/chatbot-document.schema'

export type ChatbotDocument = z.infer<typeof chatbotDocumentSchema>
