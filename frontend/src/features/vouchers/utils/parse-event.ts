import type { z } from 'zod'

export function parseEvent<TSchema extends z.ZodType>(event: Event, schema: TSchema): z.infer<TSchema> | null {
  if (!(event instanceof MessageEvent) || typeof event.data !== 'string') {
    return null
  }

  try {
    const parsed = schema.safeParse(JSON.parse(event.data))
    return parsed.success ? parsed.data : null
  } catch {
    return null
  }
}
