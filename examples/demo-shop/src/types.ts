export type CheckoutStatus = 'PENDING' | 'PAID' | 'CANCELLED' | 'EXPIRED' | 'REFUNDED'

export interface Product {
  id: string
  name: string
  description: string
  price: number
}

export interface CheckoutSession {
  id: string
  orderReference: string
  amount: number
  description: string | null
  status: CheckoutStatus
  payerName: string | null
  checkoutUrl: string
  returnUrl: string | null
  expiresAt: string
  paidAt: string | null
  refundedAt: string | null
  createdAt: string
}

export interface Order {
  reference: string
  sessionId: string
  product: Product
  status: CheckoutStatus
  checkoutUrl: string
  lastCallback: string | null
}

export interface CallbackEvent {
  event: string
  sessionId: string
  orderReference: string
  amount: number
  status: CheckoutStatus
  occurredAt: string
}

export interface Problem {
  status: number
  code: string
  detail: string
  errors?: Record<string, string>
}
