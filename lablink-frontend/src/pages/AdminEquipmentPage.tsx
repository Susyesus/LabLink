import { useState, useEffect, useCallback } from 'react';
import { Plus, Pencil, Trash2, Package } from 'lucide-react';
import { equipmentApi } from '@/services/api';
import { extractApiError } from '@/services/apiClient';
import { FullPageSpinner, EmptyState, ConfirmDialog, EquipmentStatusBadge, Modal, Spinner } from '@/components/ui';
import { PageHeader } from '@/components/layout/Sidebar';
import type { Equipment, Category, CreateEquipmentRequest, UpdateEquipmentRequest } from '@/types';
import toast from 'react-hot-toast';

// ── Equipment Form ─────────────────────────────────────────────
function EquipmentForm({
  initial,
  categories,
  onSubmit,
  isLoading,
}: {
  initial?: Equipment;
  categories: Category[];
  onSubmit: (data: CreateEquipmentRequest | UpdateEquipmentRequest) => void;
  isLoading: boolean;
}) {
  const [form, setForm] = useState({
    name:         initial?.name ?? '',
    description:  initial?.description ?? '',
    serialNumber: initial?.serialNumber ?? '',
    categoryId:   initial?.category.id ?? '',
    imageUrl:     initial?.imageUrl ?? '',
    status:       initial?.status ?? 'AVAILABLE',
  });

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) =>
    setForm(v => ({ ...v, [k]: e.target.value }));

  const handleSubmit = (ev: React.FormEvent) => {
    ev.preventDefault();
    if (!form.name.trim()) { toast.error('Name is required'); return; }
    if (!form.categoryId)  { toast.error('Category is required'); return; }
    onSubmit({ ...form, imageUrl: form.imageUrl || undefined });
  };

  const inputCls = 'input-field';
  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2">
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Name *</label>
          <input type="text" value={form.name} onChange={set('name')} placeholder="Arduino Uno R4" className={inputCls} required />
        </div>
        <div>
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Category *</label>
          <select value={form.categoryId} onChange={set('categoryId')} className={inputCls} required>
            <option value="">Select category</option>
            {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
        <div>
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Serial Number</label>
          <input type="text" value={form.serialNumber} onChange={set('serialNumber')} placeholder="SN-2024-001" className={inputCls} />
        </div>
        {initial && (
          <div>
            <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Status</label>
            <select value={form.status} onChange={set('status')} className={inputCls}>
              <option value="AVAILABLE">Available</option>
              <option value="UNAVAILABLE">Unavailable</option>
              <option value="MAINTENANCE">Maintenance</option>
            </select>
          </div>
        )}
        <div className={initial ? '' : 'col-span-2'}>
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Image URL</label>
          <input type="url" value={form.imageUrl} onChange={set('imageUrl')} placeholder="https://..." className={inputCls} />
        </div>
        <div className="col-span-2">
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Description</label>
          <textarea value={form.description} onChange={set('description')} rows={3} placeholder="Brief technical description..." className={`${inputCls} resize-none`} />
        </div>
      </div>
      <div className="flex justify-end gap-3 pt-2">
        <button type="submit" disabled={isLoading} className="btn-primary flex items-center gap-2">
          {isLoading ? <><Spinner size="sm" /> Saving...</> : (initial ? 'Update Equipment' : 'Add Equipment')}
        </button>
      </div>
    </form>
  );
}

// ── Page ──────────────────────────────────────────────────────
export default function AdminEquipmentPage() {
  const [equipment, setEquipment]   = useState<Equipment[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading]   = useState(true);
  const [formLoading, setFormLoading] = useState(false);
  const [showAdd, setShowAdd]       = useState(false);
  const [editTarget, setEditTarget] = useState<Equipment | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Equipment | null>(null);
  const [deleting, setDeleting]     = useState(false);

  const fetchAll = useCallback(async () => {
    setIsLoading(true);
    try {
      const [eqRes, catRes] = await Promise.all([equipmentApi.getAll({ limit: 100 }), equipmentApi.getCategories()]);
      if (eqRes.data.success && eqRes.data.data)   setEquipment(eqRes.data.data.equipment);
      if (catRes.data.success && catRes.data.data) setCategories(catRes.data.data.categories);
    } catch (err) { toast.error(extractApiError(err)); }
    finally { setIsLoading(false); }
  }, []);
  useEffect(() => { fetchAll(); }, [fetchAll]);

  const handleCreate = async (data: CreateEquipmentRequest | UpdateEquipmentRequest) => {
    setFormLoading(true);
    try {
      await equipmentApi.create(data as CreateEquipmentRequest);
      toast.success('Equipment added successfully.');
      setShowAdd(false); fetchAll();
    } catch (err) { toast.error(extractApiError(err)); }
    finally { setFormLoading(false); }
  };

  const handleUpdate = async (data: CreateEquipmentRequest | UpdateEquipmentRequest) => {
    if (!editTarget) return;
    setFormLoading(true);
    try {
      await equipmentApi.update(editTarget.id, data as UpdateEquipmentRequest);
      toast.success('Equipment updated successfully.');
      setEditTarget(null); fetchAll();
    } catch (err) { toast.error(extractApiError(err)); }
    finally { setFormLoading(false); }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await equipmentApi.delete(deleteTarget.id);
      toast.success('Equipment removed from catalog.');
      setDeleteTarget(null); fetchAll();
    } catch (err) { toast.error(extractApiError(err)); }
    finally { setDeleting(false); }
  };

  return (
    <>
      <PageHeader
        title="Equipment Inventory"
        subtitle={`${equipment.length} items total`}
        actions={
          <button onClick={() => setShowAdd(true)} className="btn-primary flex items-center gap-2 text-sm">
            <Plus size={15} /> Add Equipment
          </button>
        }
      />

      {isLoading ? <FullPageSpinner /> : equipment.length === 0 ? (
        <EmptyState icon={<Package size={28} />} title="No equipment yet"
          action={<button onClick={() => setShowAdd(true)} className="btn-primary text-sm">Add first item</button>} />
      ) : (
        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-lab-border bg-lab-bg/50">
                {['Equipment', 'Category', 'Serial No.', 'Status', 'Actions'].map((h) => (
                  <th key={h} className="px-5 py-3 text-left text-[11px] font-mono font-medium text-lab-muted uppercase tracking-wider">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-lab-border">
              {equipment.map((item) => (
                <tr key={item.id} className="hover:bg-lab-surface/50 transition-colors">
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-lg bg-lab-bg border border-lab-border flex items-center justify-center flex-shrink-0 overflow-hidden">
                        {item.imageUrl ? <img src={item.imageUrl} alt="" className="w-full h-full object-contain p-1" /> : <Package size={14} className="text-lab-muted" />}
                      </div>
                      <div>
                        <p className="font-display font-medium text-xs text-lab-text">{item.name}</p>
                        {item.description && <p className="text-[11px] text-lab-muted truncate max-w-[200px]">{item.description}</p>}
                      </div>
                    </div>
                  </td>
                  <td className="px-5 py-4 text-xs text-lab-muted font-mono">{item.category.name}</td>
                  <td className="px-5 py-4 text-xs text-lab-muted font-mono">{item.serialNumber || '—'}</td>
                  <td className="px-5 py-4"><EquipmentStatusBadge status={item.status} /></td>
                  <td className="px-5 py-4">
                    <div className="flex items-center gap-2">
                      <button onClick={() => setEditTarget(item)}
                        className="p-1.5 rounded-lg text-lab-muted hover:text-lab-text hover:bg-lab-border transition-colors">
                        <Pencil size={13} />
                      </button>
                      <button onClick={() => setDeleteTarget(item)}
                        className="p-1.5 rounded-lg text-lab-muted hover:text-lab-danger hover:bg-lab-danger/10 transition-colors">
                        <Trash2 size={13} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Add modal */}
      <Modal open={showAdd} onClose={() => setShowAdd(false)} title="Add New Equipment" maxWidth="max-w-xl">
        <EquipmentForm categories={categories} onSubmit={handleCreate} isLoading={formLoading} />
      </Modal>

      {/* Edit modal */}
      <Modal open={!!editTarget} onClose={() => setEditTarget(null)} title="Edit Equipment" maxWidth="max-w-xl">
        {editTarget && <EquipmentForm initial={editTarget} categories={categories} onSubmit={handleUpdate} isLoading={formLoading} />}
      </Modal>

      {/* Delete confirm */}
      <ConfirmDialog
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
        title="Delete Equipment"
        description={`Remove "${deleteTarget?.name}" from the catalog? This cannot be undone.`}
        confirmLabel="Delete"
        danger
        isLoading={deleting}
      />
    </>
  );
}
