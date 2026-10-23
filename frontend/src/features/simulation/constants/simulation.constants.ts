export const SIMULATION_ENDPOINTS = {
  voucherRace: '/api/admin/simulations/voucher-race',
  payoutRace: '/api/admin/simulations/payout-race',
} as const

export const SIMULATION_MODES = ['SAFE', 'UNSAFE'] as const
export const SIMULATION_OUTCOMES = ['WON', 'SOLD_OUT', 'PAID', 'FAILED', 'ERROR'] as const
export const SIMULATION_STEP_TYPES = [
  'READ',
  'CHECK',
  'SLEEP',
  'WRITE',
  'TAKE_ONE',
  'SOLD_OUT',
  'LOCKED',
  'COMMITTED',
  'PAID',
  'FAILED',
] as const

export const OUTCOME_BAR_CLASSES = {
  WON: 'bg-primary',
  PAID: 'bg-primary',
  SOLD_OUT: 'bg-muted-foreground/40',
  FAILED: 'bg-destructive',
  ERROR: 'bg-destructive',
} as const

export const BUYERS_MIN = 2
export const BUYERS_MAX = 50
export const STOCK_MIN = 1
export const STOCK_MAX = 20
export const RECIPIENTS_MIN = 2
export const RECIPIENTS_MAX = 30
export const DEFAULT_BUYERS = 20
export const DEFAULT_STOCK = 1
export const DEFAULT_RECIPIENTS = 10
export const PAYOUT_AMOUNT_MIN = 1
export const PAYOUT_AMOUNT_MAX = 100
export const DEFAULT_PAYOUT_AMOUNT = 15
export const DEFAULT_MERCHANT_BALANCE = 100
export const VOUCHER_TAB = 'voucher'
export const PAYOUT_TAB = 'payout'
export const PERCENT = 100
export const MIN_BAR_PERCENT = 1.5
export const MS_DECIMALS = 1
export const REPLAY_SLOWDOWNS = [50, 200, 1000] as const
export const DEFAULT_REPLAY_SLOWDOWN = 200
export const REPLAY_SCRUB_STEP = 0.1
export const STEP_BADGE_CLASSES = {
  READ: 'bg-muted text-foreground',
  CHECK: 'bg-muted text-foreground',
  SLEEP: 'bg-muted text-muted-foreground',
  WRITE: 'bg-destructive/15 text-destructive',
  TAKE_ONE: 'bg-primary/15 text-primary',
  SOLD_OUT: 'bg-muted-foreground/20 text-muted-foreground',
  LOCKED: 'bg-amber-500/15 text-amber-700 dark:text-amber-400',
  COMMITTED: 'bg-primary text-primary-foreground',
  PAID: 'bg-primary text-primary-foreground',
  FAILED: 'bg-destructive text-white',
} as const
export const VOUCHER_SHARED_STEPS = ['WRITE', 'COMMITTED'] as const
export const PAYOUT_SHARED_STEPS = ['LOCKED', 'PAID'] as const
