import { create } from 'zustand';
import type { AuthUser } from '@/types';

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  setAuth: (user: AuthUser, token: string, refreshToken: string) => void;
  clearAuth: () => void;
  setLoading: (loading: boolean) => void;
}

/**
 * Global auth state store.
 *
 * ⚠️  Security note: tokens are stored in localStorage for simplicity.
 * For higher-security needs, use httpOnly cookies + a token refresh endpoint.
 * The SDD documents this trade-off (Journey 7 logout clears localStorage).
 */
export const useAuthStore = create<AuthState>((set) => ({
  user: (() => {
    try {
      const stored = localStorage.getItem('ll_user');
      return stored ? (JSON.parse(stored) as AuthUser) : null;
    } catch {
      return null;
    }
  })(),
  isAuthenticated: !!localStorage.getItem('ll_access_token'),
  isLoading: false,

  setAuth: (user, token, refreshToken) => {
    localStorage.setItem('ll_access_token', token);
    localStorage.setItem('ll_refresh_token', refreshToken);
    localStorage.setItem('ll_user', JSON.stringify(user));
    set({ user, isAuthenticated: true });
  },

  clearAuth: () => {
    localStorage.removeItem('ll_access_token');
    localStorage.removeItem('ll_refresh_token');
    localStorage.removeItem('ll_user');
    set({ user: null, isAuthenticated: false });
  },

  setLoading: (isLoading) => set({ isLoading }),
}));
