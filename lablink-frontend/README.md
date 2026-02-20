# LabLink Frontend — React Web Dashboard

## What This Delivers

A complete **React 18 + TypeScript** web application scaffold for LabLink. All pages, components, routing, auth state, and API integration are wired — ready to connect to the Spring Boot backend.

---

## File Map

```
lablink-frontend/
├── index.html                          # Entry + Google Fonts (Syne, DM Sans, JetBrains Mono)
├── vite.config.ts                      # Vite + proxy to :8080
├── tailwind.config.js                  # Custom design tokens (lab-* colors)
├── src/
│   ├── main.tsx                        # React root
│   ├── App.tsx                         # All routes defined here
│   ├── index.css                       # Global styles, component classes, animations
│   ├── types/index.ts                  # All TypeScript types (mirrors SDD API contract)
│   ├── services/
│   │   ├── apiClient.ts                # Axios instance + JWT interceptors + token refresh
│   │   └── api.ts                      # authApi / equipmentApi / borrowApi functions
│   ├── store/
│   │   └── authStore.ts                # Zustand auth store (user, token, role)
│   ├── hooks/
│   │   └── useAuth.ts                  # register / login / logout with toasts
│   ├── components/
│   │   ├── layout/
│   │   │   ├── AppLayout.tsx           # Sidebar + main content shell
│   │   │   ├── Sidebar.tsx             # Nav links, user footer, logout
│   │   │   └── RouteGuards.tsx         # ProtectedRoute / AdminRoute / PublicRoute
│   │   ├── ui/index.tsx                # Spinner, Badges, EmptyState, Modal, ConfirmDialog
│   │   ├── equipment/
│   │   │   └── EquipmentCard.tsx       # Card with status badge + borrow button
│   │   └── borrow/
│   │       └── BorrowModal.tsx         # Borrow confirmation modal (Journey 10)
│   └── pages/
│       ├── LoginPage.tsx               # AC-4, AC-5
│       ├── RegisterPage.tsx            # AC-1, AC-2, AC-3
│       ├── CatalogPage.tsx             # AC-6, AC-7, AC-8 — search + filters + grid
│       ├── MyBorrowsPage.tsx           # Journey 12 — active borrows list
│       ├── AdminDashboardPage.tsx      # AC-10 — stats + verify return
│       └── AdminEquipmentPage.tsx      # AC-9 — CRUD table + add/edit modals
```

---

## Setup & Run

### Prerequisites
- Node.js 20+
- Spring Boot backend running on `:8080`

### Install
```bash
npm install
```

### Dev server
```bash
npm run dev       # Starts on http://localhost:3000
                  # API calls proxied to http://localhost:8080
```

### Build for production
```bash
npm run build     # Output in /dist — deploy to Vercel/Netlify
```

### Run tests
```bash
npm test
npm test -- --coverage
```

---

## Environment

Create `.env.local` to override the API base URL (optional — Vite proxy handles it in dev):

```env
VITE_API_URL=https://api.lablink.edu/api/v1
```

---

## Design System

### Fonts
| Role | Font | Usage |
|---|---|---|
| Display | Syne | Headings, labels, nav items, buttons |
| Body | DM Sans | Paragraphs, descriptions, form inputs |
| Mono | JetBrains Mono | Status badges, serial numbers, section labels |

### Color Tokens (`lab-*`)
| Token | Hex | Usage |
|---|---|---|
| `lab-bg` | `#0D0F14` | Page background |
| `lab-surface` | `#161B26` | Cards, sidebar |
| `lab-border` | `#232A3A` | Dividers, input borders |
| `lab-muted` | `#8A94A6` | Secondary text, icons |
| `lab-text` | `#E8ECF4` | Primary text |
| `lab-primary` | `#3B82F6` | Buttons, active states |
| `lab-accent` | `#06B6D4` | Category chips |
| `lab-success` | `#10B981` | Available status |
| `lab-warning` | `#F59E0B` | Overdue, maintenance |
| `lab-danger` | `#EF4444` | Unavailable, delete, errors |

### Utility classes (defined in `index.css`)
- `.card` — surface card with border
- `.btn-primary` / `.btn-ghost` / `.btn-danger` — button variants
- `.input-field` — styled form inputs
- `.badge-available` / `.badge-unavailable` / `.badge-maintenance` — status pills
- `.section-label` — mono uppercase section headers

---

## Auth Flow

```
Register/Login → POST /auth/register|login
  → JWT + refreshToken stored in localStorage (ll_access_token, ll_refresh_token)
  → Zustand authStore updated
  → Role-based redirect: ADMIN → /admin, STUDENT → /catalog

Token expired (401):
  → Axios interceptor auto-calls POST /auth/refresh
  → Retries original request
  → On refresh failure: clears storage + redirects to /login

Logout:
  → POST /auth/logout (best-effort)
  → localStorage cleared
  → Redirect to /login (replace: true — no back button)
```

⚠️ **localStorage vs httpOnly cookies**: The SDD specifies localStorage per Journey 7. For stricter security in production, switch to httpOnly cookies + CSRF tokens and update the `apiClient.ts` interceptor accordingly.

---

## Route Map

| Path | Guard | Component |
|---|---|---|
| `/login` | PublicRoute | LoginPage |
| `/register` | PublicRoute | RegisterPage |
| `/catalog` | ProtectedRoute | CatalogPage |
| `/my-items` | ProtectedRoute | MyBorrowsPage |
| `/admin` | AdminRoute | AdminDashboardPage |
| `/admin/equipment` | AdminRoute | AdminEquipmentPage |
| `/admin/borrows` | AdminRoute | AdminDashboardPage |
| `*` | — | Redirect → /catalog |

---

## What's Next

| Feature | Status | Notes |
|---|---|---|
| Equipment detail page `/catalog/:id` | Stub | `GET /equipment/{id}` is in `equipmentApi` |
| User profile page `/profile` | Stub | Link to `GET /borrow/my-items` |
| Pagination | Partial | API params wired; UI pagination component needed |
| Category management (Admin) | Not started | Needs `POST /categories` backend endpoint |
| Overdue highlighting | Done | `isPast()` check in AdminDashboardPage |
| Mobile responsiveness | Partial | Grid is responsive; sidebar needs drawer on mobile |
