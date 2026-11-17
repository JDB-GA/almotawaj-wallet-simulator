import { useTranslation } from 'react-i18next'
import { cn } from '@/lib/utils'
import { SHOWCASE_ROTATE_MS, SHOWCASE_TAGLINE_KEYS } from '../constants/auth.constants'
import { useRotatingIndex } from '../hooks/useRotatingIndex'
import { WalletIllustration } from './WalletIllustration'

export function LoginShowcase() {
  const { t } = useTranslation('auth')
  const active = useRotatingIndex(SHOWCASE_TAGLINE_KEYS.length, SHOWCASE_ROTATE_MS)
  const activeKey = SHOWCASE_TAGLINE_KEYS[active] ?? SHOWCASE_TAGLINE_KEYS[0]

  return (
    <aside className="relative hidden lg:block">
      <div className="flex h-full items-center justify-center rounded-[1.75rem] bg-linear-to-br from-[color-mix(in_oklch,var(--primary)_45%,white)] to-[color-mix(in_oklch,var(--primary)_75%,white)] pb-28 [clip-path:polygon(0_0,100%_0,100%_78%,60%_78%,48%_100%,0_100%)] rtl:[clip-path:polygon(0_0,100%_0,100%_100%,52%_100%,40%_78%,0_78%)]">
        <WalletIllustration />
      </div>
      <div className="absolute end-0 bottom-0 flex w-[46%] flex-col items-center gap-5 px-4 pb-8">
        <p key={active} className="text-center text-2xl text-white motion-safe:animate-in motion-safe:fade-in motion-safe:slide-in-from-bottom-2 motion-safe:duration-500">
          {t(`login.showcase.taglines.${activeKey}`)}
        </p>
        <div className="flex gap-2">
          {SHOWCASE_TAGLINE_KEYS.map((key, index) => (
            <span key={key} className={cn('h-1 w-10 rounded-full transition-colors duration-500', index === active ? 'bg-white' : 'bg-white/20')} />
          ))}
        </div>
      </div>
    </aside>
  )
}
