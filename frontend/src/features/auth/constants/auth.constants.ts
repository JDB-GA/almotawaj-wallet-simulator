export const AUTH_ENDPOINTS = {
  register: '/auth/users/register',
  login: '/auth/users/login',
  logout: '/auth/users/logout',
  me: '/auth/users/me',
  verifyEmail: '/auth/users/verify-email',
  resendVerification: '/auth/users/verify-email/resend',
  forgotPassword: '/auth/users/password/forgot',
  resetPassword: '/auth/users/password/reset',
  changePassword: '/auth/users/password/change',
} as const

export const AUTH_QUERY_KEYS = {
  currentUser: ['auth', 'current-user'],
} as const

export const EMAIL_MARKER = '@'
export const MOBILE_COUNTRY_CODE = '+973'
export const MOBILE_NUMBER_LENGTH = 8
export const MOBILE_NUMBER_PATTERN = /^\d{8}$/
export const EMAIL_MAX_LENGTH = 254
export const PASSWORD_MIN_LENGTH = 8
export const PASSWORD_MAX_LENGTH = 72
export const OTP_LENGTH = 6
export const OTP_PATTERN = /^\d{6}$/
export const RESEND_COOLDOWN_SECONDS = 60

export const SHOWCASE_TAGLINE_KEYS = ['manage', 'send', 'secure'] as const
export const SHOWCASE_ROTATE_MS = 4000
export const AUTH_FIELD_CLASS = 'h-12 rounded-xl px-4 md:text-base'
export const AUTH_FIELD_GROUP_CLASS = 'h-12 rounded-xl'
export const AUTH_SUBMIT_CLASS = 'h-12 rounded-xl text-base'
