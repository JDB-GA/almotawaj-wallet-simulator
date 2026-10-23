import type { LucideIcon } from 'lucide-react'
import type { LazyRouteFunction, RouteObject } from 'react-router'
import type { Permission, UserRole } from '@/features/auth/types/user.types'

type AppPageId =
  | 'wallet'
  | 'transactions'
  | 'verification'
  | 'merchant'
  | 'merchantPayments'
  | 'merchantApiKeys'
  | 'merchantDevelopers'
  | 'checkout'
  | 'admin'
  | 'kycReviews'
  | 'kycReview'
  | 'users'
  | 'user'
  | 'userTransactions'
  | 'userCreate'
  | 'auditLog'
  | 'simulation'
  | 'changePassword'
  | 'profile'
  | 'paymentRequests'
  | 'vouchers'
  | 'merchantVouchers'
  | 'merchantPayouts'

export interface AppPage {
  id: AppPageId
  path: string
  icon: LucideIcon
  roles: readonly UserRole[]
  permission?: Permission
  requiresWallet?: boolean
  parentId?: AppPageId
  showInSidebar: boolean
  lazy: LazyRouteFunction<RouteObject>
}
