import { createServer } from 'node:http'
import { config } from './config.ts'
import { GatewayError } from './gateway.ts'
import { sendHtml } from './http.ts'
import { findHandler } from './routes.ts'
import { notFoundPage, problemPage } from './views/result.ts'

const server = createServer((request, response) => {
  const url = new URL(request.url ?? '/', config.shopUrl)
  const handler = findHandler(request.method ?? 'GET', url.pathname)
  if (!handler) {
    sendHtml(response, 404, notFoundPage())
    return
  }
  handler(request, response, url).catch((error: unknown) => {
    if (error instanceof GatewayError) {
      sendHtml(response, 502, problemPage(error.problem))
      return
    }
    console.error(error)
    sendHtml(response, 500, problemPage({ status: 500, code: 'SHOP_ERROR', detail: 'The shop could not reach the payment gateway.' }))
  })
})

server.listen(config.port, () => {
  console.log(`Souq Sample Shop is running at ${config.shopUrl}`)
})
