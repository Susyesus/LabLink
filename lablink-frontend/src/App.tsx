import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import { AppLayout }       from '@/components/layout/AppLayout';
import { ProtectedRoute, AdminRoute, PublicRoute } from '@/components/layout/RouteGuards';

import LoginPage            from '@/pages/LoginPage';
import RegisterPage         from '@/pages/RegisterPage';
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
            background: '#161B26',
            color: '#E8ECF4',
            border: '1px solid #232A3A',
            borderRadius: '10px',
            fontSize: '13px',
            fontFamily: "'DM Sans', sans-serif",
          },
          success: { iconTheme: { primary: '#10B981', secondary: '#161B26' } },
          error:   { iconTheme: { primary: '#EF4444', secondary: '#161B26' } },
        }}
      />

      <Routes>
        {/* Public auth routes — redirect if already logged in */}
        <Route element={<PublicRoute />}>
          <Route path="/login"    element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>

        {/* Student routes */}
        <Route element={<ProtectedRoute />}>
          <Route element={<AppLayout />}>
            <Route path="/catalog"        element={<CatalogPage />} />
            <Route path="/catalog/:id"    element={<EquipmentDetailPage />} />
            <Route path="/my-items"       element={<MyBorrowsPage />} />
            <Route path="/profile"        element={<ProfilePage />} />
            {/* Admin routes */}
            <Route element={<AdminRoute />}>
              <Route path="/admin"           element={<AdminDashboardPage />} />
              <Route path="/admin/equipment" element={<AdminEquipmentPage />} />
              <Route path="/admin/borrows"   element={<AdminBorrowsPage />} />
            </Route>
          </Route>
        </Route>

        {/* Default redirect */}
        <Route path="/"  element={<Navigate to="/catalog" replace />} />
        <Route path="*"  element={<Navigate to="/catalog" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
