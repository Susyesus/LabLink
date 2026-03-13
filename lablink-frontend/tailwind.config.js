/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  // 'class' strategy — we toggle .light / .dark on <html>
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        display: ['Syne', 'sans-serif'],
        body:    ['DM Sans', 'sans-serif'],
        mono:    ['JetBrains Mono', 'monospace'],
      },
      colors: {
        // Map Tailwind classes to CSS variables so dark/light switching works.
        lab: {
          bg:      'var(--color-bg)',
          surface: 'var(--color-surface)',
          border:  'var(--color-border)',
          muted:   'var(--color-muted)',
          text:    'var(--color-text)',
          primary: 'var(--color-primary)',
          accent:  'var(--color-accent)',
          success: 'var(--color-success)',
          warning: 'var(--color-warning)',
          danger:  'var(--color-danger)',
        },
      },
    },
  },
  plugins: [],
};
