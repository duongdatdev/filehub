# FileHub Frontend - Admin User Management

This document describes the frontend implementation for the Admin User Management system in FileHub.

## 🏗️ Architecture Overview

The admin frontend is built using Vue 3 with TypeScript and follows a modular architecture:

### Tech Stack
- **Vue 3** - Progressive JavaScript framework
- **TypeScript** - Type-safe JavaScript
- **Pinia** - State management
- **Vue Router** - Client-side routing
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client for API calls

### Folder Structure
```
src/
├── components/           # Reusable Vue components
│   ├── AdminUsersFilters.vue   # Search and filter component
│   ├── AdminUsersTable.vue     # Users data table
│   ├── AdminPagination.vue     # Pagination controls
│   ├── SortIcon.vue            # Sort indicator component
│   └── NavBar.vue             # Navigation (updated with admin link)
├── views/               # Page components
│   └── AdminUsersPage.vue     # Main admin users page
├── stores/              # Pinia stores
│   ├── admin.ts              # Admin state management
│   └── auth.ts               # Authentication state
├── services/            # API service layers
│   ├── api.ts               # Base API service
│   └── adminApi.ts          # Admin-specific API calls
└── router/              # Vue Router configuration
    └── index.ts             # Routes and navigation guards
```

## 🚀 Features Implemented

### 1. User Management Dashboard
- **Location**: `/admin/users`
- **Access**: Admin users only
- **Features**:
  - View all users in a paginated table
  - Real-time search and filtering
  - Sort by multiple columns
  - Activate/deactivate user accounts

### 2. Advanced Filtering System
**Available Filters:**
- Username (text search)
- Email (text search)
- Full Name (text search)
- Role (USER/ADMIN dropdown)
- Status (Active/Inactive dropdown)
- Page size selection (5, 10, 20, 50)

**Filter Features:**
- Real-time filter count display
- Clear all filters button
- Persistent filter state during navigation
- Auto-reset to first page when filters change

### 3. Interactive Data Table
**Table Features:**
- Sortable columns (username, email, role, status, created date)
- Visual sort indicators
- User avatars with initials
- Status badges with color coding
- In-line status toggle buttons
- Loading states for async operations

**Sortable Columns:**
- ID
- Username
- Email
- Full Name
- Role
- Status (Active/Inactive)
- Created Date

### 4. Pagination System
**Features:**
- Page navigation with Previous/Next buttons
- Direct page number clicking
- Smart page number display (shows ellipsis for large page counts)
- Items count display ("Showing X to Y of Z results")
- Mobile-responsive design

### 5. User Status Management
**Actions Available:**
- Activate inactive users
- Deactivate active users
- Real-time UI updates
- Loading indicators during status changes
- Error handling with user feedback

## 🔐 Security Implementation

### Authentication & Authorization
- **Route Guards**: Protect admin routes from unauthorized access
- **Role-Based Access**: Only users with 'ADMIN' role can access admin features
- **JWT Integration**: Automatic token attachment to API requests
- **Auto-redirect**: Non-admin users redirected from admin routes

### API Security
- **Bearer Token Authentication**: All admin API calls include JWT token
- **Error Handling**: Proper handling of 401/403 responses
- **CORS Support**: Configured for cross-origin requests

## 📡 API Integration

### Admin API Service (`adminApi.ts`)
```typescript
// Get users with filtering and pagination
getUsers(filters?: AdminUserFilterRequest): Promise<ApiResponse<PageResponse<User>>>

// Get single user by ID
getUserById(id: number): Promise<ApiResponse<User>>

// Update user active status
updateUserStatus(id: number, isActive: boolean): Promise<ApiResponse<User>>

// Get user statistics (optional)
getUserStats(): Promise<ApiResponse<UserStats>>
```

### Request/Response Types
```typescript
// Filter request interface
interface AdminUserFilterRequest {
  username?: string
  email?: string
  fullName?: string
  role?: string
  isActive?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

// Paginated response interface
interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
}
```

## 🎨 UI/UX Features

### Responsive Design
- **Mobile-First**: Optimized for mobile devices
- **Responsive Table**: Horizontal scroll on small screens
- **Adaptive Pagination**: Different layouts for mobile/desktop
- **Touch-Friendly**: Large touch targets for mobile

### User Experience
- **Loading States**: Visual feedback during data loading
- **Error Handling**: User-friendly error messages
- **Success Feedback**: Visual confirmation of successful actions
- **Empty States**: Helpful messages when no data is available

### Design System
- **Consistent Colors**: Blue primary, semantic colors for status
- **Typography**: Clear hierarchy with proper font weights
- **Spacing**: Consistent spacing using Tailwind's spacing scale
- **Icons**: SVG icons for actions and status indicators

## 🚦 State Management

### Admin Store (`admin.ts`)
**State:**
- `users`: Array of user objects
- `totalElements`: Total number of users
- `totalPages`: Total number of pages
- `currentPage`: Current page number (0-based)
- `pageSize`: Number of items per page
- `loading`: Loading state boolean
- `error`: Error message string
- `filters`: Current filter values

**Actions:**
- `fetchUsers()`: Load users with current filters
- `getUserById()`: Get single user details
- `updateUserStatus()`: Toggle user active status
- `setPage()`: Change current page
- `setPageSize()`: Change items per page
- `setSorting()`: Update sort field and direction
- `applyFilters()`: Apply new filter values
- `clearFilters()`: Reset all filters

**Getters:**
- `hasUsers`: Boolean if users array has items
- `isFirstPage`: Boolean if on first page
- `isLastPage`: Boolean if on last page
- `activeFiltersCount`: Number of active filters

## 🛠️ Development Setup

### Prerequisites
- Node.js 16+ and npm/yarn
- Backend API running on localhost:8080

### Installation
```bash
cd filehub-front
npm install
```

### Development Server
```bash
npm run dev
```

### Environment Configuration
Create `.env` file:
```
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=FileHub
VITE_APP_VERSION=1.0.0
```

### Build for Production
```bash
npm run build
```

## 🔄 API Endpoints Used

The frontend integrates with these backend endpoints:

### User Management
- `GET /api/admin/users` - Get paginated users list with filters
- `GET /api/admin/users/{id}` - Get single user details  
- `PATCH /api/admin/users/{id}/status` - Update user active status

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

## 🐛 Error Handling

### Error Types Handled
- **Network Errors**: Connection issues, timeouts
- **Authentication Errors**: Invalid tokens, expired sessions
- **Authorization Errors**: Insufficient permissions
- **Validation Errors**: Invalid input data
- **Server Errors**: Backend API errors

### Error Display
- **Toast Notifications**: Auto-dismissing error messages
- **Inline Errors**: Form validation errors
- **Loading States**: Prevent multiple submissions
- **Retry Options**: Allow users to retry failed operations

## 🔮 Future Enhancements

### Potential Features
1. **User Creation**: Add new user form
2. **User Editing**: Edit user details
3. **Bulk Operations**: Select multiple users for batch actions
4. **Advanced Permissions**: Granular permission management
5. **Activity Logs**: User action history
6. **Export Features**: CSV/Excel export of user data
7. **User Analytics**: Dashboard with user statistics
8. **Profile Management**: Edit user profiles and roles

### Technical Improvements
1. **Caching**: Implement response caching for better performance
2. **Offline Support**: PWA features for offline functionality
3. **Real-time Updates**: WebSocket integration for live data
4. **Advanced Search**: Full-text search with highlighting
5. **Infinite Scroll**: Alternative to pagination for large datasets

## 📝 Component Documentation

### AdminUsersPage.vue
Main container component that orchestrates the admin user management interface.

**Features:**
- Route guard for admin access
- Error toast notifications
- Refresh functionality
- Responsive layout

### AdminUsersFilters.vue
Advanced filtering component with multiple search criteria.

**Props:** None (uses admin store directly)
**Features:**
- Real-time filter application
- Filter count display
- Clear all functionality
- Form validation

### AdminUsersTable.vue
Interactive data table with sorting and user actions.

**Props:** None (uses admin store directly)
**Features:**
- Sortable columns
- Status toggle buttons
- Loading states
- Empty state handling

### AdminPagination.vue
Sophisticated pagination with smart page number display.

**Props:** None (uses admin store directly)
**Features:**
- Mobile-responsive design
- Smart page number truncation
- Direct page navigation
- Results count display

This implementation provides a complete, production-ready admin interface for user management with modern UX patterns and robust error handling.
