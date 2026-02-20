import { useState, useEffect } from 'react';
import { Package, ClipboardList, CheckCircle, AlertTriangle } from 'lucide-react';
import { format, parseISO, isPast } from 'date-fns';
import { borrowApi } from '@/services/api';
import { FullPageSpinner, EmptyState, ConfirmDialog, BorrowStatusBadge } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { BorrowRecord } from '@/types';
import toast from 'react-hot-toast';
import { extractApiError } from '@/services/apiClient';

export default function AdminDashboardPage() {
  const [borrows, setBorrows]     = useState<BorrowRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [confirmId, setConfirmId] = useState<string | null>(null);
  const [returning, setReturning] = useState(false);

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
      if (res.data.success) {
        toast.success('Item marked as returned and set to Available.');
        fetchBorrows();
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setReturning(false);
      setConfirmId(null);
    }
  };

  const active  = borrows.filter((b) => b.status === 'ACTIVE');
  const overdue = borrows.filter((b) => b.status === 'ACTIVE' && isPast(parseISO(b.expectedReturnDate)));

  const stats = [
    { label: 'Total Active',  value: active.length,  icon: ClipboardList, color: 'text-lab-primary',  bg: 'bg-lab-primary/10',  border: 'border-lab-primary/20' },
    { label: 'Overdue',       value: overdue.length,  icon: AlertTriangle, color: 'text-lab-danger',   bg: 'bg-lab-danger/10',   border: 'border-lab-danger/20' },
    { label: 'Returned Today',value: borrows.filter(b => b.status === 'RETURNED').length, icon: CheckCircle, color: 'text-lab-success', bg: 'bg-lab-success/10', border: 'border-lab-success/20' },
    { label: 'Total Records', value: borrows.length,  icon: Package,       color: 'text-lab-muted',    bg: 'bg-lab-surface',     border: 'border-lab-border' },
  ];

  return (
    <>
      <PageHeader title="Admin Dashboard" subtitle="Manage borrows and verify returns" />

      {/* Stats row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {stats.map(({ label, value, icon: Icon, color, bg, border }, i) => (
          <div key={label}
            className={`card p-5 flex items-center gap-4 animate-slide-up opacity-0 ${border}`}
            style={{ animationDelay: `${i * 0.07}s`, animationFillMode: 'forwards' }}>
            <div className={`w-10 h-10 rounded-xl ${bg} border ${border} flex items-center justify-center flex-shrink-0`}>
              <Icon size={18} className={color} />
            </div>
            <div>
              <p className="text-2xl font-display font-bold text-lab-text">{value}</p>
              <p className="text-xs font-mono text-lab-muted">{label}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Active borrows table */}
      <div className="card overflow-hidden">
        <div className="px-6 py-4 border-b border-lab-border flex items-center justify-between">
          <h2 className="font-display font-semibold text-sm text-lab-text">Active Borrow Records</h2>
          <span className="section-label">{active.length} records</span>
        </div>

        {isLoading ? (
          <div className="p-8"><FullPageSpinner /></div>
        ) : borrows.length === 0 ? (
          <EmptyState icon={<ClipboardList size={24} />} title="No borrow records found" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-lab-border bg-lab-bg/50">
                  {['Student', 'Equipment', 'Borrowed', 'Return By', 'Status', 'Action'].map((h) => (
                    <th key={h} className="px-5 py-3 text-left text-[11px] font-mono font-medium text-lab-muted uppercase tracking-wider">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-lab-border">
                {borrows.map((record) => {
                  const isOverdue = record.status === 'ACTIVE' && isPast(parseISO(record.expectedReturnDate));
                  return (
                    <tr key={record.id} className={`hover:bg-lab-surface/50 transition-colors ${isOverdue ? 'bg-lab-danger/3' : ''}`}>
                      <td className="px-5 py-4 font-body text-xs text-lab-text">—</td>
                      <td className="px-5 py-4">
                        <span className="font-display font-medium text-xs text-lab-text">{record.itemName}</span>
                      </td>
                      <td className="px-5 py-4 font-mono text-xs text-lab-muted">
                        {format(parseISO(record.borrowDate), 'MMM d, yyyy')}
                      </td>
                      <td className="px-5 py-4">
                        <span className={`font-mono text-xs font-medium ${isOverdue ? 'text-lab-danger' : 'text-lab-text'}`}>
                          {format(parseISO(record.expectedReturnDate), 'MMM d, yyyy')}
                        </span>
                        {isOverdue && <span className="block text-[10px] text-lab-danger mt-0.5">OVERDUE</span>}
                      </td>
                      <td className="px-5 py-4">
                        <BorrowStatusBadge status={isOverdue ? 'OVERDUE' : record.status} />
                      </td>
                      <td className="px-5 py-4">
                        {record.status === 'ACTIVE' && (
                          <button
                            onClick={() => setConfirmId(record.id)}
                            className="flex items-center gap-1.5 text-xs font-mono font-medium
                                       text-lab-success hover:text-white hover:bg-lab-success/20
                                       px-3 py-1.5 rounded-lg border border-lab-success/30 transition-all"
                          >
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
        )}
      </div>

      <ConfirmDialog
        open={!!confirmId}
        onClose={() => setConfirmId(null)}
        onConfirm={handleReturn}
        title="Verify Equipment Return"
        description="Confirm the student has physically returned this equipment. The item will be marked Available immediately."
        confirmLabel="Confirm Return"
        isLoading={returning}
      />
    </>
  );
}
