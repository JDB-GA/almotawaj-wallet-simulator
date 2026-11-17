import { requestJson } from '@/lib/api/http-client'
import { CHATBOT_ENDPOINTS } from '../constants/chatbot.constants'
import { chatAnswerSchema } from '../schemas/chatbot.schema'
import type { ChatAnswer } from '../types/chatbot.types'

export const chatbotApi = {
  ask: (question: string): Promise<ChatAnswer> =>
    requestJson(CHATBOT_ENDPOINTS.ask, chatAnswerSchema, { method: 'POST', body: { question } }),
}
