import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import HomePage from '@/views/HomePage.vue'
import AboutPage from '@/views/AboutPage.vue'
import LoginPage from '@/views/LoginPage.vue'
import RegisterPage from '@/views/RegisterPage.vue'
import AdminUsersPage from '@/views/AdminUsersPage.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import AdminDepartments from '@/views/AdminDepartments.vue'
import AdminProjects from '@/views/AdminProjects.vue'
import AdminAssignmentsPage from '@/views/AdminAssignmentsPage.vue'
import FilesPage from '@/views/FilesPage.vue'
import SharedFilesPage from '@/views/SharedFilesPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/files',
    name: 'Files',
    component: FilesPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/files/shared',
    name: 'SharedFiles',
    component: SharedFilesPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: { requiresGuest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterPage,
    meta: { requiresGuest: true }
  },
  {
    path: '/about',
    name: 'About',
    component: AboutPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: AdminUsersPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/departments',
    name: 'AdminDepartments',
    component: AdminDepartments,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/projects',
    name: 'AdminProjects',
    component: AdminProjects,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/assignments',
    name: 'AdminAssignments',
    component: AdminAssignmentsPage,
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

export const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guards
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()

  // Initialize auth on first route
  if (!authStore.user && !authStore.token) {
    authStore.initializeAuth()
  }

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/')
  } else if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    // Redirect non-admin users trying to access admin routes
    next('/')
  } else {
    next()
  }
})

export default router
