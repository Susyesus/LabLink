import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/services/api';
import { extractApiError } from '@/services/apiClient';
import type { RegisterRequest, LoginRequest } from '@/types';

/** Wraps auth API calls with loading state, toast feedback, and store updates. */
export function useAuth() {
  const { setAuth, clearAuth, user, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const register = useCallback(async (data: RegisterRequest) => {
    setIsLoading(true);
    try {
      const res = await authApi.register(data);
      if (res.data.success && res.data.data) {
        const { user, token, refreshToken } = res.data.data;
        setAuth(user, token, refreshToken);
        toast.success('Account created! Welcome to LabLink.');
        navigate(user.role === 'ADMIN' ? '/admin' : '/catalog');
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setIsLoading(false);
    }
  }, [setAuth, navigate]);

  const login = useCallback(async (data: LoginRequest) => {
    setIsLoading(true);
    try {
      const res = await authApi.login(data);
      if (res.data.success && res.data.data) {
        const { user, token, refreshToken } = res.data.data;
        setAuth(user, token, refreshToken);
        toast.success(`Welcome back, ${user.name.split(' ')[0]}!`);
        navigate(user.role === 'ADMIN' ? '/admin' : '/catalog');
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setIsLoading(false);
    }
  }, [setAuth, navigate]);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // Ignore API error — clear client state regardless
    } finally {
      clearAuth();
      navigate('/login', { replace: true });
      toast.success('Logged out successfully.');
    }
  }, [clearAuth, navigate]);

  return { user, isAuthenticated, isLoading, register, login, logout };
}
