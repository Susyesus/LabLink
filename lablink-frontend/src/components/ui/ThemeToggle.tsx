import { Sun, Moon } from 'lucide-react';
import { useThemeStore } from '@/store/themeStore';

interface Props {
  className?: string;
}

export function ThemeToggle({ className = '' }: Props) {
  const { theme, toggleTheme } = useThemeStore();

  return (
    <button
      onClick={toggleTheme}
      aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`}
      className={`
        flex items-center justify-center w-9 h-9 rounded-lg
        border border-[var(--color-border)] bg-[var(--color-surface)]
        text-[var(--color-muted)] hover:text-[var(--color-text)]
        hover:bg-[var(--color-border)]
        transition-all duration-200 focus:outline-none focus:ring-2
        focus:ring-[var(--color-primary)]/40
        ${className}
      `}
    >
      {theme === 'dark'
        ? <Sun size={16} />
        : <Moon size={16} />
      }
    </button>
  );
}
