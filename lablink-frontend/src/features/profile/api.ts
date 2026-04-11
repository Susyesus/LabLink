import { apiClient } from '@/core/api/apiClient';
import type { ApiResponse } from '@/core/types/api';
import type { UserProfile, UpdateProfileRequest, ChangePasswordRequest } from './types';

// ── User Profile API ──────────────────────────────────────────
export const userApi = {
  getProfile: () =>
    apiClient.get<ApiResponse<UserProfile>>('/users/me'),

  updateProfile: (data: UpdateProfileRequest) =>
    apiClient.put<ApiResponse<UserProfile>>('/users/me', data),

  changePassword: (data: ChangePasswordRequest) =>
    apiClient.put<ApiResponse<null>>('/users/me/password', data),

  uploadPhoto: (file: File) => {
    const form = new FormData();
    form.append('file', file);
    return apiClient.post<ApiResponse<UserProfile>>('/users/me/photo', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  // Returns a URL string — use as <img src={...} />
  getPhotoUrl: () => `${apiClient.defaults.baseURL}/users/me/photo`,
};
