import { useState } from 'react';
import { Link } from 'react-router-dom';
import { FlaskConical, Mail, Lock, Eye, EyeOff } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { Spinner } from '@/components/ui';

export default function LoginPage() {
  const { login, isLoading } = useAuth();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw]     = useState(false);
  const [errors, setErrors]     = useState<{ email?: string; password?: string }>({});

  const validate = () => {
    const e: typeof errors = {};
    if (!email.trim())    e.email    = 'Email is required';
    if (!password.trim()) e.password = 'Password is required';
    setErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = (ev: React.FormEvent) => {
    ev.preventDefault();
    if (validate()) login({ email, password });
  };

  return (
    <div className="min-h-screen bg-lab-bg flex items-center justify-center p-6 noise-bg">
      {/* Background grid */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,#232A3A_1px,transparent_1px),linear-gradient(to_bottom,#232A3A_1px,transparent_1px)]
                      bg-[size:48px_48px] opacity-30 pointer-events-none" />

      {/* Glow orb */}
      <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96
                      bg-lab-primary/8 rounded-full blur-3xl pointer-events-none" />

      <div className="relative w-full max-w-md animate-slide-up">
        {/* Logo */}
        <div className="flex flex-col items-center mb-10">
          <div className="flex items-center justify-center w-12 h-12 rounded-2xl
                          bg-lab-primary/15 border border-lab-primary/30 mb-4">
            <FlaskConical size={22} className="text-lab-primary" />
          </div>
          <h1 className="font-display font-bold text-2xl text-lab-text">Welcome back</h1>
          <p className="text-sm text-lab-muted mt-1">Sign in to LabLink</p>
        </div>

        {/* Card */}
        <div className="card p-8">
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Email */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                Email Address
              </label>
              <div className="relative">
                <Mail size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={(e) => { setEmail(e.target.value); setErrors(v => ({ ...v, email: undefined })); }}
                  placeholder="student@cit.edu"
                  className={`input-field pl-10 ${errors.email ? 'border-lab-danger focus:ring-lab-danger/30' : ''}`}
                />
              </div>
              {errors.email && <p className="text-xs text-lab-danger mt-1.5">{errors.email}</p>}
            </div>

            {/* Password */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                Password
              </label>
              <div className="relative">
                <Lock size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input
                  type={showPw ? 'text' : 'password'}
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => { setPassword(e.target.value); setErrors(v => ({ ...v, password: undefined })); }}
                  placeholder="••••••••"
                  className={`input-field pl-10 pr-10 ${errors.password ? 'border-lab-danger focus:ring-lab-danger/30' : ''}`}
                />
                <button
                  type="button"
                  onClick={() => setShowPw(v => !v)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-lab-muted hover:text-lab-text transition-colors"
                  tabIndex={-1}
                >
                  {showPw ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
              </div>
              {errors.password && <p className="text-xs text-lab-danger mt-1.5">{errors.password}</p>}
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={isLoading}
              className="btn-primary w-full mt-2 flex items-center justify-center gap-2 py-3"
            >
              {isLoading ? <><Spinner size="sm" /> Signing in...</> : 'Sign In'}
            </button>
          </form>

          <p className="text-center text-xs text-lab-muted mt-6">
            Don&apos;t have an account?{' '}
            <Link to="/register" className="text-lab-primary hover:text-blue-400 transition-colors font-medium">
              Create account
            </Link>
          </p>
        </div>

        {/* Demo hint */}
        <p className="text-center text-[11px] text-lab-muted/60 mt-4 font-mono">
          CIT University · Laboratory Asset Management
        </p>
      </div>
    </div>
  );
}
