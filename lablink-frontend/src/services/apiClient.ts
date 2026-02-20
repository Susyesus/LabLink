import axios, { AxiosError, AxiosResponse } from 'axios';
import type { ApiResponse } from '@/types';

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api/v1';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
});

// ── Request interceptor: attach JWT ───────────────────────────
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ll_access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Response interceptor: handle 401 token expiry ────────────
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError<ApiResponse<null>>) => {
    const originalRequest = error.config as typeof error.config & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem('ll_refresh_token');
      if (refreshToken) {
        try {
          const { data } = await axios.post<ApiResponse<{ token: string }>>(
            `${BASE_URL}/auth/refresh`,
            { refreshToken }
          );
          if (data.success && data.data) {
            localStorage.setItem('ll_access_token', data.data.token);
            originalRequest!.headers!.Authorization = `Bearer ${data.data.token}`;
            return apiClient(originalRequest!);
          }
        } catch {
          // Refresh failed — force logout
          localStorage.removeItem('ll_access_token');
          localStorage.removeItem('ll_refresh_token');
          window.location.href = '/login';
        }
      } else {
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Extracts the error message from a failed ApiResponse.
 * Falls back to a generic message if structure is unexpected.
 */
export function extractApiError(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiResponse<null> | undefined;
    if (data?.error?.message) return data.error.message;
    if (error.message) return error.message;
  }
  return 'An unexpected error occurred. Please try again.';
}
