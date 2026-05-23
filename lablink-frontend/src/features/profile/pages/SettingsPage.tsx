import { useState, useEffect, useRef } from 'react';
import { User, Lock, Camera, Save, Eye, EyeOff, Upload, Trash2, AlertCircle, CheckCircle2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { userApi } from '@/features/profile/api';
import { useAuthStore } from '@/features/auth/store/authStore';
import { Spinner } from '@/core/components/ui';
import { PageHeader } from '@/core/components/layout/Sidebar';
import { apiClient, extractApiError } from '@/core/api/apiClient';
import type { UserProfile } from '@/features/profile/types';

type Tab = 'profile' | 'password' | 'photo';

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState<Tab>('profile');
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loadingProfile, setLoadingProfile] = useState(true);

  // Fetch profile once on mount
  useEffect(() => {
    userApi.getProfile()
      .then((res) => { if (res.data.success && res.data.data) setProfile(res.data.data); })
      .catch((err) => toast.error(extractApiError(err)))
      .finally(() => setLoadingProfile(false));
  }, []);

  const tabs: { id: Tab; label: string; icon: React.ElementType }[] = [
    { id: 'profile',  label: 'Edit Profile',    icon: User },
    { id: 'password', label: 'Change Password', icon: Lock },
    { id: 'photo',    label: 'Profile Photo',   icon: Camera },
  ];

  return (
    <>
      <PageHeader title="Settings" subtitle="Manage your account details" />

      <div className="max-w-2xl mx-auto">
        {/* Tab bar */}
        <div className="flex gap-1 bg-lab-surface border border-lab-border rounded-xl p-1 mb-6">
          {tabs.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              onClick={() => setActiveTab(id)}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 px-3 rounded-lg
                          text-sm font-body font-medium transition-all duration-150
                          ${activeTab === id
                            ? 'bg-lab-primary text-white shadow-sm'
                            : 'text-lab-muted hover:text-lab-text hover:bg-lab-border/50'
                          }`}
            >
              <Icon size={14} />
              <span className="hidden sm:inline">{label}</span>
            </button>
          ))}
        </div>

        {/* Tab panels */}
        <div className="card p-6 animate-fade-in">
          {loadingProfile ? (
            <div className="flex justify-center py-12"><Spinner size="md" /></div>
          ) : (
            <>
              {activeTab === 'profile'  && <EditProfileTab  profile={profile} onUpdate={setProfile} />}
              {activeTab === 'password' && <ChangePasswordTab />}
              {activeTab === 'photo'    && <PhotoTab profile={profile} onUpdate={setProfile} />}
            </>
          )}
        </div>
      </div>
    </>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab 1 — Edit Profile
// ─────────────────────────────────────────────────────────────────────────────
function EditProfileTab({
  profile,
  onUpdate,
}: {
  profile: UserProfile | null;
  onUpdate: (p: UserProfile) => void;
}) {
  const { user, setAuth } = useAuthStore();
  const [fullName, setFullName] = useState(profile?.fullName ?? '');
  const [idNumber, setIdNumber] = useState(profile?.idNumber ?? '');
  const [errors, setErrors]     = useState<{ fullName?: string; idNumber?: string }>({});
  const [saving, setSaving]     = useState(false);

  const validate = () => {
    const e: typeof errors = {};
    if (!fullName.trim()) e.fullName = 'Full name is required';
    if (idNumber && !/^\d{2}-\d{4}-\d{3}$/.test(idNumber.trim()))
      e.idNumber = 'Format: XX-XXXX-XXX (e.g. 21-1234-567)';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSave = async () => {
    if (!validate()) return;
    setSaving(true);
    try {
      const res = await userApi.updateProfile({
        fullName: fullName.trim(),
        idNumber: idNumber.trim() || undefined,
      });
      if (res.data.success && res.data.data) {
        const updated = res.data.data;
        onUpdate(updated);
        // Sync name into auth store / localStorage so sidebar updates immediately
        if (user) {
          const token = localStorage.getItem('ll_access_token') ?? '';
          const refresh = localStorage.getItem('ll_refresh_token') ?? '';
          setAuth({ ...user, name: updated.fullName }, token, refresh);
        }
        toast.success('Profile updated.');
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-5">
      <div>
        <h2 className="font-display font-semibold text-base text-lab-text">Edit Profile</h2>
        <p className="text-xs text-lab-muted mt-0.5">Update your display name and student ID.</p>
      </div>

      {/* Full name */}
      <div>
        <label className="block text-xs font-mono font-medium text-lab-muted mb-2 uppercase tracking-wider">
          Full Name
        </label>
        <input
          type="text"
          value={fullName}
          onChange={(e) => { setFullName(e.target.value); setErrors(v => ({ ...v, fullName: undefined })); }}
          placeholder="Juan Dela Cruz"
          className={`input-field ${errors.fullName ? 'border-lab-danger' : ''}`}
        />
        {errors.fullName && <p className="text-xs text-lab-danger mt-1.5">{errors.fullName}</p>}
      </div>

      {/* Email — read-only */}
      <div>
        <label className="block text-xs font-mono font-medium text-lab-muted mb-2 uppercase tracking-wider">
          University Email
        </label>
        <input
          type="text"
          value={profile?.email ?? ''}
          readOnly
          className="input-field opacity-50 cursor-not-allowed"
        />
        <p className="text-[10px] text-lab-muted mt-1 font-mono">Email cannot be changed.</p>
      </div>

      {/* Student ID */}
      <div>
        <label className="block text-xs font-mono font-medium text-lab-muted mb-2 uppercase tracking-wider">
          Student ID
        </label>
        <input
          type="text"
          value={idNumber}
          onChange={(e) => { setIdNumber(e.target.value); setErrors(v => ({ ...v, idNumber: undefined })); }}
          placeholder="21-1234-567"
          className={`input-field ${errors.idNumber ? 'border-lab-danger' : ''}`}
        />
        {errors.idNumber && <p className="text-xs text-lab-danger mt-1.5">{errors.idNumber}</p>}
      </div>

      <button
        onClick={handleSave}
        disabled={saving}
        className="btn-primary flex items-center gap-2"
      >
        {saving ? <><Spinner size="sm" /> Saving...</> : <><Save size={14} /> Save Changes</>}
      </button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab 2 — Change Password
// ─────────────────────────────────────────────────────────────────────────────
function ChangePasswordTab() {
  const [form, setForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [show, setShow] = useState({ current: false, next: false, confirm: false });
  const [errors, setErrors] = useState<Partial<typeof form>>({});
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);

  const set = (key: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(v => ({ ...v, [key]: e.target.value }));
    setErrors(v => ({ ...v, [key]: undefined }));
    setSuccess(false);
  };

  const validate = () => {
    const e: Partial<typeof form> = {};
    if (!form.currentPassword) e.currentPassword = 'Current password is required';
    if (!form.newPassword)      e.newPassword     = 'New password is required';
    else if (form.newPassword.length < 8) e.newPassword = 'Minimum 8 characters';
    if (!form.confirmPassword)  e.confirmPassword = 'Please confirm your new password';
    else if (form.newPassword !== form.confirmPassword) e.confirmPassword = 'Passwords do not match';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSave = async () => {
    if (!validate()) return;
    setSaving(true);
    try {
      await userApi.changePassword(form);
      setSuccess(true);
      setForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
      toast.success('Password changed successfully.');
    } catch (err) {
      const msg = extractApiError(err);
      // Map backend error codes to inline field errors
      if (msg.toLowerCase().includes('current password')) {
        setErrors({ currentPassword: 'Current password is incorrect' });
      } else {
        toast.error(msg);
      }
    } finally {
      setSaving(false);
    }
  };

  const fields: {
    key: keyof typeof form;
    label: string;
    showKey: keyof typeof show;
  }[] = [
    { key: 'currentPassword', label: 'Current Password', showKey: 'current' },
    { key: 'newPassword',     label: 'New Password',     showKey: 'next' },
    { key: 'confirmPassword', label: 'Confirm Password', showKey: 'confirm' },
  ];

  return (
    <div className="space-y-5">
      <div>
        <h2 className="font-display font-semibold text-base text-lab-text">Change Password</h2>
        <p className="text-xs text-lab-muted mt-0.5">Enter your current password to confirm your identity.</p>
      </div>

      {success && (
        <div className="flex items-center gap-2.5 bg-lab-success/10 border border-lab-success/30
                        text-lab-success rounded-lg px-4 py-3 text-sm">
          <CheckCircle2 size={15} className="shrink-0" />
          Password changed successfully.
        </div>
      )}

      {fields.map(({ key, label, showKey }) => (
        <div key={key}>
          <label className="block text-xs font-mono font-medium text-lab-muted mb-2 uppercase tracking-wider">
            {label}
          </label>
          <div className="relative">
            <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
            <input
              type={show[showKey] ? 'text' : 'password'}
              value={form[key]}
              onChange={set(key)}
              autoComplete={key === 'currentPassword' ? 'current-password' : 'new-password'}
              placeholder="••••••••"
              className={`input-field pl-10 pr-10 ${errors[key] ? 'border-lab-danger' : ''}`}
            />
            <button
              type="button"
              onClick={() => setShow(v => ({ ...v, [showKey]: !v[showKey] }))}
              className="absolute right-3.5 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text transition-colors"
              tabIndex={-1}
            >
              {show[showKey] ? <EyeOff size={14} /> : <Eye size={14} />}
            </button>
          </div>
          {errors[key] && (
            <p className="flex items-center gap-1 text-xs text-lab-danger mt-1.5">
              <AlertCircle size={11} />{errors[key]}
            </p>
          )}
        </div>
      ))}

      <button onClick={handleSave} disabled={saving} className="btn-primary flex items-center gap-2">
        {saving ? <><Spinner size="sm" /> Saving...</> : <><Lock size={14} /> Update Password</>}
      </button>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab 3 — Profile Photo
// ─────────────────────────────────────────────────────────────────────────────
function PhotoTab({
  profile,
  onUpdate,
}: {
  profile: UserProfile | null;
  onUpdate: (p: UserProfile) => void;
}) {
  const fileRef = useRef<HTMLInputElement>(null);
  const [preview, setPreview]   = useState<string | null>(null);
  const [file, setFile]         = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError]       = useState<string | null>(null);

  // Build the URL that will fetch the actual stored photo (with auth header via axios interceptor)
  const [photoSrc, setPhotoSrc] = useState<string | null>(null);

  useEffect(() => {
    if (profile?.hasPhoto) {
      // Fetch the photo as a blob via apiClient so auth interceptors handle tokens
      apiClient.get('/users/me/photo', { responseType: 'blob' })
        .then((res) => setPhotoSrc(URL.createObjectURL(res.data)))
        .catch(() => setPhotoSrc(null));
    }
  }, [profile?.hasPhoto]);

  const handleFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0];
    if (!f) return;
    setError(null);

    if (!['image/jpeg', 'image/png'].includes(f.type)) {
      setError('Only JPEG and PNG images are accepted.');
      return;
    }
    if (f.size > 5 * 1024 * 1024) {
      setError('Photo must be smaller than 5 MB.');
      return;
    }

    setFile(f);
    setPreview(URL.createObjectURL(f));
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    try {
      const res = await userApi.uploadPhoto(file);
      if (res.data.success && res.data.data) {
        onUpdate(res.data.data);
        setPhotoSrc(preview);
        setPreview(null);
        setFile(null);
        toast.success('Profile photo updated.');
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setUploading(false);
    }
  };

  const handleDiscard = () => {
    setFile(null);
    setPreview(null);
    setError(null);
    if (fileRef.current) fileRef.current.value = '';
  };

  const displaySrc = preview ?? photoSrc;
  const initials   = profile?.fullName?.charAt(0).toUpperCase() ?? '?';

  return (
    <div className="space-y-5">
      <div>
        <h2 className="font-display font-semibold text-base text-lab-text">Profile Photo</h2>
        <p className="text-xs text-lab-muted mt-0.5">Upload a JPEG or PNG — max 5 MB.</p>
      </div>

      {/* Avatar preview */}
      <div className="flex flex-col items-center gap-4">
        <div className="relative">
          <div className="w-28 h-28 rounded-2xl bg-lab-primary/15 border-2 border-lab-primary/30
                          flex items-center justify-center overflow-hidden">
            {displaySrc ? (
              <img src={displaySrc} alt="Profile" className="w-full h-full object-cover" />
            ) : (
              <span className="font-display font-bold text-4xl text-lab-primary">{initials}</span>
            )}
          </div>
          {/* Camera button overlay */}
          <button
            onClick={() => fileRef.current?.click()}
            className="absolute -bottom-2 -right-2 w-8 h-8 rounded-full bg-lab-primary
                       flex items-center justify-center shadow-lg hover:brightness-110 transition-all"
          >
            <Camera size={14} className="text-white" />
          </button>
        </div>

        {/* Hidden file input */}
        <input
          ref={fileRef}
          type="file"
          accept="image/jpeg,image/png"
          onChange={handleFile}
          className="hidden"
        />

        {/* File name + size */}
        {file && (
          <div className="text-center">
            <p className="text-sm font-body text-lab-text">{file.name}</p>
            <p className="text-xs font-mono text-lab-muted mt-0.5">
              {(file.size / 1024).toFixed(0)} KB
            </p>
          </div>
        )}

        {error && (
          <div className="flex items-center gap-2 text-xs text-lab-danger">
            <AlertCircle size={12} />{error}
          </div>
        )}
      </div>

      {/* Actions */}
      <div className="flex gap-3">
        <button
          onClick={() => fileRef.current?.click()}
          className="btn-ghost flex items-center gap-2 flex-1"
        >
          <Upload size={14} /> Choose Photo
        </button>

        {file && (
          <>
            <button
              onClick={handleUpload}
              disabled={uploading}
              className="btn-primary flex items-center gap-2 flex-1"
            >
              {uploading ? <><Spinner size="sm" /> Uploading...</> : <><Camera size={14} /> Upload</>}
            </button>
            <button
              onClick={handleDiscard}
              disabled={uploading}
              className="btn-ghost flex items-center gap-2 px-3 text-lab-danger hover:bg-lab-danger/10"
              title="Discard"
            >
              <Trash2 size={14} />
            </button>
          </>
        )}
      </div>

      <p className="text-[11px] font-mono text-lab-muted">
        Accepted: .jpg, .png · Max size: 5 MB
      </p>
    </div>
  );
}
