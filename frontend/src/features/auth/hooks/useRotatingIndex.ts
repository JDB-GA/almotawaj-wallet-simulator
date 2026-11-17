import { useEffect, useState } from 'react'

export function useRotatingIndex(length: number, intervalMs: number) {
  const [index, setIndex] = useState(0)

  useEffect(() => {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      return
    }
    const timer = window.setInterval(() => {
      setIndex((current) => (current + 1) % length)
    }, intervalMs)
    return () => {
      window.clearInterval(timer)
    }
  }, [length, intervalMs])

  return index
}
