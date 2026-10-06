export const getReturnHost = (returnUrl: string | null): string | null => {
  if (returnUrl === null || !URL.canParse(returnUrl)) {
    return null
  }
  const url = new URL(returnUrl)
  return url.protocol === 'https:' || url.protocol === 'http:' ? url.host : null
}
