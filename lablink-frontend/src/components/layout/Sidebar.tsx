import { NavLink, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import {
  FlaskConical, LayoutGrid, Package, ClipboardList,
  User, LogOut, ChevronRight, Shield, Settings
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { useAuth } from '@/hooks/useAuth';
import { ThemeToggle } from '@/components/ui/ThemeToggle';

const studentNav = [
  { to: '/catalog',  label: 'Equipment',  icon: LayoutGrid },
  { to: '/my-items', label: 'My Borrows', icon: ClipboardList },
  { to: '/profile',  label: 'Profile',    icon: User },
  { to: '/settings', label: 'Settings',   icon: Settings },
];

const adminNav = [
  { to: '/admin',           label: 'Dashboard', icon: LayoutGrid },
  { to: '/admin/equipment', label: 'Inventory', icon: Package },
  { to: '/admin/borrows',   label: 'Borrows',   icon: ClipboardList },
];

export function Sidebar() {
  const user = useAuthStore((s) => s.user);
  const { logout } = useAuth();
  const nav = user?.role === 'ADMIN' ? adminNav : studentNav;
  const [photoSrc, setPhotoSrc] = useState<string | null>(null);

  useEffect(() => {
    const token = localStorage.getItem('ll_access_token');
    if (!token) return;
    const base = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';
    fetch(`${base}/users/me/photo`, { headers: { Authorization: `Bearer ${token}` } })
      .then(r => r.ok ? r.blob() : Promise.reject())
      .then(b => setPhotoSrc(URL.createObjectURL(b)))
      .catch(() => setPhotoSrc(null));
  }, [user?.id]);

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 bg-lab-surface border-r border-lab-border
                      flex flex-col z-30 animate-fade-in">
      {/* Logo */}
      <div className="flex items-center gap-3 px-6 h-16 border-b border-lab-border">
        <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-lab-primary/20 border border-lab-primary/30">
          <FlaskConical size={16} className="text-lab-primary" />
        </div>
        <span className="font-display font-bold text-lg text-lab-text tracking-tight">LabLink</span>
        {user?.role === 'ADMIN' && (
          <span className="ml-auto flex items-center gap-1 text-[10px] font-mono font-medium
                           text-lab-warning bg-lab-warning/10 border border-lab-warning/20
                           px-2 py-0.5 rounded-full">
            <Shield size={9} />ADMIN
          </span>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <p className="text-[10px] font-mono font-medium text-lab-muted uppercase tracking-widest px-3 mb-3">
          {user?.role === 'ADMIN' ? 'Management' : 'Navigation'}
        </p>
        {nav.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/admin'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-body font-medium
               transition-all duration-150 group
               ${isActive
                 ? 'bg-lab-primary/15 text-lab-primary border border-lab-primary/20'
                 : 'text-lab-muted hover:text-lab-text hover:bg-lab-border/50'
               }`
            }
          >
            {({ isActive }) => (
              <>
                <Icon size={16} className={isActive ? 'text-lab-primary' : 'text-lab-muted group-hover:text-lab-text'} />
                {label}
                {isActive && <ChevronRight size={14} className="ml-auto text-lab-primary" />}
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* User footer */}
      <div className="px-3 py-4 border-t border-lab-border space-y-1">
        {/* User card */}
        <div className="flex items-center gap-3 px-3 py-2.5 rounded-lg bg-lab-bg border border-lab-border mb-2">
          <div className="w-7 h-7 rounded-full bg-lab-primary/20 border border-lab-primary/30 flex items-center justify-center flex-shrink-0 overflow-hidden">
            {photoSrc
              ? <img src={photoSrc} alt="avatar" className="w-full h-full object-cover" />
              : <span className="text-xs font-display font-bold text-lab-primary">{user?.name.charAt(0).toUpperCase()}</span>
            }
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-xs font-body font-medium text-lab-text truncate">{user?.name}</p>
            <p className="text-[10px] font-mono text-lab-muted truncate">{user?.email}</p>
          </div>
          {/* Theme toggle tucked beside user info */}
          <ThemeToggle />
        </div>

        <button
          onClick={logout}
          className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-body
                     text-lab-muted hover:text-lab-danger hover:bg-lab-danger/10
                     transition-all duration-150 group"
        >
          <LogOut size={16} className="group-hover:text-lab-danger transition-colors" />
          Sign Out
        </button>
      </div>
    </aside>
  );
}

/** Top header bar for page title + contextual actions */
export function PageHeader({
  title,
  subtitle,
  actions,
}: {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}) {
  return (
    <div className="flex items-center justify-between mb-8">
      <div>
        <h1 className="font-display font-bold text-2xl text-lab-text">{title}</h1>
        {subtitle && <p className="text-sm text-lab-muted mt-0.5">{subtitle}</p>}
      </div>
      {actions && <div className="flex items-center gap-3">{actions}</div>}
    </div>
  );
}
