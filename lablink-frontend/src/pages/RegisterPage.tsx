import { useState } from 'react';
import { Link } from 'react-router-dom';
import { FlaskConical, Mail, Lock, User, Eye, EyeOff, Hash } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { Spinner } from '@/components/ui';

interface FormErrors {
  fullName?: string;
  email?: string;
  idNumber?: string;
  password?: string;
  confirmPassword?: string;
}

export default function RegisterPage() {
  const { register, isLoading } = useAuth();
  const [form, setForm] = useState({
    fullName: '', email: '', idNumber: '', password: '', confirmPassword: '',
  });
  const [showPw, setShowPw]     = useState(false);
  const [errors, setErrors]     = useState<FormErrors>({});

  const set = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm(v => ({ ...v, [field]: e.target.value }));
    setErrors(v => ({ ...v, [field]: undefined }));
  };

  const validate = (): boolean => {
    const e: FormErrors = {};
    if (!form.fullName.trim())       e.fullName       = 'Full name is required';
    if (!form.email.trim())          e.email          = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) e.email = 'Enter a valid email address';
    if (form.password.length < 8)    e.password       = 'Password must be at least 8 characters';
    if (form.password !== form.confirmPassword) e.confirmPassword = 'Passwords do not match';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = (ev: React.FormEvent) => {
    ev.preventDefault();
    if (validate()) register(form);
  };

  const fields = [
    { key: 'fullName',        label: 'Full Name',        icon: User,    type: 'text',     placeholder: 'Juan Dela Cruz',     autoComplete: 'name' },
    { key: 'email',           label: 'University Email', icon: Mail,    type: 'email',    placeholder: 'student@cit.edu',    autoComplete: 'email' },
    { key: 'idNumber',        label: 'Student ID',       icon: Hash,    type: 'text',     placeholder: '21-XXXX-XXX',        autoComplete: 'off' },
  ] as const;

  return (
    <div className="min-h-screen bg-lab-bg flex items-center justify-center p-6 noise-bg">
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#232A3A_1px,transparent_1px),linear-gradient(to_bottom,#232A3A_1px,transparent_1px)]
                      bg-[size:48px_48px] opacity-30 pointer-events-none" />
      <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96
                      bg-lab-accent/6 rounded-full blur-3xl pointer-events-none" />

      <div className="relative w-full max-w-md animate-slide-up">
        <div className="flex flex-col items-center mb-10">
          <div className="flex items-center justify-center w-12 h-12 rounded-2xl
                          bg-lab-accent/15 border border-lab-accent/30 mb-4">
            <FlaskConical size={22} className="text-lab-accent" />
          </div>
          <h1 className="font-display font-bold text-2xl text-lab-text">Create account</h1>
          <p className="text-sm text-lab-muted mt-1">Join LabLink to borrow equipment</p>
        </div>

        <div className="card p-8">
          <form onSubmit={handleSubmit} className="space-y-4">
            {fields.map(({ key, label, icon: Icon, type, placeholder, autoComplete }) => (
              <div key={key}>
                <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                  {label}
                </label>
                <div className="relative">
                  <Icon size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                  <input
                    type={type}
                    autoComplete={autoComplete}
                    value={form[key]}
                    onChange={set(key)}
                    placeholder={placeholder}
                    className={`input-field pl-10 ${errors[key] ? 'border-lab-danger focus:ring-lab-danger/30' : ''}`}
                  />
                </div>
                {errors[key] && <p className="text-xs text-lab-danger mt-1.5">{errors[key]}</p>}
              </div>
            ))}

            {/* Password */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                Password
              </label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input
                  type={showPw ? 'text' : 'password'}
                  autoComplete="new-password"
                  value={form.password}
                  onChange={set('password')}
                  placeholder="Min. 8 characters"
                  className={`input-field pl-10 pr-10 ${errors.password ? 'border-lab-danger focus:ring-lab-danger/30' : ''}`}
                />
                <button type="button" onClick={() => setShowPw(v => !v)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text transition-colors" tabIndex={-1}>
                  {showPw ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-lab-danger mt-1.5">{errors.password}</p>}
            </div>

            {/* Confirm password */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                Confirm Password
              </label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input
                  type={showPw ? 'text' : 'password'}
                  autoComplete="new-password"
                  value={form.confirmPassword}
                  onChange={set('confirmPassword')}
                  placeholder="Re-enter password"
                  className={`input-field pl-10 ${errors.confirmPassword ? 'border-lab-danger focus:ring-lab-danger/30' : ''}`}
                />
              </div>
              {errors.confirmPassword && <p className="text-xs text-lab-danger mt-1.5">{errors.confirmPassword}</p>}
            </div>

            <button type="submit" disabled={isLoading}
              className="btn-primary w-full mt-2 flex items-center justify-center gap-2 py-3">
              {isLoading ? <><Spinner size="sm" /> Creating account...</> : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-xs text-lab-muted mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-lab-primary hover:text-blue-400 transition-colors font-medium">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
