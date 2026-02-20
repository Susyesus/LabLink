import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';

/** Redirects unauthenticated users to /login */
export function ProtectedRoute() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

/** Redirects non-admin users to /catalog */
export function AdminRoute() {
  const user = useAuthStore((s) => s.user);
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== 'ADMIN') return <Navigate to="/catalog" replace />;
  return <Outlet />;
}

/** Redirects already-authenticated users away from auth pages */
export function PublicRoute() {
  const { isAuthenticated, user } = useAuthStore();
  if (!isAuthenticated) return <Outlet />;
  return <Navigate to={user?.role === 'ADMIN' ? '/admin' : '/catalog'} replace />;
}
