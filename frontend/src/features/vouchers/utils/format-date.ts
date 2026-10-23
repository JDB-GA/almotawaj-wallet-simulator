import type { Language } from '@/i18n/i18n.types'

export const formatDate = (value: string, language: Language): string =>
  new Intl.DateTimeFormat(language, { dateStyle: 'medium' }).format(new Date(value))
