import { apiClient } from '@/core/api/apiClient';
import type { ApiResponse } from '@/core/types/api';
import type { BorrowRequest, BorrowResponse, MyBorrowsResponse, BorrowRecord } from './types';

// ── Borrow API ────────────────────────────────────────────────
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
