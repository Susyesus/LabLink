import { useState, useEffect } from 'react';
import { ClipboardList, Calendar, Clock } from 'lucide-react';
import { format, differenceInDays, parseISO } from 'date-fns';
import { borrowApi } from '@/services/api';
import { FullPageSpinner, EmptyState, BorrowStatusBadge } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { BorrowRecord } from '@/types';
import toast from 'react-hot-toast';
import { extractApiError } from '@/services/apiClient';

export default function MyBorrowsPage() {
  const [borrows, setBorrows] = useState<BorrowRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    borrowApi.getMyBorrows()
      .then((res) => {
        if (res.data.success && res.data.data) setBorrows(res.data.data.activeBorrows);
      })
      .catch((err) => toast.error(extractApiError(err)))
      .finally(() => setIsLoading(false));
  }, []);

  const getDueLabel = (dateStr: string) => {
    const days = differenceInDays(parseISO(dateStr), new Date());
    if (days < 0)  return { text: `${Math.abs(days)}d overdue`,  cls: 'text-lab-danger' };
    if (days === 0) return { text: 'Due today',                  cls: 'text-lab-warning' };
    if (days <= 2)  return { text: `Due in ${days}d`,            cls: 'text-lab-warning' };
    return           { text: `Due in ${days}d`,                  cls: 'text-lab-muted' };
  };

  return (
    <>
      <PageHeader
        title="My Borrowed Items"
        subtitle={`${borrows.length} active borrow${borrows.length !== 1 ? 's' : ''}`}
      />

      {isLoading ? (
        <FullPageSpinner />
      ) : borrows.length === 0 ? (
        <EmptyState
          icon={<ClipboardList size={28} />}
          title="No active borrows"
          description="Items you borrow will appear here with their return dates."
        />
      ) : (
        <div className="space-y-3">
          {borrows.map((record, i) => {
            const due = getDueLabel(record.expectedReturnDate);
            return (
              <div
                key={record.id}
                className="card p-5 flex items-center gap-5 animate-slide-up opacity-0"
                style={{ animationDelay: `${i * 0.06}s`, animationFillMode: 'forwards' }}
              >
                {/* Thumbnail */}
                <div className="w-16 h-16 rounded-lg bg-lab-bg border border-lab-border flex-shrink-0 overflow-hidden">
                  {record.imageUrl
                    ? <img src={record.imageUrl} alt={record.itemName} className="w-full h-full object-contain p-2" />
                    : <div className="w-full h-full flex items-center justify-center">
                        <ClipboardList size={20} className="text-lab-muted" />
                      </div>
                  }
                </div>

                {/* Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4">
                    <h3 className="font-display font-semibold text-sm text-lab-text truncate">
                      {record.itemName}
                    </h3>
                    <BorrowStatusBadge status={record.status} />
                  </div>
                  <div className="flex items-center gap-5 mt-2">
                    <span className="flex items-center gap-1.5 text-xs text-lab-muted">
                      <Calendar size={11} />
                      Borrowed {format(parseISO(record.borrowDate), 'MMM d, yyyy')}
                    </span>
                    <span className="flex items-center gap-1.5 text-xs text-lab-muted">
                      <Clock size={11} />
                      Return by{' '}
                      <span className="text-lab-text font-medium">
                        {format(parseISO(record.expectedReturnDate), 'MMM d, yyyy')}
                      </span>
                    </span>
                  </div>
                  {record.status === 'ACTIVE' && (
                    <p className={`text-xs font-mono font-medium mt-1.5 ${due.cls}`}>{due.text}</p>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </>
  );
}
