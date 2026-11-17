import type { ComponentProps } from 'react'
import { EyeIcon, EyeOffIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { InputGroup, InputGroupAddon, InputGroupButton, InputGroupInput } from '@/components/ui/input-group'
import { usePasswordVisibility } from '../hooks/usePasswordVisibility'

type PasswordInputProps = Omit<ComponentProps<typeof InputGroupInput>, 'type'> & {
  groupClassName?: string
}

export function PasswordInput({ groupClassName, ...props }: PasswordInputProps) {
  const { t } = useTranslation('auth')
  const { isVisible, toggleVisibility } = usePasswordVisibility()

  return (
    <InputGroup className={groupClassName}>
      <InputGroupInput type={isVisible ? 'text' : 'password'} dir="ltr" {...props} />
      <InputGroupAddon align="inline-end">
        <InputGroupButton
          size="icon-xs"
          aria-label={t(isVisible ? 'password.hide' : 'password.show')}
          aria-pressed={isVisible}
          onClick={toggleVisibility}
        >
          {isVisible ? <EyeOffIcon /> : <EyeIcon />}
        </InputGroupButton>
      </InputGroupAddon>
    </InputGroup>
  )
}
