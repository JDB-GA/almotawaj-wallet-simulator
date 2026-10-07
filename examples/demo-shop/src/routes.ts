import type { IncomingMessage, ServerResponse } from 'node:http'
import { findProduct } from './catalog.ts'
import { PATHS, QUICK_EXPIRY_MINUTES, SIGNATURE_HEADER, config } from './config.ts'
import { gateway } from './gateway.ts'
import { readBody, readForm, redirect, sendHtml, sendStatus } from './http.ts'
import { newOrderReference, orderStore } from './orders.ts'
import { isValidSignature } from './signature.ts'
import type { CallbackEvent } from './types.ts'
import { ordersPage } from './views/orders.ts'
import { notFoundPage, resultPage } from './views/result.ts'
import { shopPage } from './views/shop.ts'

type Handler = (request: IncomingMessage, response: ServerResponse, url: URL) => Promise<void>

const buy: Handler = async (request, response) => {
  const form = await readForm(request)
  const product = findProduct(form.get('productId') ?? '')
  if (!product) {
    sendHtml(response, 404, notFoundPage())
    return
  }
  const session = await gateway.createSession({
    orderReference: newOrderReference(),
    amount: product.price,
    description: product.name,
    returnUrl: `${config.shopUrl}${PATHS.complete}`,
    expiresInMinutes: form.get('quickExpiry') === 'true' ? QUICK_EXPIRY_MINUTES : undefined,
  })
  orderStore.add(product, session)
  redirect(response, session.checkoutUrl)
}

const complete: Handler = async (_request, response, url) => {
  const order = orderStore.sync(await gateway.getSession(url.searchParams.get('sessionId') ?? ''))
  sendHtml(response, order ? 200 : 404, order ? resultPage(order) : notFoundPage())
}

const listOrders: Handler = async (_request, response) => {
  const sessions = await Promise.all(orderStore.list().map((order) => gateway.getSession(order.sessionId)))
  sessions.forEach(orderStore.sync)
  sendHtml(response, 200, ordersPage(orderStore.list(), config.signingSecret !== null))
}

const changeOrder: Handler = async (_request, response, url) => {
  const [, , sessionId = '', operation] = url.pathname.split('/')
  orderStore.sync(operation === 'refund' ? await gateway.refundSession(sessionId) : await gateway.cancelSession(sessionId))
  redirect(response, PATHS.orders)
}

const webhook: Handler = async (request, response) => {
  const body = await readBody(request)
  const signature = request.headers[SIGNATURE_HEADER]
  if (!config.signingSecret || !isValidSignature(config.signingSecret, body, typeof signature === 'string' ? signature : undefined)) {
    sendStatus(response, 401)
    return
  }
  const event = JSON.parse(body) as CallbackEvent
  orderStore.recordCallback(event.sessionId, event.event)
  console.log(`callback ${event.event} for ${event.orderReference}`)
  sendStatus(response, 200)
}

const home: Handler = (_request, response) => {
  sendHtml(response, 200, shopPage())
  return Promise.resolve()
}

const PAGES: Record<string, Handler> = { [PATHS.home]: home, [PATHS.complete]: complete, [PATHS.orders]: listOrders }
const ORDER_ACTION = /^\/orders\/[^/]+\/(cancel|refund)$/

export const findHandler = (method: string, path: string): Handler | undefined => {
  if (method === 'GET') {
    return PAGES[path]
  }
  if (method !== 'POST') {
    return undefined
  }
  if (path === PATHS.buy) {
    return buy
  }
  if (path === PATHS.webhook) {
    return webhook
  }
  return ORDER_ACTION.test(path) ? changeOrder : undefined
}
