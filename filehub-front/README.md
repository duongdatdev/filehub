# FileHub Frontend

Ứng dụng Vue 3 frontend với TypeScript, được xây dựng với các công nghệ hiện đại.

## 🛠️ Công Nghệ Sử Dụng

- **Vue 3** - Progressive JavaScript Framework
- **TypeScript** - Strongly typed programming language
- **Vite** - Fast build tool and dev server
- **Vue Router 4** - Official router for Vue.js
- **Pinia** - State management library
- **Axios** - HTTP client for API calls
- **TailwindCSS** - Utility-first CSS framework
- **PostCSS & Autoprefixer** - CSS processing tools

## 📁 Cấu Trúc Project

```
src/
├── components/          # Các component tái sử dụng
│   ├── HelloWorld.vue
│   └── NavBar.vue
├── views/              # Các trang chính
│   ├── HomePage.vue
│   └── AboutPage.vue
├── stores/             # Pinia stores
│   ├── index.ts
│   └── counter.ts
├── router/             # Vue Router config
│   └── index.ts
├── services/           # API services
│   └── api.ts
├── assets/             # Static assets
└── style.css          # Global styles với Tailwind
```

## 🚀 Cài Đặt và Chạy

### Cài đặt dependencies
```bash
npm install
```

### Chạy development server
```bash
npm run dev
```

### Build cho production
```bash
npm run build
```

### Preview build
```bash
npm run preview
```

## 📋 Tính Năng Có Sẵn

### ✅ Vue Router
- Routing giữa các trang
- Navigation guards sẵn sàng
- Lazy loading routes

### ✅ Pinia State Management
- Store example với counter
- Composition API setup
- TypeScript support

### ✅ Axios Configuration
- HTTP client đã cấu hình
- Request/Response interceptors
- Error handling
- Token authentication ready

### ✅ TailwindCSS
- Utility-first CSS
- Responsive design
- Dark mode ready
- Custom configurations

### ✅ TypeScript
- Type safety
- IntelliSense support
- Path aliases (@/* → src/*)

## 🔧 Cấu Hình Chính

### Path Aliases
- `@/` maps to `src/`
- Configured in `vite.config.ts` và `tsconfig.app.json`

### API Service
```typescript
import { apiService } from '@/services/api'

// GET request
const data = await apiService.get('/endpoint')

// POST request
const result = await apiService.post('/endpoint', payload)
```

### State Management
```typescript
import { useCounterStore } from '@/stores/counter'

const store = useCounterStore()
store.increment()
```

## 🎨 UI Components

Ứng dụng sử dụng TailwindCSS với:
- Responsive grid layouts
- Modern card designs
- Button hover effects
- Gradient backgrounds
- Clean navigation

## 🚀 Phát Triển Tiếp

Để mở rộng ứng dụng:

1. **Thêm trang mới**: Tạo component trong `src/views/` và thêm route
2. **State management**: Tạo store mới trong `src/stores/`
3. **API integration**: Sử dụng `apiService` trong `src/services/api.ts`
4. **Components**: Tạo reusable components trong `src/components/`
5. **Styling**: Sử dụng TailwindCSS classes hoặc thêm custom CSS

## 📝 Scripts

- `npm run dev` - Chạy development server
- `npm run build` - Build production
- `npm run preview` - Preview production build
- `npm run type-check` - Type checking

---

Được tạo với ❤️ sử dụng Vue 3 + TypeScript + Vite
