import { useState, useEffect } from 'react';
import { User, Mail, Shield, Calendar, ClipboardList } from 'lucide-react';
import { format, parseISO } from 'date-fns';
import { borrowApi } from '@/services/api';
import { useAuthStore } from '@/store/authStore';
import { FullPageSpinner, BorrowStatusBadge } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { BorrowRecord } from '@/types';
import { extractApiError } from '@/services/apiClient';
import toast from 'react-hot-toast';

export default function ProfilePage() {
  const user = useAuthStore((s) => s.user);
  const [borrows, setBorrows] = useState<BorrowRecord[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    borrowApi.getMyBorrows()
      .then((res) => { if (res.data.success && res.data.data) setBorrows(res.data.data.activeBorrows); })
      .catch((err) => toast.error(extractApiError(err)))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <>
      <PageHeader title="My Profile" subtitle="Account details and borrow history" />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Account card */}
        <div className="card p-6 flex flex-col gap-5 animate-fade-in">
          {/* Avatar */}
          <div className="flex flex-col items-center gap-3 pb-5 border-b border-lab-border">
            <div className="w-16 h-16 rounded-2xl bg-lab-primary/15 border border-lab-primary/30
                            flex items-center justify-center">
              <span className="font-display font-bold text-2xl text-lab-primary">
                {user?.name.charAt(0).toUpperCase()}
              </span>
            </div>
            <div className="text-center">
              <h2 className="font-display font-bold text-base text-lab-text">{user?.name}</h2>
              <p className="text-xs text-lab-muted font-mono mt-0.5">{user?.email}</p>
            </div>
          </div>

          {/* Details */}
          <div className="space-y-3">
            {[
              { icon: User,    label: 'Full Name',  value: user?.name ?? '—' },
              { icon: Mail,    label: 'Email',      value: user?.email ?? '—' },
              { icon: Shield,  label: 'Role',       value: user?.role ?? '—' },
            ].map(({ icon: Icon, label, value }) => (
              <div key={label} className="flex items-start gap-3">
                <div className="w-7 h-7 rounded-lg bg-lab-bg border border-lab-border flex items-center justify-center flex-shrink-0 mt-0.5">
                  <Icon size={12} className="text-lab-muted" />
                </div>
                <div>
                  <p className="text-[10px] font-mono text-lab-muted uppercase tracking-wider">{label}</p>
                  <p className="text-sm font-body text-lab-text">{value}</p>
                </div>
              </div>
            ))}
          </div>

          {/* Stats */}
          <div className="pt-4 border-t border-lab-border grid grid-cols-2 gap-3">
            <div className="bg-lab-bg rounded-lg border border-lab-border p-3 text-center">
              <p className="font-display font-bold text-xl text-lab-primary">
                {borrows.filter(b => b.status === 'ACTIVE').length}
              </p>
              <p className="text-[10px] font-mono text-lab-muted mt-0.5">Active</p>
            </div>
            <div className="bg-lab-bg rounded-lg border border-lab-border p-3 text-center">
              <p className="font-display font-bold text-xl text-lab-text">{borrows.length}</p>
              <p className="text-[10px] font-mono text-lab-muted mt-0.5">Total</p>
            </div>
          </div>
        </div>

        {/* Borrow history */}
        <div className="lg:col-span-2 card overflow-hidden animate-slide-up opacity-0"
             style={{ animationDelay: '0.1s', animationFillMode: 'forwards' }}>
          <div className="px-6 py-4 border-b border-lab-border flex items-center gap-2">
            <ClipboardList size={14} className="text-lab-muted" />
            <h3 className="font-display font-semibold text-sm text-lab-text">Borrow History</h3>
          </div>

          {isLoading ? (
            <div className="p-8"><FullPageSpinner /></div>
          ) : borrows.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <ClipboardList size={32} className="text-lab-border mb-3" />
              <p className="text-sm font-body text-lab-muted">No borrow records yet.</p>
            </div>
          ) : (
            <div className="divide-y divide-lab-border">
              {borrows.map((record) => (
                <div key={record.id} className="flex items-center gap-4 px-6 py-4 hover:bg-lab-surface/50 transition-colors">
                  <div className="w-10 h-10 rounded-lg bg-lab-bg border border-lab-border flex-shrink-0 overflow-hidden">
                    {record.imageUrl
                      ? <img src={record.imageUrl} alt={record.itemName} className="w-full h-full object-contain p-1.5" />
                      : <div className="w-full h-full flex items-center justify-center"><ClipboardList size={14} className="text-lab-muted" /></div>
                    }
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-display font-medium text-sm text-lab-text truncate">{record.itemName}</p>
                    <div className="flex items-center gap-3 mt-0.5">
                      <span className="flex items-center gap-1 text-[11px] font-mono text-lab-muted">
                        <Calendar size={9} />
                        {format(parseISO(record.borrowDate), 'MMM d, yyyy')}
                      </span>
                      <span className="text-[11px] font-mono text-lab-muted">
                        → Return by {format(parseISO(record.expectedReturnDate), 'MMM d, yyyy')}
                      </span>
                    </div>
                  </div>
                  <BorrowStatusBadge status={record.status} />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
