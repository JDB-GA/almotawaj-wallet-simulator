export function WalletIllustration() {
  return (
    <div aria-hidden="true" className="relative size-80 xl:size-96">
      <svg viewBox="0 0 240 240" className="absolute inset-0 size-full overflow-visible stroke-white/70">
        <circle cx="62" cy="40" r="9" fill="none" strokeWidth="4" className="origin-self motion-safe:animate-drift" />
        <path d="M196 36l14 14M210 36l-14 14" strokeWidth="4" strokeLinecap="round" className="origin-self motion-safe:animate-drift-delayed" />
        <path d="M30 120l16-22 6 26z" fill="none" strokeWidth="4" strokeLinejoin="round" className="origin-self motion-safe:animate-drift-delayed" />
        <circle cx="44" cy="190" r="14" fill="none" strokeWidth="5" className="origin-self motion-safe:animate-drift" />
        <path d="M206 150c10 4 6 22 10 34" fill="none" strokeWidth="4" strokeLinecap="round" className="origin-self motion-safe:animate-drift" />
      </svg>

      <div className="absolute inset-x-10 bottom-2 h-5 rounded-full bg-black/25 blur-md motion-safe:animate-shadow-pulse" />

      <svg viewBox="0 0 240 240" className="absolute inset-0 size-full motion-safe:animate-float">
        <g transform="rotate(-18 120 120)">
          <g className="origin-self motion-safe:animate-peek">
            <rect x="92" y="52" width="104" height="70" rx="10" className="fill-emerald-600" />
            <rect x="100" y="44" width="104" height="70" rx="10" className="fill-emerald-500" />
            <circle cx="152" cy="79" r="14" className="fill-emerald-700" />
          </g>
          <rect x="44" y="80" width="150" height="104" rx="22" className="fill-primary" />
          <rect x="44" y="80" width="150" height="104" rx="22" className="fill-black/25" />
          <rect x="44" y="72" width="150" height="104" rx="22" className="fill-primary" />
          <rect x="58" y="86" width="122" height="76" rx="14" fill="none" strokeWidth="3" className="stroke-white/30" />
          <rect x="146" y="106" width="58" height="32" rx="16" className="fill-white" />
          <circle cx="166" cy="122" r="8" className="fill-amber-400" />
        </g>
      </svg>
    </div>
  )
}
