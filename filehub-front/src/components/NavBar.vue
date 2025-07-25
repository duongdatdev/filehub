<template>
  <nav class="bg-white shadow-lg border-b border-gray-200 fixed top-0 left-0 right-0 z-50">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16">
        <!-- Left side - Logo and main nav -->
        <div class="flex items-center">
          <!-- Logo -->
          <router-link to="/" class="flex items-center">
            <div class="flex-shrink-0">
              <h1 class="text-2xl font-bold text-blue-600">FileHub</h1>
            </div>
          </router-link>

          <!-- Main Navigation -->
          <div class="hidden md:ml-8 md:flex md:space-x-8">
            <router-link
              to="/"
              class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200"
              :class="isActiveRoute('/') 
                ? 'border-blue-500 text-gray-900' 
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <FolderIcon class="w-4 h-4 mr-2" />
              Dashboard
            </router-link>

            <router-link
              to="/files"
              class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200"
              :class="isActiveRoute('/files') 
                ? 'border-blue-500 text-gray-900' 
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <DocumentIcon class="w-4 h-4 mr-2" />
              My Files
            </router-link>

            <router-link
              to="/about"
              class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200"
              :class="isActiveRoute('/about') 
                ? 'border-blue-500 text-gray-900' 
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
            >
              <InfoIcon class="w-4 h-4 mr-2" />
              About
            </router-link>

            <!-- Admin Navigation -->
            <div v-if="isAdmin" class="relative">
              <button
                @click="showAdminDropdown = !showAdminDropdown"
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200"
                :class="isActiveRoute('/admin') 
                  ? 'border-blue-500 text-gray-900' 
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
              >
                <UserIcon class="w-4 h-4 mr-2" />
                Admin Panel
                <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
              
              <!-- Admin Dropdown -->
              <div
                v-if="showAdminDropdown"
                class="absolute left-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 border border-gray-200"
                @click="showAdminDropdown = false"
              >
                <router-link
                  to="/admin"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-200"
                >
                  <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                  </svg>
                  Dashboard
                </router-link>
                <router-link
                  to="/admin/users"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-200"
                >
                  <UserIcon class="w-4 h-4 inline mr-2" />
                  Manage Users
                </router-link>
                <router-link
                  to="/admin/departments"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-200"
                >
                  <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.75 21h16.5M4.5 3h15M5.25 3v18m13.5-18v18M9 6.75h1.5m-1.5 3h1.5m-1.5 3h1.5m3-6H15m-1.5 3H15m-1.5 3H15M9 21v-3.375c0-.621.504-1.125 1.125-1.125h3.75c.621 0 1.125.504 1.125 1.125V21" />
                  </svg>
                  Departments
                </router-link>
                <router-link
                  to="/admin/projects"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors duration-200"
                >
                  <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 713.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
                  </svg>
                  Projects
                </router-link>
              </div>
            </div>
          </div>
        </div>

        <!-- Right side - User menu and bulk actions -->
        <div class="flex items-center space-x-4">
          <!-- Bulk Actions (shown on admin pages) -->
          <div v-if="showBulkActions" class="hidden md:flex items-center space-x-2">
            <div class="flex items-center space-x-2 px-3 py-1 bg-gray-100 rounded-md">
              <input
                id="selectAll"
                type="checkbox"
                :checked="allSelected"
                :indeterminate="someSelected && !allSelected"
                @change="toggleSelectAll"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label for="selectAll" class="text-sm text-gray-700">
                {{ selectedCount > 0 ? `${selectedCount} selected` : 'Select All' }}
              </label>
            </div>
            
            <div v-if="selectedCount > 0" class="flex items-center space-x-2">
              <button
                @click="bulkActivate"
                class="px-3 py-1 text-xs font-medium text-green-700 bg-green-100 rounded hover:bg-green-200 transition-colors"
              >
                Activate ({{ selectedCount }})
              </button>
              <button
                @click="bulkDeactivate"
                class="px-3 py-1 text-xs font-medium text-red-700 bg-red-100 rounded hover:bg-red-200 transition-colors"
              >
                Deactivate ({{ selectedCount }})
              </button>
            </div>
          </div>

          <!-- User Menu -->
          <div class="relative" ref="userMenuRef">
            <button
              @click="toggleUserMenu"
              class="flex items-center text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                <span class="text-sm font-medium text-blue-600">
                  {{ userInitials }}
                </span>
              </div>
              <span class="hidden md:ml-2 md:block text-sm font-medium text-gray-700">
                {{ user?.username }}
              </span>
              <svg class="hidden md:ml-1 md:block h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>

            <!-- Dropdown Menu -->
            <div
              v-if="showUserMenu"
              class="absolute right-0 mt-2 w-48 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50"
            >
              <div class="py-1">
                <div class="px-4 py-2 border-b border-gray-100">
                  <p class="text-sm font-medium text-gray-900">{{ user?.fullName }}</p>
                  <p class="text-sm text-gray-500">{{ user?.email }}</p>
                  <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 mt-1">
                    {{ user?.role }}
                  </span>
                </div>
                <router-link
                  to="/profile"
                  class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                  @click="closeUserMenu"
                >
                  Profile Settings
                </router-link>
                <button
                  @click="handleLogout"
                  class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Sign out
                </button>
              </div>
            </div>
          </div>

          <!-- Mobile menu button -->
          <div class="md:hidden">
            <button
              @click="toggleMobileMenu"
              type="button"
              class="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500"
            >
              <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="!showMobileMenu" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- Mobile menu -->
      <div v-if="showMobileMenu" class="md:hidden">
        <div class="pt-2 pb-3 space-y-1 border-t border-gray-200">
          <router-link
            to="/"
            class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
            :class="isActiveRoute('/') 
              ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
              : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
            @click="closeMobileMenu"
          >
            Dashboard
          </router-link>
          <router-link
            to="/about"
            class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
            :class="isActiveRoute('/about') 
              ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
              : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
            @click="closeMobileMenu"
          >
            About
          </router-link>
          <!-- Admin Mobile Menu -->
          <div v-if="isAdmin" class="pl-3">
            <div class="text-sm font-medium text-gray-500 uppercase tracking-wider mb-2">Admin</div>
            <router-link
              to="/admin"
              class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
              :class="isActiveRoute('/admin') && route.path === '/admin'
                ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
                : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
              @click="closeMobileMenu"
            >
              Dashboard
            </router-link>
            <router-link
              to="/admin/users"
              class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
              :class="isActiveRoute('/admin/users')
                ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
                : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
              @click="closeMobileMenu"
            >
              Manage Users
            </router-link>
            <router-link
              to="/admin/departments"
              class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
              :class="isActiveRoute('/admin/departments')
                ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
                : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
              @click="closeMobileMenu"
            >
              Departments
            </router-link>
            <router-link
              to="/admin/projects"
              class="block pl-3 pr-4 py-2 text-base font-medium transition-colors duration-200"
              :class="isActiveRoute('/admin/projects')
                ? 'border-l-4 border-blue-500 text-blue-700 bg-blue-50' 
                : 'border-l-4 border-transparent text-gray-600 hover:text-gray-800 hover:bg-gray-50'"
              @click="closeMobileMenu"
            >
              Projects
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAdminStore } from '@/stores/admin'
import FolderIcon from '@/components/icons/FolderIcon.vue'
import DocumentIcon from '@/components/icons/DocumentIcon.vue'
import InfoIcon from '@/components/icons/InfoIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const adminStore = useAdminStore()

// State
const showUserMenu = ref(false)
const showMobileMenu = ref(false)
const showAdminDropdown = ref(false)
const userMenuRef = ref<HTMLElement>()

// Computed
const user = computed(() => authStore.user)
const isAdmin = computed(() => authStore.isAdmin)
const userInitials = computed(() => {
  if (!user.value?.username) return 'U'
  return user.value.username.charAt(0).toUpperCase()
})

// Navigation helpers
const isActiveRoute = (path: string): boolean => {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

// Bulk actions (for admin pages)
const showBulkActions = computed(() => {
  return isAdmin.value && route.path.startsWith('/admin')
})

// Selection state from admin store
const selectedCount = computed(() => adminStore.selectedCount)
const allSelected = computed(() => adminStore.allCurrentPageSelected)
const someSelected = computed(() => adminStore.someCurrentPageSelected)

// Methods
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
  showMobileMenu.value = false
}

const closeUserMenu = () => {
  showUserMenu.value = false
}

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
  showUserMenu.value = false
}

const closeMobileMenu = () => {
  showMobileMenu.value = false
}

const handleLogout = async () => {
  await authStore.logout()
  router.push('/login')
  closeUserMenu()
}

// Bulk actions methods
const toggleSelectAll = () => {
  adminStore.toggleSelectAllCurrentPage()
}

const bulkActivate = async () => {
  await adminStore.bulkUpdateUserStatus(true)
}

const bulkDeactivate = async () => {
  await adminStore.bulkUpdateUserStatus(false)
}

// Click outside to close menus
const handleClickOutside = (event: Event) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target as Node)) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// Watch route changes to close mobile menu
router.afterEach(() => {
  showMobileMenu.value = false
  showUserMenu.value = false
})
</script>

<style scoped>
/* Custom styles for indeterminate checkbox */
input[type="checkbox"]:indeterminate {
  background-image: url("data:image/svg+xml;charset=utf-8,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='%23fff' viewBox='0 0 16 16'%3E%3Cpath d='M5 8h6'/%3E%3C/svg%3E");
  background-color: #3b82f6;
  border-color: #3b82f6;
}
</style>