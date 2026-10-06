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
  | 'changePassword'
  | 'profile'
  | 'paymentRequests'

export interface AppPage {
  id: AppPageId
  path: string
  icon: LucideIcon
  roles: readonly UserRole[]
  permission?: Permission
  parentId?: AppPageId
  showInSidebar: boolean
  lazy: LazyRouteFunction<RouteObject>
}
