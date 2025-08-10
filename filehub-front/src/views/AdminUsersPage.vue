<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
    <!-- Enhanced Header with Breadcrumb -->
    <div class="bg-white/80 backdrop-blur-sm border-b border-gray-200 shadow-sm sticky top-0 z-40">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Breadcrumb -->
        <nav class="flex pt-4 pb-2" aria-label="Breadcrumb">
          <ol role="list" class="flex items-center space-x-2">
            <li>
              <router-link 
                to="/admin" 
                class="group text-gray-400 hover:text-emerald-600 transition-all duration-200 flex items-center"
              >
                <div class="p-1 rounded-md group-hover:bg-emerald-50 transition-colors duration-200">
                  <svg class="flex-shrink-0 h-4 w-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M9.293 2.293a1 1 0 011.414 0l7 7A1 1 0 0117 11h-1v6a1 1 0 01-1 1h-2a1 1 0 01-1-1v-3a1 1 0 00-1-1H9a1 1 0 00-1 1v3a1 1 0 01-1 1H5a1 1 0 01-1-1v-6H3a1 1 0 01-.707-1.707l7-7z" clip-rule="evenodd" />
                  </svg>
                </div>
                <span class="sr-only">Admin Dashboard</span>
              </router-link>
            </li>
            <li>
              <div class="flex items-center">
                <svg class="flex-shrink-0 h-4 w-4 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                </svg>
                <span class="ml-2 text-sm font-medium text-gray-900">User Management</span>
              </div>
            </li>
          </ol>
        </nav>

        <!-- Header Content -->
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between py-6">
          <div class="flex-1 min-w-0">
            <div class="flex items-start lg:items-center">
              <div class="flex-shrink-0">
                <div class="relative">
                  <div class="w-14 h-14 bg-gradient-to-r from-emerald-500 to-green-600 rounded-2xl flex items-center justify-center shadow-lg transform hover:scale-105 transition-transform duration-200">
                    <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                  </div>
                  <!-- Activity indicator -->
                  <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-400 rounded-full border-2 border-white animate-pulse"></div>
                </div>
              </div>
              <div class="ml-4 flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <h1 class="text-3xl font-bold leading-tight text-gray-900 lg:text-4xl">
                    User Management
                  </h1>
                  <div class="hidden lg:flex items-center gap-2">
                    <div class="px-3 py-1 bg-emerald-100 text-emerald-800 text-xs font-medium rounded-full">
                      {{ totalUsers }} users
                    </div>
                    <div v-if="activeFiltersCount > 0" class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full">
                      {{ activeFiltersCount }} filters
                    </div>
                  </div>
                </div>
                <p class="text-sm text-gray-600 max-w-2xl leading-relaxed">
                  Manage user accounts, roles, permissions, and department assignments with advanced filtering and bulk operations
                </p>
                <div class="flex lg:hidden items-center gap-2 mt-3">
                  <div class="px-3 py-1 bg-emerald-100 text-emerald-800 text-xs font-medium rounded-full">
                    {{ totalUsers }} users
                  </div>
                  <div v-if="activeFiltersCount > 0" class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full">
                    {{ activeFiltersCount }} filters
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="mt-6 lg:mt-0 lg:ml-6 flex flex-col sm:flex-row gap-3">
            <button
              @click="refreshData"
              :disabled="loading"
              class="group inline-flex items-center justify-center px-4 py-2.5 border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 hover:shadow-md"
            >
              <svg 
                :class="['w-4 h-4 mr-2 transition-transform duration-200', 
                         loading ? 'animate-spin' : 'group-hover:rotate-180']" 
                fill="none" 
                stroke="currentColor" 
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              {{ loading ? 'Refreshing...' : 'Refresh' }}
            </button>
            <button
              @click="showCreateUserModal = true"
              class="group inline-flex items-center justify-center px-6 py-2.5 border border-transparent rounded-lg shadow-lg text-sm font-medium text-white bg-gradient-to-r from-emerald-600 to-green-600 hover:from-emerald-700 hover:to-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 transform hover:scale-105 hover:shadow-xl transition-all duration-200"
            >
              <svg class="w-4 h-4 mr-2 group-hover:rotate-90 transition-transform duration-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Add New User
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
      <!-- Quick Stats Bar -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div class="stats-card bg-white rounded-xl p-6 shadow-soft border border-gray-100 hover:shadow-medium transition-all duration-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Total Users</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalUsers }}</p>
            </div>
          </div>
        </div>
        
        <div class="stats-card bg-white rounded-xl p-6 shadow-soft border border-gray-100 hover:shadow-medium transition-all duration-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Active Users</p>
              <p class="text-2xl font-bold text-gray-900">{{ activeUsersCount }}</p>
            </div>
          </div>
        </div>
        
        <div class="stats-card bg-white rounded-xl p-6 shadow-soft border border-gray-100 hover:shadow-medium transition-all duration-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Selected</p>
              <p class="text-2xl font-bold text-gray-900">{{ selectedUsersCount }}</p>
            </div>
          </div>
        </div>
        
        <div class="stats-card bg-white rounded-xl p-6 shadow-soft border border-gray-100 hover:shadow-medium transition-all duration-200">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-amber-100 rounded-lg flex items-center justify-center">
                <svg class="w-5 h-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.414A1 1 0 013 6.707V4z" />
                </svg>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Active Filters</p>
              <p class="text-2xl font-bold text-gray-900">{{ activeFiltersCount }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters Section -->
      <div class="mb-8">
        <AdminUsersFilters />
      </div>

      <!-- Users Table Section -->
      <div class="mb-8">
        <AdminUsersTable />
      </div>

      <!-- Pagination Section -->
      <div class="flex justify-center">
        <AdminPagination />
      </div>
    </div>

    <!-- Enhanced Error Toast with Slide Animation -->
    <Transition
      enter-active-class="transform ease-out duration-300 transition"
      enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
      leave-active-class="transition ease-in duration-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="error && showErrorToast"
        class="fixed bottom-4 right-4 bg-white border-l-4 border-red-400 rounded-xl shadow-2xl max-w-md z-50 overflow-hidden"
      >
        <div class="p-4">
          <div class="flex items-start">
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center animate-pulse">
                <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div class="ml-4 flex-1">
              <h3 class="text-sm font-semibold text-gray-900">Something went wrong</h3>
              <p class="mt-1 text-sm text-gray-600 leading-relaxed">{{ error }}</p>
              <div class="mt-3 flex gap-2">
                <button
                  @click="refreshData"
                  class="text-xs bg-red-50 text-red-700 px-3 py-1 rounded-md hover:bg-red-100 transition-colors duration-200 font-medium"
                >
                  Retry
                </button>
                <button
                  @click="dismissError"
                  class="text-xs text-gray-500 hover:text-gray-700 transition-colors duration-200 font-medium"
                >
                  Dismiss
                </button>
              </div>
            </div>
            <div class="ml-4 flex-shrink-0">
              <button
                @click="dismissError"
                class="text-gray-400 hover:text-gray-600 transition-colors duration-200 p-1 rounded-md hover:bg-gray-100"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
        <!-- Progress bar for auto-dismiss -->
        <div class="h-1 bg-red-200">
          <div class="h-full bg-red-500 animate-shrink-width"></div>
        </div>
      </div>
    </Transition>

    <!-- Success Toast -->
    <Transition
      enter-active-class="transform ease-out duration-300 transition"
      enter-from-class="translate-y-2 opacity-0 sm:translate-y-0 sm:translate-x-2"
      enter-to-class="translate-y-0 opacity-100 sm:translate-x-0"
      leave-active-class="transition ease-in duration-100"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="showSuccessToast"
        class="fixed bottom-4 right-4 bg-white border-l-4 border-green-400 rounded-xl shadow-2xl max-w-md z-50 overflow-hidden"
      >
        <div class="p-4">
          <div class="flex items-start">
            <div class="flex-shrink-0">
              <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
            </div>
            <div class="ml-4 flex-1">
              <h3 class="text-sm font-semibold text-gray-900">Success!</h3>
              <p class="mt-1 text-sm text-gray-600 leading-relaxed">{{ successMessage }}</p>
            </div>
            <div class="ml-4 flex-shrink-0">
              <button
                @click="showSuccessToast = false"
                class="text-gray-400 hover:text-gray-600 transition-colors duration-200 p-1 rounded-md hover:bg-gray-100"
              >
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import AdminUsersFilters from '@/components/AdminUsersFilters.vue'
import AdminUsersTable from '@/components/AdminUsersTable.vue'
import AdminPagination from '@/components/AdminPagination.vue'

const adminStore = useAdminStore()
const authStore = useAuthStore()
const router = useRouter()

const showErrorToast = ref(false)
const showCreateUserModal = ref(false)
const showSuccessToast = ref(false)
const successMessage = ref('')

// Computed properties
const loading = computed(() => adminStore.loading)
const error = computed(() => adminStore.error)
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')
const totalUsers = computed(() => adminStore.totalElements)
const activeFiltersCount = computed(() => adminStore.activeFiltersCount)
const activeUsersCount = computed(() => 
  adminStore.users.filter(user => user.isActive).length
)
const selectedUsersCount = computed(() => adminStore.selectedCount)

// Watch for errors to show toast
watch(error, (newError) => {
  if (newError) {
    showErrorToast.value = true
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
      showErrorToast.value = false
    }, 5000)
  } else {
    showErrorToast.value = false
  }
})

// Methods
const refreshData = async () => {
  try {
    await adminStore.fetchUsers()
    showSuccessMessage('User data refreshed successfully')
  } catch (error) {
    console.error('Failed to refresh data:', error)
  }
}

const dismissError = () => {
  showErrorToast.value = false
  adminStore.clearError()
}

const showSuccessMessage = (message: string) => {
  successMessage.value = message
  showSuccessToast.value = true
  setTimeout(() => {
    showSuccessToast.value = false
  }, 3000)
}

// Check admin access and initialize
onMounted(async () => {
  // Check if user is admin
  if (!isAdmin.value) {
    console.warn('Access denied: Admin role required')
    router.push('/')
    return
  }

  // Fetch initial data
  await adminStore.fetchUsers()
})

// Provide global error handling
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled promise rejection:', event.reason)
})
</script>

<style scoped>
/* Custom animations for enhanced UX */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Progress bar animation for auto-dismiss */
@keyframes shrink-width {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.animate-shrink-width {
  animation: shrink-width 5s linear forwards;
}

/* Hover effects for interactive elements */
.stats-card {
  transform: translateY(0);
  transition: all 0.2s ease;
}

.stats-card:hover {
  transform: translateY(-2px) scale(1.02);
}

/* Enhanced shadows */
.shadow-soft {
  box-shadow: 0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04);
}

.shadow-medium {
  box-shadow: 0 4px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

/* Backdrop blur support */
@supports (backdrop-filter: blur(8px)) {
  .backdrop-blur-sm {
    backdrop-filter: blur(8px);
  }
}

/* Custom scrollbar for webkit browsers */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Focus states for better accessibility */
.focus-ring:focus {
  outline: none;
  box-shadow: 0 0 0 2px #10b981, 0 0 0 4px rgba(16, 185, 129, 0.2);
}

/* Loading state shimmer effect */
@keyframes shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: calc(200px + 100%) 0;
  }
}

.shimmer {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200px 100%;
  animation: shimmer 1.5s infinite;
}
</style>