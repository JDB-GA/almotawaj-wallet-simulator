export const CHATBOT_ENDPOINTS = {
  ask: '/api/chat',
  stream: '/api/chat/stream',
} as const

export const CHATBOT_QUESTION_MAX = 500
export const CHATBOT_SUGGESTIONS = ['topUp', 'transfer', 'kyc'] as const

export const CHATBOT_STREAM_EVENTS = {
  delta: 'delta',
  done: 'done',
  error: 'error',
} as const

export const SSE_EVENT_SEPARATOR = '\n\n'
export const SSE_EVENT_PREFIX = 'event:'
export const SSE_DATA_PREFIX = 'data:'
export const CONTENT_TYPE_EVENT_STREAM = 'text/event-stream'
