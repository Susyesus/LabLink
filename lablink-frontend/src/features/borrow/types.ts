// ── Borrow Feature Types ──────────────────────────────────────

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
