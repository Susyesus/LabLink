// ============================================================
// LabLink — Shared API Types
// Used by all feature slices.
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
