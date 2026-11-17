import { Outlet } from 'react-router'
import { ChatbotWidget } from '@/features/chatbot/components/ChatbotWidget'
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar'
import { AppHeader } from './AppHeader'
import { AppSidebar } from './AppSidebar'

export function AppLayout() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset className="min-h-0 min-w-0">
        <AppHeader />
        <div className="flex min-h-0 flex-1 flex-col overflow-y-auto bg-muted p-4 md:p-6">
          <Outlet />
        </div>
      </SidebarInset>
      <ChatbotWidget />
    </SidebarProvider>
  )
}
