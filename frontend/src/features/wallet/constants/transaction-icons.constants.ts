import { ArrowDownLeftIcon, ArrowUpRightIcon, BanknoteIcon, LandmarkIcon, ShoppingBagIcon, StoreIcon, TicketIcon, Undo2Icon } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import type { Transaction } from '../types/wallet.types'

export const TRANSACTION_ICONS: Readonly<Record<Transaction['type'], LucideIcon>> = {
  TOP_UP: LandmarkIcon,
  PAYMENT: ShoppingBagIcon,
  PAYMENT_RECEIVED: StoreIcon,
  REFUND: Undo2Icon,
  REFUND_ISSUED: Undo2Icon,
  TRANSFER_IN: ArrowDownLeftIcon,
  TRANSFER_OUT: ArrowUpRightIcon,
  VOUCHER_PURCHASE: TicketIcon,
  VOUCHER_SALE: TicketIcon,
  PAYOUT_SENT: BanknoteIcon,
  PAYOUT_RECEIVED: BanknoteIcon,
}
