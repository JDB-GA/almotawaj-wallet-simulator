import type { IncomingMessage, ServerResponse } from 'node:http'
import { MAX_BODY_BYTES } from './config.ts'

export const readBody = async (request: IncomingMessage): Promise<string> => {
  const chunks: Buffer[] = []
  let size = 0
  for await (const chunk of request) {
    const buffer = chunk as Buffer
    size += buffer.length
    if (size > MAX_BODY_BYTES) {
      throw new Error('Request body is too large')
    }
    chunks.push(buffer)
  }
  return Buffer.concat(chunks).toString('utf8')
}

export const readForm = async (request: IncomingMessage): Promise<URLSearchParams> => new URLSearchParams(await readBody(request))

export const sendHtml = (response: ServerResponse, status: number, html: string): void => {
  response.writeHead(status, {
    'Content-Type': 'text/html; charset=utf-8',
    'Content-Security-Policy': "default-src 'none'; style-src 'unsafe-inline'; base-uri 'none'",
    'X-Content-Type-Options': 'nosniff',
    'Referrer-Policy': 'no-referrer',
  })
  response.end(html)
}

export const redirect = (response: ServerResponse, location: string): void => {
  response.writeHead(303, { Location: location })
  response.end()
}

export const sendStatus = (response: ServerResponse, status: number): void => {
  response.writeHead(status)
  response.end()
}
