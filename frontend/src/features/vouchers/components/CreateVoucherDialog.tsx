import { useState } from 'react'
import { PlusIcon } from 'lucide-react'
import { useTranslation } from 'react-i18next'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { CreateVoucherForm } from './CreateVoucherForm'

export function CreateVoucherDialog() {
  const { t } = useTranslation('vouchers')
  const [open, setOpen] = useState(false)

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusIcon data-icon="inline-start" aria-hidden="true" />
          {t('manage.create')}
        </Button>
      </DialogTrigger>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>{t('form.heading')}</DialogTitle>
          <DialogDescription>{t('form.description')}</DialogDescription>
        </DialogHeader>
        <CreateVoucherForm
          onCreated={() => {
            setOpen(false)
            toast.success(t('form.created'))
          }}
        />
      </DialogContent>
    </Dialog>
  )
}
