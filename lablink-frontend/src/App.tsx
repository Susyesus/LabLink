import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import { AppLayout }       from '@/components/layout/AppLayout';
import { ProtectedRoute, AdminRoute, PublicRoute } from '@/components/layout/RouteGuards';

import LoginPage            from '@/pages/LoginPage';
import RegisterPage         from '@/pages/RegisterPage';
import AdminRegisterPage    from '@/pages/AdminRegisterPage';
import CatalogPage          from '@/pages/CatalogPage';
import EquipmentDetailPage  from '@/pages/EquipmentDetailPage';
import MyBorrowsPage        from '@/pages/MyBorrowsPage';
import ProfilePage          from '@/pages/ProfilePage';
import AdminDashboardPage   from '@/pages/AdminDashboardPage';
import AdminEquipmentPage   from '@/pages/AdminEquipmentPage';
import AdminBorrowsPage     from '@/pages/AdminBorrowsPage';

export default function App() {
  return (
    <BrowserRouter>
      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            background: 'var(--color-surface)',
            color: 'var(--color-text)',
            border: '1px solid var(--color-border)',
            borderRadius: '10px',
            fontSize: '13px',
            fontFamily: "'DM Sans', sans-serif",
          },
          success: { iconTheme: { primary: 'var(--color-success)', secondary: 'var(--color-surface)' } },
          error:   { iconTheme: { primary: 'var(--color-danger)',  secondary: 'var(--color-surface)' } },
        }}
      />

      <Routes>
        {/* Public auth routes — redirect to app if already logged in */}
        <Route element={<PublicRoute />}>
          <Route path="/login"          element={<LoginPage />} />
          <Route path="/register"       element={<RegisterPage />} />
          <Route path="/register-admin" element={<AdminRegisterPage />} />
        </Route>

        {/* Protected app routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/catalog"        element={<CatalogPage />} />
            <Route path="/catalog/:id"    element={<EquipmentDetailPage />} />
            <Route path="/my-items"       element={<MyBorrowsPage />} />
            <Route path="/profile"        element={<ProfilePage />} />
            {/* Admin-only routes */}
            <Route element={<AdminRoute />}>
              <Route path="/admin"           element={<AdminDashboardPage />} />
              <Route path="/admin/equipment" element={<AdminEquipmentPage />} />
              <Route path="/admin/borrows"   element={<AdminBorrowsPage />} />
            </Route>
          </Route>
        </Route>

        <Route path="/"  element={<Navigate to="/catalog" replace />} />
        <Route path="*"  element={<Navigate to="/catalog" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
