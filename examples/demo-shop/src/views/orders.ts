import { PATHS } from '../config.ts'
import type { Order } from '../types.ts'
import { escapeHtml, formatPrice, page } from './layout.ts'

const action = (order: Order, name: 'cancel' | 'refund', label: string): string => `
  <form method="post" action="${PATHS.orders}/${encodeURIComponent(order.sessionId)}/${name}">
    <button class="danger" type="submit">${label}</button>
  </form>`

const actionsFor = (order: Order): string => {
  if (order.status === 'PENDING') {
    return `<a class="button" href="${escapeHtml(order.checkoutUrl)}">Pay</a> ${action(order, 'cancel', 'Cancel')}`
  }
  return order.status === 'PAID' ? action(order, 'refund', 'Refund') : ''
}

const row = (order: Order): string => `
  <tr>
    <td><code>${escapeHtml(order.reference)}</code></td>
    <td>${escapeHtml(order.product.name)}</td>
    <td>${formatPrice(order.product.price)}</td>
    <td><span class="status ${order.status}">${order.status}</span></td>
    <td>${order.lastCallback ? `<code>${escapeHtml(order.lastCallback)}</code>` : '<span class="muted">none</span>'}</td>
    <td>${actionsFor(order)}</td>
  </tr>`

export const ordersPage = (orders: readonly Order[], callbacksVerified: boolean): string =>
  page(
    'Orders',
    `<h1>Orders</h1>
     <p class="muted">Statuses are read from the gateway each time this page loads. ${
       callbacksVerified
         ? 'Callbacks are verified with the signing secret.'
         : 'Set WALLET_SIGNING_SECRET to accept signed callbacks.'
     }</p>
     ${
       orders.length === 0
         ? `<div class="card">No orders yet. <a href="${PATHS.home}">Buy something</a>.</div>`
         : `<table>
              <thead><tr><th>Order</th><th>Product</th><th>Amount</th><th>Status</th><th>Last callback</th><th>Actions</th></tr></thead>
              <tbody>${orders.map(row).join('')}</tbody>
            </table>`
     }`,
  )
