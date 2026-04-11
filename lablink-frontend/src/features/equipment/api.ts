import { apiClient } from '@/core/api/apiClient';
import type { ApiResponse } from '@/core/types/api';
import type {
  Equipment, EquipmentListResponse, EquipmentQueryParams,
  CreateEquipmentRequest, UpdateEquipmentRequest, Category,
} from './types';

// ── Equipment API ─────────────────────────────────────────────
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
