<template>
  <div class="bg-white rounded-xl shadow-soft border border-gray-100 overflow-hidden">
    <!-- Header Section -->
    <div class="px-6 py-4 bg-gradient-to-r from-gray-50 to-white border-b border-gray-100">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <h3 class="text-lg font-semibold text-gray-900">Search & Filter Users</h3>
        </div>
        <div class="flex items-center space-x-3">
          <div v-if="activeFiltersCount > 0" class="flex items-center space-x-2">
            <div class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full">
              {{ activeFiltersCount }} filter{{ activeFiltersCount !== 1 ? 's' : '' }} active
            </div>
            <button
              @click="clearAllFilters"
              class="text-sm text-red-600 hover:text-red-800 font-medium transition-colors duration-200 flex items-center space-x-1"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
              <span>Clear All</span>
            </button>
          </div>
          <button
            @click="isExpanded = !isExpanded"
            class="p-2 text-gray-400 hover:text-gray-600 transition-colors duration-200 rounded-lg hover:bg-gray-100"
            :aria-expanded="isExpanded"
            :aria-label="isExpanded ? 'Collapse filters' : 'Expand filters'"
          >
            <svg 
              :class="['w-5 h-5 transform transition-transform duration-200', isExpanded ? 'rotate-180' : '']" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- Collapsible Content -->
    <div v-show="isExpanded" class="px-6 py-6">
      <form @submit.prevent="applyFilters" class="space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <!-- Username Filter -->
          <div class="group">
            <label for="username" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <span>Username</span>
            </label>
            <div class="relative">
              <input
                id="username"
                v-model="filters.username"
                type="text"
                placeholder="Search by username..."
                class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900 placeholder-gray-500"
              />
              <div v-if="filters.username" class="absolute inset-y-0 right-0 flex items-center pr-3">
                <button
                  @click="filters.username = ''"
                  type="button"
                  class="text-gray-400 hover:text-gray-600 transition-colors duration-200"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- Email Filter -->
          <div class="group">
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              <span>Email</span>
            </label>
            <div class="relative">
              <input
                id="email"
                v-model="filters.email"
                type="email"
                placeholder="Search by email..."
                class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900 placeholder-gray-500"
              />
              <div v-if="filters.email" class="absolute inset-y-0 right-0 flex items-center pr-3">
                <button
                  @click="filters.email = ''"
                  type="button"
                  class="text-gray-400 hover:text-gray-600 transition-colors duration-200"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- Full Name Filter -->
          <div class="group">
            <label for="fullName" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
              </svg>
              <span>Full Name</span>
            </label>
            <div class="relative">
              <input
                id="fullName"
                v-model="filters.fullName"
                type="text"
                placeholder="Search by full name..."
                class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900 placeholder-gray-500"
              />
              <div v-if="filters.fullName" class="absolute inset-y-0 right-0 flex items-center pr-3">
                <button
                  @click="filters.fullName = ''"
                  type="button"
                  class="text-gray-400 hover:text-gray-600 transition-colors duration-200"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <!-- Role Filter -->
          <div class="group">
            <label for="role" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4M7.835 4.697a3.42 3.42 0 001.946-.806 3.42 3.42 0 014.438 0 3.42 3.42 0 001.946.806 3.42 3.42 0 013.138 3.138 3.42 3.42 0 00.806 1.946 3.42 3.42 0 010 4.438 3.42 3.42 0 00-.806 1.946 3.42 3.42 0 01-3.138 3.138 3.42 3.42 0 00-1.946.806 3.42 3.42 0 01-4.438 0 3.42 3.42 0 00-1.946-.806 3.42 3.42 0 01-3.138-3.138 3.42 3.42 0 00-.806-1.946 3.42 3.42 0 010-4.438 3.42 3.42 0 00.806-1.946 3.42 3.42 0 013.138-3.138z" />
              </svg>
              <span>Role</span>
            </label>
            <select
              id="role"
              v-model="filters.role"
              class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900"
            >
              <option value="">All Roles</option>
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>

          <!-- Status Filter -->
          <div class="group">
            <label for="status" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>Status</span>
            </label>
            <select
              id="status"
              v-model="filters.isActive"
              class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900"
            >
              <option :value="undefined">All Statuses</option>
              <option :value="true">Active</option>
              <option :value="false">Inactive</option>
            </select>
          </div>

          <!-- Page Size -->
          <div class="group">
            <label for="pageSize" class="block text-sm font-medium text-gray-700 mb-2 flex items-center space-x-2">
              <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17V7m0 10a2 2 0 01-2 2H5a2 2 0 01-2-2V7a2 2 0 012-2h2a2 2 0 012 2m0 10a2 2 0 002 2h2a2 2 0 002-2M9 7a2 2 0 012-2h2a2 2 0 012 2m0 10V7m0 10a2 2 0 002 2h2a2 2 0 002-2V7a2 2 0 00-2-2h-2a2 2 0 00-2 2" />
              </svg>
              <span>Items per page</span>
            </label>
            <select
            id="pageSize"
            v-model="filters.size"
            class="w-full px-4 py-3 bg-white border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200 group-hover:border-gray-400 text-gray-900"
          >
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
        </div>
      </div>

      <div class="flex items-center justify-between pt-4 border-t border-gray-200">
        <div class="flex items-center space-x-4">
          <button
            type="submit"
            class="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors font-medium"
          >
            Apply Filters
          </button>
          <button
            type="button"
            @click="clearAllFilters"
            class="bg-gray-100 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-colors font-medium"
          >
            Clear
          </button>
        </div>

        <div class="text-sm text-gray-500">
          {{ totalElements }} user{{ totalElements !== 1 ? 's' : '' }} found
        </div>
      </div>
    </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'
import type { AdminUserFilterRequest } from '@/services/adminApi'

const adminStore = useAdminStore()

// Component state
const isExpanded = ref(true)

// Local reactive filters
const filters = ref<AdminUserFilterRequest>({
  username: '',
  email: '',
  fullName: '',
  role: '',
  isActive: undefined,
  size: 10
})

// Computed properties
const activeFiltersCount = computed(() => {
  let count = 0
  if (filters.value.username) count++
  if (filters.value.email) count++
  if (filters.value.fullName) count++
  if (filters.value.role) count++
  if (filters.value.isActive !== undefined) count++
  return count
})

const totalElements = computed(() => adminStore.totalElements)

// Watch for page size changes and apply immediately
watch(() => filters.value.size, (newSize) => {
  if (newSize) {
    adminStore.setPageSize(newSize)
  }
})

// Methods
const applyFilters = () => {
  const cleanFilters: AdminUserFilterRequest = {}
  
  if (filters.value.username?.trim()) cleanFilters.username = filters.value.username.trim()
  if (filters.value.email?.trim()) cleanFilters.email = filters.value.email.trim()
  if (filters.value.fullName?.trim()) cleanFilters.fullName = filters.value.fullName.trim()
  if (filters.value.role) cleanFilters.role = filters.value.role
  if (filters.value.isActive !== undefined) cleanFilters.isActive = filters.value.isActive
  if (filters.value.size) cleanFilters.size = filters.value.size

  adminStore.applyFilters(cleanFilters)
}

const clearAllFilters = () => {
  filters.value = {
    username: '',
    email: '',
    fullName: '',
    role: '',
    isActive: undefined,
    size: 10
  }
  adminStore.clearFilters()
}

// Initialize filters from store
const initializeFilters = () => {
  const storeFilters = adminStore.filters
  filters.value = {
    username: storeFilters.username || '',
    email: storeFilters.email || '',
    fullName: storeFilters.fullName || '',
    role: storeFilters.role || '',
    isActive: storeFilters.isActive,
    size: storeFilters.size || 10
  }
}

// Initialize on mount
initializeFilters()
</script>

<style scoped>
/* Enhanced shadows */
.shadow-soft {
  box-shadow: 0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04);
}

/* Group hover effects */
.group:hover .group-hover\:border-gray-400 {
  border-color: #9ca3af;
}

/* Focus visible improvements */
input:focus-visible,
select:focus-visible {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* Custom transitions */
.transition-all {
  transition-property: all;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 200ms;
}

/* Smooth collapse/expand animation */
.collapse-enter-active,
.collapse-leave-active {
  transition: all 0.3s ease;
  max-height: 1000px;
  overflow: hidden;
}

.collapse-enter-from,
.collapse-leave-to {
  max-height: 0;
  opacity: 0;
}
</style>