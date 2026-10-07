import { PATHS } from '../config.ts'

const ESCAPES: Record<string, string> = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }

export const escapeHtml = (value: string): string => value.replace(/[&<>"']/g, (character) => ESCAPES[character] ?? character)

export const formatPrice = (amount: number): string => `BHD ${amount.toFixed(3)}`

const STYLES = `
  * { box-sizing: border-box; }
  body { margin: 0; font-family: system-ui, sans-serif; color: #1f2933; background: #f6f7f9; line-height: 1.5; }
  header { background: #fff; border-bottom: 1px solid #e3e6ea; }
  header div, main { max-width: 56rem; margin: 0 auto; padding: 1rem; }
  header div { display: flex; align-items: center; justify-content: space-between; gap: 1rem; }
  header a { color: inherit; text-decoration: none; font-weight: 600; }
  nav a { margin-inline-start: 1rem; font-weight: 400; }
  h1 { font-size: 1.5rem; margin: 1rem 0; }
  .grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(14rem, 1fr)); }
  .card { background: #fff; border: 1px solid #e3e6ea; border-radius: 0.5rem; padding: 1rem; }
  .price { font-size: 1.25rem; font-weight: 600; margin: 0.5rem 0; }
  .muted { color: #667085; font-size: 0.875rem; }
  button, .button { display: inline-block; border: 1px solid #1d4ed8; background: #1d4ed8; color: #fff; padding: 0.5rem 0.875rem;
    border-radius: 0.375rem; font: inherit; cursor: pointer; text-decoration: none; }
  button.secondary, .button.secondary { background: #fff; color: #1d4ed8; }
  button.danger { background: #fff; border-color: #b42318; color: #b42318; }
  label { display: flex; align-items: center; gap: 0.5rem; margin: 0.75rem 0; font-size: 0.875rem; }
  table { width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e3e6ea; }
  th, td { text-align: start; padding: 0.625rem; border-bottom: 1px solid #e3e6ea; font-size: 0.875rem; vertical-align: middle; }
  td form { display: inline; }
  code { font-family: ui-monospace, monospace; font-size: 0.8125rem; }
  .status { display: inline-block; padding: 0.125rem 0.5rem; border-radius: 0.25rem; font-size: 0.75rem; font-weight: 600; background: #eef2f6; }
  .status.PAID { background: #d1fadf; color: #05603a; }
  .status.CANCELLED, .status.EXPIRED { background: #fee4e2; color: #912018; }
  .status.REFUNDED { background: #fef0c7; color: #93370d; }
  .notice { border-inline-start: 4px solid #1d4ed8; }
  .notice.error { border-color: #b42318; }
  .notice.success { border-color: #039855; }
  .actions { display: flex; flex-wrap: wrap; gap: 0.5rem; margin-top: 1rem; }
`

export const page = (title: string, content: string): string => `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${escapeHtml(title)} · Souq Sample Shop</title>
  <style>${STYLES}</style>
</head>
<body>
  <header>
    <div>
      <a href="${PATHS.home}">Souq Sample Shop</a>
      <nav><a href="${PATHS.home}">Products</a><a href="${PATHS.orders}">Orders</a></nav>
    </div>
  </header>
  <main>${content}</main>
</body>
</html>`
