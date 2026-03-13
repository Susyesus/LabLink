import { useState } from 'react';
import { Link } from 'react-router-dom';
import { FlaskConical, Mail, Lock, Eye, EyeOff, AlertCircle } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { Spinner } from '@/components/ui';
import { ThemeToggle } from '@/components/ui/ThemeToggle';

export default function LoginPage() {
  const { login, isLoading } = useAuth();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw]     = useState(false);

  const [fieldErrors, setFieldErrors] = useState<{ email?: string; password?: string }>({});
  const [apiError, setApiError]       = useState<string | null>(null);

  const validate = () => {
    const e: typeof fieldErrors = {};
    if (!email.trim())
      e.email = 'Email is required';
    else if (!/^[a-zA-Z0-9._%+-]+\.[a-zA-Z0-9._%+-]+@cit\.edu$/.test(email))
      e.email = 'Enter a valid CIT email (e.g. juan.dela@cit.edu)';
    if (!password.trim())
      e.password = 'Password is required';
    setFieldErrors(e);
    return !Object.keys(e).length;
  };

  const handleSubmit = async (ev: React.FormEvent) => {
    ev.preventDefault();
    setApiError(null);
    if (!validate()) return;

    const error = await login({ email, password });
    if (error) setApiError(error);
  };

  return (
    <div className="min-h-screen bg-lab-bg flex items-center justify-center p-6 noise-bg">
      {/* Background grid */}
      <div className="absolute inset-0 bg-[linear-gradient(to_right,var(--color-border)_1px,transparent_1px),linear-gradient(to_bottom,var(--color-border)_1px,transparent_1px)]
                      bg-[size:48px_48px] opacity-30 pointer-events-none" />
      {/* Glow orb */}
      <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-96 h-96
                      bg-lab-primary/8 rounded-full blur-3xl pointer-events-none" />

      {/* Theme toggle — top right */}
      <div className="fixed top-4 right-4 z-50">
        <ThemeToggle />
      </div>

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
          {/* API-level error banner */}
          {apiError && (
            <div className="flex items-start gap-2.5 bg-lab-danger/10 border border-lab-danger/30
                            text-lab-danger rounded-lg px-4 py-3 mb-5 text-sm">
              <AlertCircle size={15} className="mt-0.5 shrink-0" />
              <span>{apiError}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} noValidate className="space-y-5">
            {/* Email
                type="text" — prevents browser native email validation from interfering
                with our custom CIT regex and stops Chromium from resetting the form
                state on re-render after a failed submission.
                autoComplete="username" — tells the browser to use saved usernames
                (which includes full email addresses the user previously typed) rather
                than its own email-domain heuristics that default to .com suggestions. */}
            <div>
              <label className="block text-xs font-mono font-medium text-lab-muted mb-2 tracking-wider uppercase">
                University Email
              </label>
              <div className="relative">
                <Mail size={14} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-lab-muted" />
                <input
                  type="text"
                  autoComplete="username"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    setFieldErrors(v => ({ ...v, email: undefined }));
                    setApiError(null);
                  }}
                  placeholder="juan.dela@cit.edu"
                  className={`input-field pl-10 ${fieldErrors.email ? 'border-lab-danger' : ''}`}
                />
              </div>
              {fieldErrors.email && <p className="text-xs text-lab-danger mt-1.5">{fieldErrors.email}</p>}
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
                  onChange={(e) => {
                    setPassword(e.target.value);
                    setFieldErrors(v => ({ ...v, password: undefined }));
                    setApiError(null);
                  }}
                  placeholder="••••••••"
                  className={`input-field pl-10 pr-10 ${fieldErrors.password ? 'border-lab-danger' : ''}`}
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
              {fieldErrors.password && <p className="text-xs text-lab-danger mt-1.5">{fieldErrors.password}</p>}
            </div>

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
            <Link to="/register" className="text-lab-primary hover:underline font-medium">
              Create account
            </Link>
          </p>
        </div>

        <p className="text-center text-[11px] text-lab-muted/60 mt-4 font-mono">
          CIT University · Laboratory Asset Management
        </p>
      </div>
    </div>
  );
}
