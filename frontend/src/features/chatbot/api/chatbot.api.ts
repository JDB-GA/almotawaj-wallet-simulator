import { env } from '@/config/env'
import i18n from '@/i18n'
import { DEFAULT_LANGUAGE } from '@/i18n/languages'
import { ApiError } from '@/lib/api/api-error'
import { CONTENT_TYPE_JSON } from '@/lib/api/http.constants'
import { requestJson } from '@/lib/api/http-client'
import { CHATBOT_ENDPOINTS, CHATBOT_STREAM_EVENTS, CONTENT_TYPE_EVENT_STREAM } from '../constants/chatbot.constants'
import { chatAnswerSchema, chatDeltaSchema, chatDoneSchema } from '../schemas/chatbot.schema'
import type { ChatAnswer, ChatStreamHandlers, ServerSentEvent } from '../types/chatbot.types'
import { splitServerSentEvents } from '../utils/parse-server-sent-events'

const handleEvent = ({ event, data }: ServerSentEvent, handlers: ChatStreamHandlers) => {
  if (event === CHATBOT_STREAM_EVENTS.delta) handlers.onDelta(chatDeltaSchema.parse(JSON.parse(data)).text)
  if (event === CHATBOT_STREAM_EVENTS.done) handlers.onDone(chatDoneSchema.parse(JSON.parse(data)).sources)
  if (event === CHATBOT_STREAM_EVENTS.error) throw new ApiError(503, {})
}

export const chatbotApi = {
  ask: (question: string): Promise<ChatAnswer> =>
    requestJson(CHATBOT_ENDPOINTS.ask, chatAnswerSchema, { method: 'POST', body: { question } }),
  stream: async (question: string, handlers: ChatStreamHandlers, signal?: AbortSignal): Promise<void> => {
    const response = await fetch(`${env.apiUrl}${CHATBOT_ENDPOINTS.stream}`, {
      method: 'POST',
      signal,
      credentials: 'include',
      headers: {
        Accept: CONTENT_TYPE_EVENT_STREAM,
        'Content-Type': CONTENT_TYPE_JSON,
        'Accept-Language': i18n.resolvedLanguage ?? DEFAULT_LANGUAGE,
      },
      body: JSON.stringify({ question }),
    })
    if (!response.ok || !response.body) {
      throw new ApiError(response.status, {})
    }
    const reader = response.body.pipeThrough(new TextDecoderStream()).getReader()
    let buffer = ''
    for (;;) {
      const { value, done } = await reader.read()
      if (done) break
      const { events, rest } = splitServerSentEvents(buffer + value)
      buffer = rest
      events.forEach((event) => {
        handleEvent(event, handlers)
      })
    }
  },
}
