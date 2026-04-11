// ── Profile Feature Types ─────────────────────────────────────

import type { UserRole } from '@/features/auth/types';

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  idNumber: string | null;
  role: UserRole;
  hasPhoto: boolean;
  createdAt: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  idNumber?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
