<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <div class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center py-6">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">User Management</h1>
            <p class="mt-2 text-sm text-gray-600">
              Manage user accounts, roles, and permissions
            </p>
          </div>
          <div class="flex items-center space-x-4">
            <!-- Refresh button -->
            <button
              @click="refreshData"
              :disabled="loading"
              class="bg-white border border-gray-300 rounded-md px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg
                :class="['h-4 w-4 mr-2', loading ? 'animate-spin' : '']"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Refresh
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Filters -->
      <AdminUsersFilters />

      <!-- Users Table -->
      <AdminUsersTable />

      <!-- Pagination -->
      <AdminPagination />
    </div>

    <!-- Error Toast -->
    <div
      v-if="error && showErrorToast"
      class="fixed bottom-4 right-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg shadow-lg max-w-md"
    >
      <div class="flex items-center justify-between">
        <div class="flex items-center">
          <svg class="h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
          <span class="text-sm font-medium">{{ error }}</span>
        </div>
        <button
          @click="dismissError"
          class="ml-4 text-red-400 hover:text-red-600"
        >
          <svg class="h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </div>
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

// Computed properties
const loading = computed(() => adminStore.loading)
const error = computed(() => adminStore.error)
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

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
const refreshData = () => {
  adminStore.fetchUsers()
}

const dismissError = () => {
  showErrorToast.value = false
  adminStore.clearError()
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
/* Additional custom styles if needed */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>