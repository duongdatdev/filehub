# Testing the Admin Frontend

This document provides instructions for testing the newly implemented admin frontend functionality.

## 🚀 Quick Start

### 1. Start the Backend Server
```bash
cd filehub-back
./gradlew bootRun
```
The backend should start on `http://localhost:8080`

### 2. Start the Frontend Development Server
```bash
cd filehub-front
npm install  # if not already installed
npm run dev
```
The frontend should start on `http://localhost:5173`

## 🧪 Testing Scenarios

### Scenario 1: Admin User Access
1. **Login as Admin**:
   - Navigate to `http://localhost:5173/login`
   - Use admin credentials from your database
   - After login, verify the "Admin" link appears in the navigation

2. **Access Admin Dashboard**:
   - Click the "Admin" link in the navigation
   - Should navigate to `/admin/users`
   - Verify the page loads with user management interface

### Scenario 2: User Filtering and Search
1. **Username Search**:
   - Enter a username in the "Username" filter field
   - Click "Apply Filters"
   - Verify the table shows only matching users

2. **Role Filter**:
   - Select "ADMIN" from the Role dropdown
   - Apply filters and verify only admin users are shown
   - Clear filters and verify all users return

3. **Status Filter**:
   - Select "Active" from the Status dropdown
   - Verify only active users are displayed
   - Switch to "Inactive" and verify results

### Scenario 3: Sorting Functionality
1. **Sort by Username**:
   - Click the "Username" column header
   - Verify sort icon appears and users are sorted alphabetically
   - Click again to reverse sort order

2. **Sort by Created Date**:
   - Click "Created" column header
   - Verify users are sorted by creation date
   - Toggle between ascending and descending

### Scenario 4: Pagination
1. **Page Navigation**:
   - Change page size to 5 items
   - Verify pagination controls appear
   - Navigate between pages using page numbers
   - Test Previous/Next buttons

2. **Items Count**:
   - Verify "Showing X to Y of Z results" displays correctly
   - Change page size and verify count updates

### Scenario 5: User Status Management
1. **Activate/Deactivate Users**:
   - Find an active user and click "Deactivate"
   - Verify the button shows loading state
   - After completion, verify status badge changes to "Inactive"
   - Repeat for inactive users with "Activate" button

### Scenario 6: Non-Admin User Access
1. **Login as Regular User**:
   - Logout from admin account
   - Login with a regular user account (role: USER)
   - Verify "Admin" link does NOT appear in navigation

2. **Direct URL Access**:
   - Try to navigate directly to `/admin/users`
   - Should be redirected to home page (non-admin users blocked)

## 🔍 API Testing with Browser DevTools

### 1. Network Tab Verification
Open browser DevTools (F12) and check Network tab:

**Expected API Calls:**
- `GET /api/admin/users?page=0&size=10&sortBy=createdAt&sortDir=desc`
- `PATCH /api/admin/users/{id}/status` (when toggling status)

**Request Headers Should Include:**
- `Authorization: Bearer <token>`
- `Content-Type: application/json`

### 2. Console Error Checking
Monitor the Console tab for:
- No JavaScript errors
- Proper API response logging
- Proper error handling messages

## 📱 Responsive Design Testing

### Desktop (1920x1080)
- All columns visible in table
- Filters displayed in 3-column grid
- Full pagination with page numbers

### Tablet (768x1024)
- Table might scroll horizontally
- Filters in 2-column grid
- Pagination adapts to smaller space

### Mobile (375x667)
- Horizontal table scroll
- Filters stack vertically
- Simple Previous/Next pagination

## 🚨 Error Scenarios to Test

### 1. Backend Offline
- Stop the backend server
- Try to load admin page
- Verify error message displays
- Verify retry functionality works when backend returns

### 2. Invalid Token
- Manually clear localStorage token
- Try to access admin page
- Should be redirected to login

### 3. Network Timeout
- Use browser DevTools to simulate slow network
- Verify loading states display properly
- Verify timeout error handling

## 📊 Performance Testing

### Load Testing
1. **Large Dataset**:
   - Create many test users in database
   - Test pagination performance
   - Verify filter performance with large result sets

2. **Concurrent Users**:
   - Open multiple browser tabs
   - Perform actions simultaneously
   - Verify state consistency

## ✅ Expected Results Checklist

### UI/UX
- [ ] Clean, professional interface
- [ ] Consistent styling with Tailwind CSS
- [ ] Responsive design works on all screen sizes
- [ ] Loading states provide clear feedback
- [ ] Error messages are user-friendly

### Functionality
- [ ] All filters work correctly
- [ ] Sorting works for all columns
- [ ] Pagination navigates correctly
- [ ] Status toggle updates immediately
- [ ] Admin-only access enforced

### Performance
- [ ] Page loads quickly (<2 seconds)
- [ ] API calls complete in reasonable time
- [ ] No memory leaks or console errors
- [ ] Smooth interactions with no lag

### Security
- [ ] Non-admin users cannot access admin routes
- [ ] JWT tokens sent with all API requests
- [ ] Proper error handling for auth failures
- [ ] No sensitive data exposed in console

## 🐛 Common Issues and Solutions

### Issue: Admin link not showing
**Solution**: Verify user role is exactly "ADMIN" (case-sensitive)

### Issue: 401 Unauthorized errors
**Solution**: Check JWT token in localStorage, re-login if expired

### Issue: Table not loading
**Solution**: Verify backend is running and CORS is configured

### Issue: Filters not working
**Solution**: Check browser console for API errors

### Issue: Pagination broken
**Solution**: Verify API returns proper PageResponse structure

## 📝 Test Data Setup

### Sample Admin User
```sql
INSERT INTO users (username, email, password, full_name, role, is_active, created_at, updated_at)
VALUES ('admin', 'admin@filehub.com', '$2a$10$encoded_password', 'System Administrator', 'ADMIN', true, NOW(), NOW());
```

### Sample Regular Users
```sql
INSERT INTO users (username, email, password, full_name, role, is_active, created_at, updated_at)
VALUES 
  ('john_doe', 'john@example.com', '$2a$10$encoded_password', 'John Doe', 'USER', true, NOW(), NOW()),
  ('jane_smith', 'jane@example.com', '$2a$10$encoded_password', 'Jane Smith', 'USER', false, NOW(), NOW()),
  ('bob_wilson', 'bob@example.com', '$2a$10$encoded_password', 'Bob Wilson', 'USER', true, NOW(), NOW());
```

This testing guide ensures comprehensive validation of the admin frontend functionality.
