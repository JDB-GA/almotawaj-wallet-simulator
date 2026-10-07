import { PATHS } from '../config.ts'
import type { CheckoutStatus, Order, Problem } from '../types.ts'
import { escapeHtml, formatPrice, page } from './layout.ts'

interface Outcome {
  tone: 'success' | 'error' | 'info'
  heading: string
  message: string
}

const OUTCOMES: Record<CheckoutStatus, Outcome> = {
  PAID: { tone: 'success', heading: 'Payment received', message: 'Thank you. Your order is confirmed and will be prepared now.' },
  PENDING: { tone: 'info', heading: 'Payment not completed', message: 'We have not received your payment yet. You can go back and pay.' },
  CANCELLED: { tone: 'error', heading: 'Payment cancelled', message: 'This payment was cancelled. Nothing was charged.' },
  EXPIRED: { tone: 'error', heading: 'Payment expired', message: 'The payment was not completed in time. Please order again.' },
  REFUNDED: { tone: 'info', heading: 'Payment refunded', message: 'This order was refunded to your wallet.' },
}

export const resultPage = (order: Order): string => {
  const outcome = OUTCOMES[order.status]
  const payAgain = order.status === 'PENDING' ? `<a class="button" href="${escapeHtml(order.checkoutUrl)}">Go to payment</a>` : ''
  return page(
    outcome.heading,
    `<div class="card notice ${outcome.tone}">
       <h1>${outcome.heading}</h1>
       <p>${outcome.message}</p>
       <p class="muted">Order <code>${escapeHtml(order.reference)}</code> · ${escapeHtml(order.product.name)} · ${formatPrice(order.product.price)}</p>
       <p><span class="status ${order.status}">${order.status}</span></p>
       <div class="actions">${payAgain}<a class="button secondary" href="${PATHS.orders}">View orders</a></div>
     </div>`,
  )
}

export const problemPage = (problem: Problem): string => {
  const fields = Object.entries(problem.errors ?? {})
    .map(([field, message]) => `<li><code>${escapeHtml(field)}</code>: ${escapeHtml(message)}</li>`)
    .join('')
  return page(
    'Payment problem',
    `<div class="card notice error">
       <h1>We could not complete this step</h1>
       <p>${escapeHtml(problem.detail)}</p>
       <p class="muted">Gateway answered <code>${String(problem.status)}</code> with code <code>${escapeHtml(problem.code)}</code>.</p>
       ${fields ? `<ul>${fields}</ul>` : ''}
       <div class="actions"><a class="button secondary" href="${PATHS.orders}">View orders</a></div>
     </div>`,
  )
}

export const notFoundPage = (): string =>
  page('Not found', `<div class="card notice error"><h1>Page not found</h1><a class="button secondary" href="${PATHS.home}">Back to products</a></div>`)
