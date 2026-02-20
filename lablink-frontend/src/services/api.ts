import { apiClient } from './apiClient';
import type {
  ApiResponse,
  AuthResponse,
  RegisterRequest,
  LoginRequest,
  Equipment,
  EquipmentListResponse,
  EquipmentQueryParams,
  CreateEquipmentRequest,
  UpdateEquipmentRequest,
  BorrowRequest,
  BorrowResponse,
  MyBorrowsResponse,
  Category,
} from '@/types';

// ── Auth ──────────────────────────────────────────────────────

export const authApi = {
  register: (data: RegisterRequest) =>
    apiClient.post<ApiResponse<AuthResponse>>('/auth/register', data),

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

  /** Admin: verify physical return and reset item status to AVAILABLE */
  verifyReturn: (recordId: string, conditionNotes?: string) =>
    apiClient.post<ApiResponse<{ message: string; itemStatus: string }>>(
      `/admin/return/${recordId}`,
      { conditionNotes, status: 'RETURNED' }
    ),

  /** Admin: get all active borrow records */
  getAllActive: () =>
    apiClient.get<ApiResponse<{ borrows: import('@/types').BorrowRecord[] }>>('/admin/borrows'),
};
