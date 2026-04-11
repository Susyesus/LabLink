import { create } from 'zustand';

type Theme = 'dark' | 'light';

interface ThemeState {
  theme: Theme;
  toggleTheme: () => void;
  setTheme: (t: Theme) => void;
}

function applyTheme(theme: Theme) {
  const root = document.documentElement;
  if (theme === 'light') {
    root.classList.add('light');
    root.classList.remove('dark');
  } else {
    root.classList.add('dark');
    root.classList.remove('light');
  }
}

export const useThemeStore = create<ThemeState>((set) => {
  const saved = (localStorage.getItem('ll_theme') as Theme | null) ?? 'dark';
  applyTheme(saved);
  return {
    theme: saved,
    toggleTheme: () =>
      set((state) => {
        const next: Theme = state.theme === 'dark' ? 'light' : 'dark';
        localStorage.setItem('ll_theme', next);
        applyTheme(next);
        return { theme: next };
      }),
    setTheme: (t) => {
      localStorage.setItem('ll_theme', t);
      applyTheme(t);
      set({ theme: t });
    },
  };
});
