import { useState, useEffect } from 'react';
import { CheckCircle, AlertTriangle, ClipboardList } from 'lucide-react';
import { format, parseISO, isPast } from 'date-fns';
import { borrowApi } from '@/services/api';
import { FullPageSpinner, EmptyState, ConfirmDialog, BorrowStatusBadge } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { BorrowRecord } from '@/types';
import toast from 'react-hot-toast';
import { extractApiError } from '@/services/apiClient';

type FilterStatus = 'ALL' | 'ACTIVE' | 'OVERDUE' | 'RETURNED';

export default function AdminBorrowsPage() {
  const [borrows, setBorrows]           = useState<BorrowRecord[]>([]);
  const [isLoading, setIsLoading]       = useState(true);
  const [confirmId, setConfirmId]       = useState<string | null>(null);
  const [returning, setReturning]       = useState(false);
  const [filter, setFilter]             = useState<FilterStatus>('ALL');

  const fetchBorrows = () => {
    setIsLoading(true);
    borrowApi.getAllActive()
      .then((res) => { if (res.data.success && res.data.data) setBorrows(res.data.data.borrows); })
      .catch((err) => toast.error(extractApiError(err)))
      .finally(() => setIsLoading(false));
  };
  useEffect(fetchBorrows, []);

  const handleReturn = async () => {
    if (!confirmId) return;
    setReturning(true);
    try {
      const res = await borrowApi.verifyReturn(confirmId);
      if (res.data.success) { toast.success('Item marked as returned.'); fetchBorrows(); }
    } catch (err) { toast.error(extractApiError(err)); }
    finally { setReturning(false); setConfirmId(null); }
  };

  const isOverdue = (b: BorrowRecord) => b.status === 'ACTIVE' && isPast(parseISO(b.expectedReturnDate));

  const filtered = borrows.filter((b) => {
    if (filter === 'ALL')      return true;
    if (filter === 'OVERDUE')  return isOverdue(b);
    if (filter === 'ACTIVE')   return b.status === 'ACTIVE' && !isOverdue(b);
    return b.status === filter;
  });

  const tabs: { label: string; value: FilterStatus; count: number }[] = [
    { label: 'All',      value: 'ALL',      count: borrows.length },
    { label: 'Active',   value: 'ACTIVE',   count: borrows.filter(b => b.status === 'ACTIVE' && !isOverdue(b)).length },
    { label: 'Overdue',  value: 'OVERDUE',  count: borrows.filter(isOverdue).length },
    { label: 'Returned', value: 'RETURNED', count: borrows.filter(b => b.status === 'RETURNED').length },
  ];

  return (
    <>
      <PageHeader title="Borrow Records" subtitle="View and manage all equipment borrows" />

      {/* Filter tabs */}
      <div className="flex items-center gap-1 bg-lab-surface border border-lab-border rounded-lg p-1 mb-6 w-fit">
        {tabs.map(({ label, value, count }) => (
          <button key={value} onClick={() => setFilter(value)}
            className={`flex items-center gap-2 px-4 py-2 rounded-md text-xs font-mono font-medium transition-all
              ${filter === value
                ? 'bg-lab-primary text-white'
                : 'text-lab-muted hover:text-lab-text'}`}>
            {label}
            <span className={`px-1.5 py-0.5 rounded-full text-[10px]
              ${filter === value ? 'bg-white/20' : 'bg-lab-border text-lab-muted'}`}>
              {count}
            </span>
          </button>
        ))}
      </div>

      {isLoading ? <FullPageSpinner /> : filtered.length === 0 ? (
        <EmptyState icon={<ClipboardList size={28} />} title="No records match this filter" />
      ) : (
        <div className="card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-lab-border bg-lab-bg/50">
                  {['Equipment', 'Borrowed', 'Return By', 'Status', 'Action'].map((h) => (
                    <th key={h} className="px-5 py-3 text-left text-[11px] font-mono font-medium text-lab-muted uppercase tracking-wider">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-lab-border">
                {filtered.map((record) => {
                  const overdue = isOverdue(record);
                  return (
                    <tr key={record.id} className={`hover:bg-lab-surface/50 transition-colors ${overdue ? 'bg-lab-danger/3' : ''}`}>
                      <td className="px-5 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-lab-bg border border-lab-border flex items-center justify-center flex-shrink-0 overflow-hidden">
                            {record.imageUrl
                              ? <img src={record.imageUrl} alt="" className="w-full h-full object-contain p-1" />
                              : <ClipboardList size={12} className="text-lab-muted" />
                            }
                          </div>
                          <span className="font-display font-medium text-xs text-lab-text">{record.itemName}</span>
                        </div>
                      </td>
                      <td className="px-5 py-4 font-mono text-xs text-lab-muted">
                        {format(parseISO(record.borrowDate), 'MMM d, yyyy')}
                      </td>
                      <td className="px-5 py-4">
                        <span className={`font-mono text-xs font-medium ${overdue ? 'text-lab-danger' : 'text-lab-text'}`}>
                          {format(parseISO(record.expectedReturnDate), 'MMM d, yyyy')}
                        </span>
                        {overdue && (
                          <span className="flex items-center gap-1 text-[10px] text-lab-danger mt-0.5">
                            <AlertTriangle size={9} /> OVERDUE
                          </span>
                        )}
                      </td>
                      <td className="px-5 py-4">
                        <BorrowStatusBadge status={overdue ? 'OVERDUE' : record.status} />
                      </td>
                      <td className="px-5 py-4">
                        {(record.status === 'ACTIVE' || overdue) && (
                          <button onClick={() => setConfirmId(record.id)}
                            className="flex items-center gap-1.5 text-xs font-mono font-medium
                                       text-lab-success hover:bg-lab-success/15 px-3 py-1.5
                                       rounded-lg border border-lab-success/30 transition-all">
                            <CheckCircle size={12} /> Verify Return
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <ConfirmDialog
        open={!!confirmId}
        onClose={() => setConfirmId(null)}
        onConfirm={handleReturn}
        title="Verify Equipment Return"
        description="Confirm the student has returned this equipment. It will immediately become available for others to borrow."
        confirmLabel="Confirm Return"
        isLoading={returning}
      />
    </>
  );
}
