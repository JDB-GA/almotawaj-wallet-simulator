import { PATHS, QUICK_EXPIRY_MINUTES } from '../config.ts'
import { PRODUCTS } from '../catalog.ts'
import type { Product } from '../types.ts'
import { escapeHtml, formatPrice, page } from './layout.ts'

const productCard = (product: Product): string => `
  <form class="card" method="post" action="${PATHS.buy}">
    <h2>${escapeHtml(product.name)}</h2>
    <p class="muted">${escapeHtml(product.description)}</p>
    <p class="price">${formatPrice(product.price)}</p>
    <input type="hidden" name="productId" value="${escapeHtml(product.id)}">
    <label><input type="checkbox" name="quickExpiry" value="true"> Expire in ${String(QUICK_EXPIRY_MINUTES)} minute</label>
    <button type="submit">Pay with Digital Wallet</button>
  </form>`

export const shopPage = (): string =>
  page(
    'Products',
    `<h1>Products</h1>
     <p class="muted">Choose a product. You are sent to the Digital Wallet checkout page and brought back here when you are done.</p>
     <div class="grid">${PRODUCTS.map(productCard).join('')}</div>`,
  )
