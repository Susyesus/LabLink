import { useState, useEffect, useCallback } from 'react';
import { Search, SlidersHorizontal, X } from 'lucide-react';
import { equipmentApi } from '@/services/api';
import { extractApiError } from '@/services/apiClient';
import { EquipmentCard } from '@/components/equipment/EquipmentCard';
import { BorrowModal } from '@/components/borrow/BorrowModal';
import { FullPageSpinner, EmptyState } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { Equipment, Category, EquipmentStatus } from '@/types';
import toast from 'react-hot-toast';

const STATUS_FILTERS: { label: string; value: EquipmentStatus | 'ALL' }[] = [
  { label: 'All',         value: 'ALL' },
  { label: 'Available',   value: 'AVAILABLE' },
  { label: 'In Use',      value: 'UNAVAILABLE' },
  { label: 'Maintenance', value: 'MAINTENANCE' },
];

export default function CatalogPage() {
  const [equipment, setEquipment]       = useState<Equipment[]>([]);
  const [categories, setCategories]     = useState<Category[]>([]);
  const [isLoading, setIsLoading]       = useState(true);
  const [search, setSearch]             = useState('');
  const [selectedCat, setSelectedCat]   = useState('ALL');
  const [statusFilter, setStatusFilter] = useState<EquipmentStatus | 'ALL'>('ALL');
  const [borrowTarget, setBorrowTarget] = useState<Equipment | null>(null);
  const [total, setTotal]               = useState(0);

  const fetchEquipment = useCallback(async () => {
    setIsLoading(true);
    try {
      const res = await equipmentApi.getAll({
        search:     search || undefined,
        status:     statusFilter !== 'ALL' ? statusFilter : undefined,
        categoryId: selectedCat !== 'ALL' ? selectedCat : undefined,
        limit: 24,
      });
      if (res.data.success && res.data.data) {
        setEquipment(res.data.data.equipment);
        setTotal(res.data.data.pagination.total);
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setIsLoading(false);
    }
  }, [search, statusFilter, selectedCat]);

  useEffect(() => { fetchEquipment(); }, [fetchEquipment]);

  useEffect(() => {
    equipmentApi.getCategories().then((res) => {
      if (res.data.success && res.data.data) setCategories(res.data.data.categories);
    }).catch(() => {});
  }, []);

  const clearFilters = () => {
    setSearch(''); setSelectedCat('ALL'); setStatusFilter('ALL');
  };
  const hasActiveFilters = search || selectedCat !== 'ALL' || statusFilter !== 'ALL';

  return (
    <>
      <PageHeader
        title="Equipment Catalog"
        subtitle={`${total} items in inventory`}
      />

      {/* Search + Filters bar */}
      <div className="flex flex-col gap-4 mb-8">
        <div className="flex items-center gap-3">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <Search size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search equipment..."
              className="input-field pl-10"
            />
            {search && (
              <button onClick={() => setSearch('')}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text">
                <X size={14} />
              </button>
            )}
          </div>

          {/* Status filter */}
          <div className="flex items-center gap-1 bg-lab-surface border border-lab-border rounded-lg p-1">
            {STATUS_FILTERS.map(({ label, value }) => (
              <button key={value} onClick={() => setStatusFilter(value)}
                className={`px-3 py-1.5 rounded-md text-xs font-mono font-medium transition-all duration-150
                  ${statusFilter === value
                    ? 'bg-lab-primary text-white'
                    : 'text-lab-muted hover:text-lab-text'}`}>
                {label}
              </button>
            ))}
          </div>

          {hasActiveFilters && (
            <button onClick={clearFilters} className="btn-ghost text-xs flex items-center gap-1.5">
              <SlidersHorizontal size={13} /> Clear
            </button>
          )}
        </div>

        {/* Category chips */}
        {categories.length > 0 && (
          <div className="flex items-center gap-2 flex-wrap">
            <button onClick={() => setSelectedCat('ALL')}
              className={`px-3 py-1 rounded-full text-xs font-mono font-medium border transition-all
                ${selectedCat === 'ALL'
                  ? 'bg-lab-accent/15 border-lab-accent/30 text-lab-accent'
                  : 'border-lab-border text-lab-muted hover:border-lab-accent/30 hover:text-lab-text'}`}>
              All Categories
            </button>
            {categories.map((cat) => (
              <button key={cat.id} onClick={() => setSelectedCat(cat.id)}
                className={`px-3 py-1 rounded-full text-xs font-mono font-medium border transition-all
                  ${selectedCat === cat.id
                    ? 'bg-lab-accent/15 border-lab-accent/30 text-lab-accent'
                    : 'border-lab-border text-lab-muted hover:border-lab-accent/30 hover:text-lab-text'}`}>
                {cat.name}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Grid */}
      {isLoading ? (
        <FullPageSpinner />
      ) : equipment.length === 0 ? (
        <EmptyState
          icon={<Search size={28} />}
          title="No items match your search"
          description="Try adjusting your filters or search terms."
          action={hasActiveFilters ? (
            <button onClick={clearFilters} className="btn-ghost text-sm">Clear filters</button>
          ) : undefined}
        />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
          {equipment.map((item, i) => (
            <EquipmentCard
              key={item.id}
              equipment={item}
              onBorrow={setBorrowTarget}
              animationDelay={`${Math.min(i * 0.05, 0.35)}s`}
            />
          ))}
        </div>
      )}

      {/* Borrow modal */}
      <BorrowModal
        equipment={borrowTarget}
        onClose={() => setBorrowTarget(null)}
        onSuccess={fetchEquipment}
      />
    </>
  );
}
