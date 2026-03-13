import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuthStore } from '@/store/authStore';
import { authApi } from '@/services/api';
import { extractApiError, extractFieldErrors } from '@/services/apiClient';
import type { RegisterRequest, LoginRequest } from '@/types';

/** Wraps auth API calls with loading state, toast feedback, and store updates. */
export function useAuth() {
  const { setAuth, clearAuth, user, isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  /**
   * Attempts registration.
   * Returns field-error map on VALID-001, null on other errors, undefined on success.
   */
  const register = useCallback(async (
    data: RegisterRequest
  ): Promise<Record<string, string> | null | undefined> => {
    setIsLoading(true);
    try {
      const res = await authApi.register(data);
      if (res.data.success && res.data.data) {
        const { user, token, refreshToken } = res.data.data;
        setAuth(user, token, refreshToken);
        toast.success('Account created! Welcome to LabLink.');
        navigate(user.role === 'ADMIN' ? '/admin' : '/catalog');
        return undefined; // success — no errors
      }
    } catch (err) {
      const fieldErrors = extractFieldErrors(err);
      if (fieldErrors) {
        // Return to component for inline display; also show summary toast
        toast.error('Please fix the highlighted fields.');
        return fieldErrors;
      }
      toast.error(extractApiError(err));
      return null;
    } finally {
      setIsLoading(false);
    }
  }, [setAuth, navigate]);

  /**
   * Attempts login.
   * Returns a string error message on failure (shown inline), undefined on success.
   */
  const login = useCallback(async (
    data: LoginRequest
  ): Promise<string | undefined> => {
    setIsLoading(true);
    try {
      const res = await authApi.login(data);
      if (res.data.success && res.data.data) {
        const { user, token, refreshToken } = res.data.data;
        setAuth(user, token, refreshToken);
        toast.success(`Welcome back, ${user.name.split(' ')[0]}!`);
        navigate(user.role === 'ADMIN' ? '/admin' : '/catalog');
        return undefined; // success
      }
    } catch (err) {
      const message = extractApiError(err);
      return message; // returned to component for inline display
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
