import { apiClient } from './apiClient';
import type {
  ApiResponse, AuthResponse, RegisterRequest, LoginRequest,
  AdminRegisterRequest,
  Equipment, EquipmentListResponse, EquipmentQueryParams,
  CreateEquipmentRequest, UpdateEquipmentRequest,
  BorrowRequest, BorrowResponse, MyBorrowsResponse, BorrowRecord,
  Category,
  UserProfile, UpdateProfileRequest, ChangePasswordRequest,
} from '@/types';

// ── Auth ──────────────────────────────────────────────────────
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

// ── Equipment ─────────────────────────────────────────────────
export const equipmentApi = {
  getAll: (params?: EquipmentQueryParams) =>
    apiClient.get<ApiResponse<EquipmentListResponse>>('/equipment', { params }),

  getById: (id: string) =>
    apiClient.get<ApiResponse<{ item: Equipment }>>(`/equipment/${id}`),

  create: (data: CreateEquipmentRequest) =>
    apiClient.post<ApiResponse<{ item: Equipment }>>('/equipment', data),

  update: (id: string, data: UpdateEquipmentRequest) =>
    apiClient.put<ApiResponse<{ item: Equipment }>>(`/equipment/${id}`, data),

  delete: (id: string) =>
    apiClient.delete<ApiResponse<null>>(`/equipment/${id}`),

  getCategories: () =>
    apiClient.get<ApiResponse<{ categories: Category[] }>>('/equipment/categories'),
};

// ── Borrow ────────────────────────────────────────────────────
export const borrowApi = {
  borrow: (data: BorrowRequest) =>
    apiClient.post<ApiResponse<BorrowResponse>>('/borrow', data),

  getMyBorrows: () =>
    apiClient.get<ApiResponse<MyBorrowsResponse>>('/borrow/my-items'),

  // Admin: get all borrow records (matches AdminBorrowsPage call)
  getAllActive: () =>
    apiClient.get<ApiResponse<{ borrows: BorrowRecord[] }>>('/admin/borrows'),

  // Admin: mark a borrow record as returned
  verifyReturn: (recordId: string, conditionNotes?: string) =>
    apiClient.post<ApiResponse<{ message: string; itemStatus: string }>>(
      `/admin/return/${recordId}`,
      conditionNotes ? { conditionNotes } : {}
    ),
};

// ── User Profile ──────────────────────────────────────────────
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
