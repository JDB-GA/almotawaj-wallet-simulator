import type { CheckoutSession, Order, Product } from './types.ts'

const orders = new Map<string, Order>()

export const orderStore = {
  add: (product: Product, session: CheckoutSession): Order => {
    const order: Order = {
      reference: session.orderReference,
      sessionId: session.id,
      product,
      status: session.status,
      checkoutUrl: session.checkoutUrl,
      lastCallback: null,
    }
    orders.set(session.id, order)
    return order
  },

  find: (sessionId: string): Order | undefined => orders.get(sessionId),

  list: (): Order[] => [...orders.values()].reverse(),

  sync: (session: CheckoutSession): Order | undefined => {
    const order = orders.get(session.id)
    if (order) {
      order.status = session.status
    }
    return order
  },

  recordCallback: (sessionId: string, event: string): void => {
    const order = orders.get(sessionId)
    if (order) {
      order.lastCallback = event
    }
  },
}

export const newOrderReference = (): string => `SHOP-${Date.now().toString(36).toUpperCase()}`
