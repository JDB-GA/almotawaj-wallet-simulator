export const CHATBOT_DOCUMENT_ENDPOINTS = {
  list: '/api/admin/chat/documents',
  item: (id: string) => `/api/admin/chat/documents/${encodeURIComponent(id)}`,
} as const

export const CHATBOT_DOCUMENT_QUERY_KEYS = {
  list: ['chatbot-documents', 'list'],
} as const

export const CHATBOT_DOCUMENT_ACCEPT = '.md,.txt'
export const CHATBOT_DOCUMENT_FIELD = 'file'
export const CHATBOT_DOCUMENT_STATES = ['STATE_UNSPECIFIED', 'STATE_PENDING', 'STATE_ACTIVE', 'STATE_FAILED'] as const
export const BYTES_PER_KB = 1024
