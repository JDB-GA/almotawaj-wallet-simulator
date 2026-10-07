import { createHmac, timingSafeEqual } from 'node:crypto'

export const isValidSignature = (secret: string, body: string, received: string | undefined): boolean => {
  if (!received) {
    return false
  }
  const expected = createHmac('sha256', secret).update(body).digest('hex')
  return expected.length === received.length && timingSafeEqual(Buffer.from(expected), Buffer.from(received))
}
