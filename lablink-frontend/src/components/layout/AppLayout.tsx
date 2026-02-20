import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

/** Main authenticated layout: fixed sidebar + scrollable content area */
export function AppLayout() {
  return (
    <div className="min-h-screen bg-lab-bg flex">
      <Sidebar />
      <main className="flex-1 ml-64 min-h-screen">
        <div className="max-w-7xl mx-auto px-8 py-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
