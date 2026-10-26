import { useEffect, useRef } from 'react'

export function LoginAnimation() {
  const container = useRef<HTMLDivElement>(null)

  useEffect(() => {
    let destroy: (() => void) | undefined
    let cancelled = false

    void Promise.all([import('lottie-web/build/player/lottie_light'), import('@/assets/lottie/money.json')]).then(
      ([{ default: lottie }, { default: animationData }]) => {
        if (cancelled || !container.current) {
          return
        }
        const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
        const animation = lottie.loadAnimation({
          container: container.current,
          renderer: 'svg',
          loop: !reducedMotion,
          autoplay: !reducedMotion,
          animationData,
        })
        destroy = () => {
          animation.destroy()
        }
      },
    )

    return () => {
      cancelled = true
      destroy?.()
    }
  }, [])

  return <div ref={container} aria-hidden="true" className="mx-auto size-40" />
}
