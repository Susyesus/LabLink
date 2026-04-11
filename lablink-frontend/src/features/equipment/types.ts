// ── Equipment Feature Types ───────────────────────────────────

export type EquipmentStatus = 'AVAILABLE' | 'UNAVAILABLE' | 'MAINTENANCE';

export interface Equipment {
  id: string;
  name: string;
  description: string;
  serialNumber: string;
  status: EquipmentStatus;
  category: Category;
  imageUrl: string | null;
}

export interface Category {
  id: string;
  name: string;
  description?: string;
}

export interface EquipmentListResponse {
  equipment: Equipment[];
  pagination: import('@/core/types/api').Pagination;
}

export interface CreateEquipmentRequest {
  name: string;
  description: string;
  serialNumber: string;
  categoryId: string;
  imageUrl?: string;
}

export interface UpdateEquipmentRequest {
  name?: string;
  description?: string;
  status?: EquipmentStatus;
  categoryId?: string;
  imageUrl?: string;
}

export interface EquipmentQueryParams {
  page?: number;
  limit?: number;
  search?: string;
  status?: EquipmentStatus;
  categoryId?: string;
}
