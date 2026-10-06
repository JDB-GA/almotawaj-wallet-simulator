# Frontend

[← Back to the README](../README.md)

React 19, TypeScript, Vite, TanStack Query, React Hook Form, Zod, shadcn/ui and i18next. Runs on http://localhost:5173.

## Setup

Prerequisites: Node.js 20+ and pnpm (`npm install -g pnpm`).

```bash
cd frontend
pnpm install
cp .env.example .env
pnpm dev
```

`VITE_API_URL` in `.env` must point to the backend (`http://localhost:8080`). 
## Scripts

| Script | Does |
| --- | --- |
| `pnpm dev` | start the development server |
| `pnpm build` | type-check and build for production |
| `pnpm preview` | serve the production build locally |
| `pnpm lint` | type-aware ESLint (`strictTypeChecked`); generated shadcn components in `src/components/ui` are excluded |

## Structure

```
src
├── app/          providers, router, route guards, layout, navigation (page registry)
├── components/   shared components (ui/ is shadcn)
├── config/       env and route constants
├── features/     auth, kyc, kyc-review, user-management, audit-log, wallet, notifications, admin, merchant, checkout, profile, developers
├── hooks/        shared hooks
├── i18n/         i18next setup and en/ar translations
└── lib/          HTTP client, query client, file helpers
```

Each feature is split into `api`, `schemas`, `types`, `constants`, `hooks`, `utils`, `components` and `pages`. Every API response is parsed with a Zod schema before it reaches a component, and types are inferred from those schemas.

## Pages and access

Pages are registered once in `src/app/navigation` with their route, icon, roles and optional permission. The router, the sidebar and the route guard are all built from that list.

## Localisation

Every text comes from a typed namespace in `src/i18n/locales/en` and `src/i18n/locales/ar`; a missing or misspelled key fails the type-check. Arabic switches the whole layout to right-to-left.

## Responsive layout

The wallet screens use lists instead of tables: each transaction or payment request is one item with an icon, the counterparty, the date, the reference and the amount, and it reflows to any width. The wallet home is a balance card with quick actions followed by the most recent transactions.

Administrative screens keep tables, and tables never hide columns. A table with the `stacked-table` class (see `src/styles/tables.css`) turns each row into a labelled card when the space it is given is narrower than 44rem; the label of each value comes from the cell's `data-label`. The switch is a container query, so it follows the table's own width rather than the screen, which keeps it correct when the sidebar is open.
