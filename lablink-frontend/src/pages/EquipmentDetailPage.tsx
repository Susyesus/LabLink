import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Package, Tag, Hash, Info } from 'lucide-react';
import { equipmentApi } from '@/services/api';
import { extractApiError } from '@/services/apiClient';
import { FullPageSpinner, EquipmentStatusBadge } from '@/components/ui';
import { BorrowModal } from '@/components/borrow/BorrowModal';
import { useAuthStore } from '@/store/authStore';
import type { Equipment } from '@/types';
import toast from 'react-hot-toast';

export default function EquipmentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const user = useAuthStore((s) => s.user);

  const [equipment, setEquipment] = useState<Equipment | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [borrowTarget, setBorrowTarget] = useState<Equipment | null>(null);

  useEffect(() => {
    if (!id) return;
    equipmentApi.getById(id)
      .then((res) => {
        if (res.data.success && res.data.data) setEquipment(res.data.data.item);
      })
      .catch((err) => toast.error(extractApiError(err)))
      .finally(() => setIsLoading(false));
  }, [id]);

  if (isLoading) return <FullPageSpinner />;
  if (!equipment) return (
    <div className="text-center py-20 text-lab-muted font-body">Equipment not found.</div>
  );

  const isAvailable = equipment.status === 'AVAILABLE';

  return (
    <>
      {/* Back nav */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-sm text-lab-muted hover:text-lab-text
                   transition-colors mb-8 font-body"
      >
        <ArrowLeft size={15} /> Back to Catalog
      </button>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 animate-fade-in">
        {/* Left: Image */}
        <div className="card overflow-hidden">
          <div className="aspect-square bg-lab-bg flex items-center justify-center p-10">
            {equipment.imageUrl ? (
              <img
                src={equipment.imageUrl}
                alt={equipment.name}
                className="max-w-full max-h-full object-contain"
              />
            ) : (
              <Package size={80} className="text-lab-border" />
            )}
          </div>
          {/* Serial number footer */}
          {equipment.serialNumber && (
            <div className="px-5 py-3 border-t border-lab-border flex items-center gap-2">
              <Hash size={12} className="text-lab-muted" />
              <span className="font-mono text-xs text-lab-muted">S/N: {equipment.serialNumber}</span>
            </div>
          )}
        </div>

        {/* Right: Details */}
        <div className="flex flex-col gap-6">
          {/* Header */}
          <div>
            <div className="flex items-center gap-3 mb-3">
              <span className="flex items-center gap-1.5 text-xs font-mono font-medium
                               text-lab-accent bg-lab-accent/10 border border-lab-accent/20
                               px-2.5 py-0.5 rounded-full">
                <Tag size={10} /> {equipment.category.name}
              </span>
              <EquipmentStatusBadge status={equipment.status} />
            </div>
            <h1 className="font-display font-bold text-2xl text-lab-text leading-tight">
              {equipment.name}
            </h1>
          </div>

          {/* Description */}
          {equipment.description && (
            <div className="card p-5">
              <div className="flex items-center gap-2 mb-3">
                <Info size={13} className="text-lab-muted" />
                <span className="section-label">Description</span>
              </div>
              <p className="text-sm text-lab-muted font-body leading-relaxed">
                {equipment.description}
              </p>
            </div>
          )}

          {/* Specs grid */}
          <div className="card p-5">
            <span className="section-label block mb-4">Specifications</span>
            <div className="space-y-3">
              {[
                { label: 'Category',      value: equipment.category.name },
                { label: 'Serial Number', value: equipment.serialNumber || '—' },
                { label: 'Availability',  value: equipment.status },
              ].map(({ label, value }) => (
                <div key={label} className="flex items-center justify-between">
                  <span className="text-xs font-mono text-lab-muted">{label}</span>
                  <span className="text-xs font-body font-medium text-lab-text">{value}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Borrow action */}
          {user?.role === 'STUDENT' && (
            <div className="mt-auto">
              {isAvailable ? (
                <button
                  onClick={() => setBorrowTarget(equipment)}
                  className="btn-primary w-full py-3 text-base"
                >
                  Borrow This Item
                </button>
              ) : (
                <div className="w-full py-3 text-center rounded-lg border border-lab-border
                                text-sm text-lab-muted font-body bg-lab-surface/50">
                  This item is currently {equipment.status.toLowerCase()} — check back later.
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <BorrowModal
        equipment={borrowTarget}
        onClose={() => setBorrowTarget(null)}
        onSuccess={() => navigate('/my-items')}
      />
    </>
  );
}
