import { X } from 'lucide-react';
import type { EquipmentStatus, BorrowStatus } from '@/types';

// ── Spinner ───────────────────────────────────────────────────
export function Spinner({ size = 'md' }: { size?: 'sm' | 'md' | 'lg' }) {
  const s = { sm: 'w-4 h-4', md: 'w-6 h-6', lg: 'w-10 h-10' }[size];
  return (
    <div className={`${s} border-2 border-lab-border border-t-lab-primary rounded-full animate-spin`} />
  );
}

export function FullPageSpinner() {
  return (
    <div className="flex items-center justify-center min-h-[400px]">
      <Spinner size="lg" />
    </div>
  );
}

// ── Status Badges ─────────────────────────────────────────────
export function EquipmentStatusBadge({ status }: { status: EquipmentStatus }) {
  const config = {
    AVAILABLE:   { cls: 'badge-available',    dot: 'bg-lab-success',  label: 'Available' },
    UNAVAILABLE: { cls: 'badge-unavailable',  dot: 'bg-lab-danger',   label: 'In Use' },
    MAINTENANCE: { cls: 'badge-maintenance',  dot: 'bg-lab-warning',  label: 'Maintenance' },
  }[status];

  return (
    <span className={config.cls}>
      <span className={`w-1.5 h-1.5 rounded-full ${config.dot} animate-pulse-slow`} />
      {config.label}
    </span>
  );
}

export function BorrowStatusBadge({ status }: { status: BorrowStatus }) {
  const config = {
    ACTIVE:   { cls: 'badge-available',    label: 'Active' },
    RETURNED: { cls: 'badge-unavailable',  label: 'Returned' },
    OVERDUE:  { cls: 'badge-maintenance',  label: 'Overdue' },
  }[status];
  return <span className={config.cls}>{config.label}</span>;
}

// ── Empty State ───────────────────────────────────────────────
export function EmptyState({
  icon,
  title,
  description,
  action,
}: {
  icon: React.ReactNode;
  title: string;
  description?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex flex-col items-center justify-center py-20 text-center animate-fade-in">
      <div className="w-16 h-16 rounded-2xl bg-lab-surface border border-lab-border flex items-center justify-center mb-4 text-lab-muted">
        {icon}
      </div>
      <h3 className="font-display font-semibold text-base text-lab-text mb-1">{title}</h3>
      {description && <p className="text-sm text-lab-muted max-w-xs">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}

// ── Modal ─────────────────────────────────────────────────────
export function Modal({
  open,
  onClose,
  title,
  children,
  maxWidth = 'max-w-lg',
}: {
  open: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  maxWidth?: string;
}) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm animate-fade-in"
        onClick={onClose}
      />
      {/* Panel */}
      <div className={`relative w-full ${maxWidth} card animate-slide-up shadow-2xl shadow-black/50`}>
        <div className="flex items-center justify-between px-6 py-4 border-b border-lab-border">
          <h2 className="font-display font-semibold text-base text-lab-text">{title}</h2>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-lab-border text-lab-muted hover:text-lab-text transition-colors"
          >
            <X size={16} />
          </button>
        </div>
        <div className="px-6 py-5">{children}</div>
      </div>
    </div>
  );
}

// ── Confirm Dialog ────────────────────────────────────────────
export function ConfirmDialog({
  open,
  onClose,
  onConfirm,
  title,
  description,
  confirmLabel = 'Confirm',
  danger = false,
  isLoading = false,
}: {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  description: string;
  confirmLabel?: string;
  danger?: boolean;
  isLoading?: boolean;
}) {
  return (
    <Modal open={open} onClose={onClose} title={title} maxWidth="max-w-sm">
      <p className="text-sm text-lab-muted mb-6">{description}</p>
      <div className="flex items-center justify-end gap-3">
        <button onClick={onClose} className="btn-ghost text-sm">Cancel</button>
        <button
          onClick={onConfirm}
          disabled={isLoading}
          className={danger ? 'btn-danger text-sm' : 'btn-primary text-sm'}
        >
          {isLoading ? <Spinner size="sm" /> : confirmLabel}
        </button>
      </div>
    </Modal>
  );
}
