import { Package, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import { EquipmentStatusBadge } from '@/components/ui';
import type { Equipment } from '@/types';

interface EquipmentCardProps {
  equipment: Equipment;
  /** If true, shows a "Borrow" button instead of a link */
  showBorrowAction?: boolean;
  onBorrow?: (equipment: Equipment) => void;
  animationDelay?: string;
}

export function EquipmentCard({ equipment, onBorrow, animationDelay = '0s' }: EquipmentCardProps) {
  const isAvailable = equipment.status === 'AVAILABLE';

  return (
    <div
      className="card group overflow-hidden flex flex-col transition-all duration-300
                 hover:border-lab-primary/30 hover:shadow-lg hover:shadow-lab-primary/5
                 animate-slide-up opacity-0"
      style={{ animationDelay, animationFillMode: 'forwards' }}
    >
      {/* Image */}
      <div className="relative aspect-[4/3] bg-lab-bg overflow-hidden border-b border-lab-border">
        {equipment.imageUrl ? (
          <img
            src={equipment.imageUrl}
            alt={equipment.name}
            className="w-full h-full object-contain p-4 transition-transform duration-500 group-hover:scale-105"
          />
        ) : (
          <div className="flex items-center justify-center h-full">
            <Package size={40} className="text-lab-border" />
          </div>
        )}
        {/* Status badge overlay */}
        <div className="absolute top-3 right-3">
          <EquipmentStatusBadge status={equipment.status} />
        </div>
        {/* Category chip */}
        <div className="absolute top-3 left-3">
          <span className="text-[10px] font-mono font-medium text-lab-muted
                           bg-lab-surface/80 backdrop-blur-sm border border-lab-border
                           px-2 py-0.5 rounded-full">
            {equipment.category.name}
          </span>
        </div>
      </div>

      {/* Content */}
      <div className="p-4 flex flex-col flex-1 gap-3">
        <div>
          <h3 className="font-display font-semibold text-sm text-lab-text leading-tight line-clamp-1">
            {equipment.name}
          </h3>
          <p className="text-xs text-lab-muted mt-1 line-clamp-2 font-body leading-relaxed">
            {equipment.description}
          </p>
        </div>

        {equipment.serialNumber && (
          <div className="flex items-center gap-1.5 text-[10px] font-mono text-lab-muted">
            <MapPin size={10} />
            <span>S/N: {equipment.serialNumber}</span>
          </div>
        )}

        {/* Action */}
        <div className="mt-auto pt-1">
          {isAvailable ? (
            <button
              onClick={() => onBorrow?.(equipment)}
              className="btn-primary w-full text-xs py-2"
            >
              Borrow Equipment
            </button>
          ) : (
            <Link
              to={`/catalog/${equipment.id}`}
              className="flex items-center justify-center w-full px-4 py-2 rounded-lg
                         border border-lab-border text-lab-muted text-xs font-body
                         hover:border-lab-primary/30 hover:text-lab-text transition-all"
            >
              View Details
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}
