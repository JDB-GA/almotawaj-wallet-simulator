import { SSE_DATA_PREFIX, SSE_EVENT_PREFIX, SSE_EVENT_SEPARATOR } from '../constants/chatbot.constants'
import type { ServerSentEvent } from '../types/chatbot.types'

const parseBlock = (block: string): ServerSentEvent | null => {
  let event = ''
  const data: string[] = []
  for (const line of block.split('\n')) {
    if (line.startsWith(SSE_EVENT_PREFIX)) event = line.slice(SSE_EVENT_PREFIX.length).trim()
    if (line.startsWith(SSE_DATA_PREFIX)) data.push(line.slice(SSE_DATA_PREFIX.length).trim())
  }
  return event ? { event, data: data.join('\n') } : null
}

export function splitServerSentEvents(buffer: string): { events: ServerSentEvent[]; rest: string } {
  const blocks = buffer.replace(/\r\n/g, '\n').split(SSE_EVENT_SEPARATOR)
  const rest = blocks.pop() ?? ''
  const events = blocks.map(parseBlock).filter((event): event is ServerSentEvent => event !== null)
  return { events, rest }
}
