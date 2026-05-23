import axios, { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import type { ApiResponse } from '@/core/types/api';

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api/v1';

// ── Retry configuration ──────────────────────────────────────
const MAX_RETRIES = 3;
const INITIAL_DELAY_MS = 1_000; // 1s → 2s → 4s

/** Returns true for errors that are worth retrying (transient / cold-start). */
function isRetryable(error: AxiosError): boolean {
  // Network errors (no response at all — ECONNABORTED, ERR_NETWORK, timeout)
  if (!error.response) return true;

  // Server-side transient errors (Render cold-start 502/503, overload 429)
  const status = error.response.status;
  return status === 429 || status === 502 || status === 503 || status === 504;
}

/** Only retry idempotent methods to avoid duplicate writes. */
function isSafeMethod(config: InternalAxiosRequestConfig): boolean {
  const method = (config.method ?? 'get').toLowerCase();
  return ['get', 'head', 'options'].includes(method);
}

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30_000,
});

// ── Request interceptor: attach JWT ───────────────────────────
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('ll_access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ── Response interceptor: retry transient errors, then handle 401 ──
apiClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError<ApiResponse<null>>) => {
    const config = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
      _retryCount?: number;
    };

    // ── 1. Automatic retry for transient / cold-start failures ──
    if (config && isRetryable(error) && isSafeMethod(config)) {
      const retryCount = config._retryCount ?? 0;
      if (retryCount < MAX_RETRIES) {
        config._retryCount = retryCount + 1;
        const delay = INITIAL_DELAY_MS * Math.pow(2, retryCount);
        await new Promise((r) => setTimeout(r, delay));
        return apiClient(config);
      }
    }

    // ── 2. Token refresh on 401 ─────────────────────────────────
    if (error.response?.status === 401 && config && !config._retry) {
      // Do not intercept 401 errors for login/register endpoints
      if (config.url?.includes('/auth/login') || config.url?.includes('/auth/register')) {
        return Promise.reject(error);
      }

      config._retry = true;

      const refreshToken = localStorage.getItem('ll_refresh_token');
      if (refreshToken) {
        try {
          const { data } = await axios.post<ApiResponse<{ token: string }>>(
            `${BASE_URL}/auth/refresh`,
            { refreshToken }
          );
          if (data.success && data.data) {
            localStorage.setItem('ll_access_token', data.data.token);
            config.headers.Authorization = `Bearer ${data.data.token}`;
            return apiClient(config);
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
 * Extracts the top-level error message from a failed ApiResponse.
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

/**
 * Extracts field-level validation errors from a VALID-001 backend response.
 * Returns a map of { fieldName: errorMessage } or null if not a field-error response.
 *
 * Backend shape: { error: { code: "VALID-001", details: { email: "...", fullName: "..." } } }
 */
export function extractFieldErrors(error: unknown): Record<string, string> | null {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiResponse<null> | undefined;
    if (
      data?.error?.code === 'VALID-001' &&
      data.error.details &&
      typeof data.error.details === 'object' &&
      !Array.isArray(data.error.details)
    ) {
      return data.error.details as Record<string, string>;
    }
  }
  return null;
}
