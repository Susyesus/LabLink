import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, User, Eye, EyeOff, KeyRound, Shield } from 'lucide-react';
import { Spinner } from '@/core/components/ui';
import { ThemeToggle } from '@/core/components/ui/ThemeToggle';
import { authApi } from '@/features/auth/api';
import { useAuthStore } from '@/features/auth/store/authStore';
import { extractApiError } from '@/core/api/apiClient';
import toast from 'react-hot-toast';

interface FormErrors {
  fullName?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
  adminSecret?: string;
}

export default function AdminRegisterPage() {
  const { setAuth } = useAuthStore();
  const navigate    = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [showPw, setShowPw]       = useState(false);
  const [showSecret, setShowSecret] = useState(false);
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', confirmPassword: '', adminSecret: '',
  });
  const [errors, setErrors] = useState<FormErrors>({});

  const set = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(v => ({ ...v, [field]: e.target.value }));
    setErrors(v => ({ ...v, [field]: undefined }));
  };

  const validate = (): boolean => {
    const e: FormErrors = {};
    if (!form.fullName.trim())  e.fullName    = 'Full name is required';
    if (!form.email.trim())     e.email       = 'Email is required';
    else if (!/^[a-zA-Z]+\.[a-zA-Z]+@cit\.edu$/.test(form.email))
                                e.email       = 'Must be a valid CIT email (firstname.lastname@cit.edu)';
    if (form.password.length < 8) e.password  = 'Password must be at least 8 characters';
    if (form.password !== form.confirmPassword) e.confirmPassword = 'Passwords do not match';
    if (!form.adminSecret.trim()) e.adminSecret = 'Admin secret key is required';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (ev: React.FormEvent) => {
    ev.preventDefault();
    if (!validate()) return;
    setIsLoading(true);
    try {
      const res = await authApi.registerAdmin({
        fullName:     form.fullName,
        email:        form.email,
        password:     form.password,
        confirmPassword: form.confirmPassword,
        adminSecret:  form.adminSecret,
      });
      if (res.data.success && res.data.data) {
        const { user, token, refreshToken } = res.data.data;
        setAuth(user, token, refreshToken);
        toast.success(`Admin account created. Welcome, ${user.name.split(' ')[0]}!`);
        navigate('/admin');
      }
    } catch (err) {
      toast.error(extractApiError(err));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-lab-bg flex items-center justify-center p-6 noise-bg">
      <div className="absolute inset-0 bg-[linear-gradient(to_right,var(--color-border)_1px,transparent_1px),linear-gradient(to_bottom,var(--color-border)_1px,transparent_1px)]
                      bg-[size:48px_48px] opacity-30 pointer-events-none" />
      <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96
                      bg-lab-warning/6 rounded-full blur-3xl pointer-events-none" />

      <div className="fixed top-4 right-4 z-50">
        <ThemeToggle />
      </div>

      <div className="relative w-full max-w-md animate-slide-up">
        <div className="flex flex-col items-center mb-10">
          <div className="flex items-center justify-center w-12 h-12 rounded-2xl
                          bg-lab-warning/15 border border-lab-warning/30 mb-4">
            <Shield size={22} className="text-lab-warning" />
          </div>
          <h1 className="font-display font-bold text-2xl text-lab-text">Admin Registration</h1>
          <p className="text-sm text-lab-muted mt-1">Requires the admin secret key</p>
        </div>

        <div className="card p-8">
          {/* Warning banner */}
          <div className="flex items-start gap-3 p-3 rounded-lg bg-lab-warning/10 border border-lab-warning/20 mb-6">
            <Shield size={14} className="text-lab-warning mt-0.5 flex-shrink-0" />
            <p className="text-xs text-lab-warning/90">
              Admin accounts have full system access. Only authorized lab staff should register here.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Full Name */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Full Name</label>
              <div className="relative">
                <User size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input type="text" autoComplete="name" value={form.fullName} onChange={set('fullName')}
                  placeholder="Lab Administrator" className={`input-field pl-10 ${errors.fullName ? 'border-lab-danger' : ''}`} />
              </div>
              {errors.fullName && <p className="text-xs text-lab-danger mt-1.5">{errors.fullName}</p>}
            </div>

            {/* Email */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">University Email</label>
              <div className="relative">
                <Mail size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input type="email" autoComplete="email" value={form.email} onChange={set('email')}
                  placeholder="admin.name@cit.edu" className={`input-field pl-10 ${errors.email ? 'border-lab-danger' : ''}`} />
              </div>
              {errors.email && <p className="text-xs text-lab-danger mt-1.5">{errors.email}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Password</label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input type={showPw ? 'text' : 'password'} autoComplete="new-password"
                  value={form.password} onChange={set('password')} placeholder="••••••••"
                  className={`input-field pl-10 pr-10 ${errors.password ? 'border-lab-danger' : ''}`} />
                <button type="button" tabIndex={-1}
                  onClick={() => setShowPw(v => !v)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text transition-colors">
                  {showPw ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-lab-danger mt-1.5">{errors.password}</p>}
            </div>

            {/* Confirm Password */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Confirm Password</label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input type={showPw ? 'text' : 'password'} autoComplete="new-password"
                  value={form.confirmPassword} onChange={set('confirmPassword')} placeholder="••••••••"
                  className={`input-field pl-10 ${errors.confirmPassword ? 'border-lab-danger' : ''}`} />
              </div>
              {errors.confirmPassword && <p className="text-xs text-lab-danger mt-1.5">{errors.confirmPassword}</p>}
            </div>

            {/* Admin Secret */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">Admin Secret Key</label>
              <div className="relative">
                <KeyRound size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input type={showSecret ? 'text' : 'password'} autoComplete="off"
                  value={form.adminSecret} onChange={set('adminSecret')} placeholder="Enter secret key"
                  className={`input-field pl-10 pr-10 ${errors.adminSecret ? 'border-lab-danger' : ''}`} />
                <button type="button" tabIndex={-1}
                  onClick={() => setShowSecret(v => !v)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text transition-colors">
                  {showSecret ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              </div>
              {errors.adminSecret && <p className="text-xs text-lab-danger mt-1.5">{errors.adminSecret}</p>}
            </div>

            <button type="submit" disabled={isLoading}
              className="btn-primary w-full mt-2 flex items-center justify-center gap-2 py-3"
              style={{ backgroundColor: 'var(--color-warning)', color: '#000' }}>
              {isLoading ? <><Spinner size="sm" /> Creating account...</> : 'Create Admin Account'}
            </button>
          </form>

          <p className="text-center text-xs text-lab-muted mt-6">
            Regular student?{' '}
            <Link to="/register" className="text-lab-primary hover:underline font-medium">Register here</Link>
            {' · '}
            <Link to="/login" className="text-lab-primary hover:underline font-medium">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
