import { useState } from 'react';
import { Calendar, FileText, FlaskConical } from 'lucide-react';
import { format, addDays } from 'date-fns';
import toast from 'react-hot-toast';
import { Modal, Spinner, EquipmentStatusBadge } from '@/components/ui';
import { borrowApi } from '@/services/api';
import { extractApiError } from '@/services/apiClient';
import type { Equipment } from '@/types';

interface BorrowModalProps {
  equipment: Equipment | null;
  onClose: () => void;
  onSuccess: () => void;
}

/** Borrow confirmation modal — Journey 10 in the SDD */
export function BorrowModal({ equipment, onClose, onSuccess }: BorrowModalProps) {
  const today = new Date();
  const minDate = format(addDays(today, 1), 'yyyy-MM-dd');
  const maxDate = format(addDays(today, 7), 'yyyy-MM-dd');

  const [returnDate, setReturnDate] = useState(format(addDays(today, 3), 'yyyy-MM-dd'));
  const [purpose, setPurpose]       = useState('');
  const [isLoading, setIsLoading]   = useState(false);

  const handleSubmit = async () => {
    if (!equipment) return;
    if (!returnDate) { toast.error('Please select a return date.'); return; }

    setIsLoading(true);
    try {
      const res = await borrowApi.borrow({
        equipmentId: equipment.id,
        expectedReturnDate: returnDate,
        purpose: purpose.trim() || undefined,
      });
      if (res.data.success) {
        toast.success('Reservation confirmed! Pick up at the Lab Admin desk.');
        onSuccess();
        onClose();
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Modal open={!!equipment} onClose={onClose} title="Borrow Equipment">
      {equipment && (
        <div className="space-y-5">
          {/* Equipment summary */}
          <div className="flex items-start gap-4 p-4 bg-lab-bg rounded-lg border border-lab-border">
            <div className="w-14 h-14 rounded-lg bg-lab-surface border border-lab-border flex items-center justify-center flex-shrink-0">
              {equipment.imageUrl
                ? <img src={equipment.imageUrl} alt={equipment.name} className="w-full h-full object-contain p-1.5 rounded-lg" />
                : <FlaskConical size={20} className="text-lab-muted" />
              }
            </div>
            <div className="flex-1 min-w-0">
              <h3 className="font-display font-semibold text-sm text-lab-text">{equipment.name}</h3>
              <p className="text-xs text-lab-muted mt-0.5">{equipment.category.name}</p>
              <div className="mt-2">
                <EquipmentStatusBadge status={equipment.status} />
              </div>
            </div>
          </div>

          {/* Return date */}
          <div>
            <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wide uppercase">
              <Calendar size={11} className="inline mr-1.5" />
              Expected Return Date
            </label>
            <input
              type="date"
              value={returnDate}
              min={minDate}
              max={maxDate}
              onChange={(e) => setReturnDate(e.target.value)}
              className="input-field"
            />
            <p className="text-[11px] text-lab-muted mt-1.5">
              Maximum loan period is 7 days.
            </p>
          </div>

          {/* Purpose */}
          <div>
            <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wide uppercase">
              <FileText size={11} className="inline mr-1.5" />
              Purpose / Project (optional)
            </label>
            <textarea
              value={purpose}
              onChange={(e) => setPurpose(e.target.value)}
              placeholder="e.g. IoT project for IT342 course..."
              rows={3}
              className="input-field resize-none"
            />
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end gap-3 pt-2">
            <button onClick={onClose} className="btn-ghost" disabled={isLoading}>
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              className="btn-primary flex items-center gap-2"
              disabled={isLoading}
            >
              {isLoading ? <><Spinner size="sm" /> Processing...</> : 'Confirm Reservation'}
            </button>
          </div>
        </div>
      )}
    </Modal>
  );
}
