const SECONDS_PER_MINUTE = 60

export const secondsUntil = (isoDate: string): number => Math.max(0, Math.ceil((new Date(isoDate).getTime() - Date.now()) / 1000))

export const formatRemaining = (seconds: number): string => {
  const minutes = Math.floor(seconds / SECONDS_PER_MINUTE)
  return `${String(minutes).padStart(2, '0')}:${String(seconds % SECONDS_PER_MINUTE).padStart(2, '0')}`
}
