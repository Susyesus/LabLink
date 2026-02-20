// ============================================================
// LabLink — Shared TypeScript Types
// Mirrors the SDD API contract exactly.
// ============================================================

// ── API Response Wrapper ──────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ApiError | null;
  timestamp: string;
}

export interface ApiError {
  code: string;
  message: string;
  details: Record<string, string> | string | null;
}

// ── Pagination ────────────────────────────────────────────────
export interface Pagination {
  page: number;
  limit: number;
  total: number;
  pages: number;
}

// ── Auth ──────────────────────────────────────────────────────
export type UserRole = 'STUDENT' | 'ADMIN';

export interface AuthUser {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export interface AuthResponse {
  user: AuthUser;
  token: string;
  refreshToken: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  confirmPassword: string;
  idNumber?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

// ── Equipment ─────────────────────────────────────────────────
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
  pagination: Pagination;
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

// ── Borrow Records ────────────────────────────────────────────
export type BorrowStatus = 'ACTIVE' | 'RETURNED' | 'OVERDUE';

export interface BorrowRecord {
  id: string;
  recordId: string;
  itemName: string;
  equipmentId: string;
  imageUrl: string | null;
  borrowDate: string;
  expectedReturnDate: string;
  actualReturnDate: string | null;
  status: BorrowStatus;
  purpose?: string;
  remarks?: string;
}

export interface BorrowRequest {
  equipmentId: string;
  expectedReturnDate: string;
  purpose?: string;
}

export interface BorrowResponse {
  message: string;
  borrowRecord: {
    id: string;
    borrowDate: string;
    expectedReturnDate: string;
    status: BorrowStatus;
  };
}

export interface MyBorrowsResponse {
  activeBorrows: BorrowRecord[];
}

// ── Equipment query params ────────────────────────────────────
export interface EquipmentQueryParams {
  page?: number;
  limit?: number;
  search?: string;
  status?: EquipmentStatus;
  categoryId?: string;
}
