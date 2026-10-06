import arAuditLog from './locales/ar/auditLog.json'
import arAuth from './locales/ar/auth.json'
import arCommon from './locales/ar/common.json'
import arDevelopers from './locales/ar/developers.json'
import arErrors from './locales/ar/errors.json'
import arKyc from './locales/ar/kyc.json'
import arKycReview from './locales/ar/kycReview.json'
import arMerchant from './locales/ar/merchant.json'
import arProfile from './locales/ar/profile.json'
import arStatistics from './locales/ar/statistics.json'
import arUsers from './locales/ar/users.json'
import arValidation from './locales/ar/validation.json'
import arWallet from './locales/ar/wallet.json'
import enAuditLog from './locales/en/auditLog.json'
import enAuth from './locales/en/auth.json'
import enCommon from './locales/en/common.json'
import enDevelopers from './locales/en/developers.json'
import enErrors from './locales/en/errors.json'
import enKyc from './locales/en/kyc.json'
import enKycReview from './locales/en/kycReview.json'
import enMerchant from './locales/en/merchant.json'
import enProfile from './locales/en/profile.json'
import enStatistics from './locales/en/statistics.json'
import enUsers from './locales/en/users.json'
import enValidation from './locales/en/validation.json'
import enWallet from './locales/en/wallet.json'

const en = {
  common: enCommon,
  auth: enAuth,
  validation: enValidation,
  errors: enErrors,
  kyc: enKyc,
  kycReview: enKycReview,
  users: enUsers,
  auditLog: enAuditLog,
  wallet: enWallet,
  statistics: enStatistics,
  merchant: enMerchant,
  profile: enProfile,
  developers: enDevelopers,
}

const ar: typeof en = {
  common: arCommon,
  auth: arAuth,
  validation: arValidation,
  errors: arErrors,
  kyc: arKyc,
  kycReview: arKycReview,
  users: arUsers,
  auditLog: arAuditLog,
  wallet: arWallet,
  statistics: arStatistics,
  merchant: arMerchant,
  profile: arProfile,
  developers: arDevelopers,
}

export const resources = { en, ar }

export const NAMESPACES = ['common', 'auth', 'validation', 'errors', 'kyc', 'kycReview', 'users', 'auditLog', 'wallet', 'statistics', 'merchant', 'profile', 'developers'] as const

export const DEFAULT_NAMESPACE = 'common'
