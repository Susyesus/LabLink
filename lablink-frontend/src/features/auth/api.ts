import { apiClient } from '@/core/api/apiClient';
import type { ApiResponse } from '@/core/types/api';
import type { AuthResponse, RegisterRequest, LoginRequest, AdminRegisterRequest } from './types';

// ── Auth API ──────────────────────────────────────────────────
export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data),

  registerAdmin: (data: AdminRegisterRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/register-admin', data),

  login: (data: LoginRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/login', data),

  logout: () =>
    apiClient.post<ApiResponse<{ message: string }>>('/auth/logout'),
};
