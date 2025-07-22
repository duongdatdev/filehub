<template>
  <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200 mb-6">
    <div class="flex items-center justify-between mb-4">
      <h3 class="text-lg font-medium text-gray-900">Search & Filter Users</h3>
      <div class="flex items-center space-x-2">
        <span class="text-sm text-gray-500" v-if="activeFiltersCount > 0">
          {{ activeFiltersCount }} filter{{ activeFiltersCount !== 1 ? 's' : '' }} active
        </span>
        <button
          @click="clearAllFilters"
          v-if="activeFiltersCount > 0"
          class="text-sm text-blue-600 hover:text-blue-800 font-medium"
        >
          Clear All
        </button>
      </div>
    </div>

    <form @submit.prevent="applyFilters" class="space-y-4">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <!-- Username Filter -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
            Username
          </label>
          <input
            id="username"
            v-model="filters.username"
            type="text"
            placeholder="Search by username..."
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Email Filter -->
        <div>
          <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>
          <input
            id="email"
            v-model="filters.email"
            type="email"
            placeholder="Search by email..."
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Full Name Filter -->
        <div>
          <label for="fullName" class="block text-sm font-medium text-gray-700 mb-1">
            Full Name
          </label>
          <input
            id="fullName"
            v-model="filters.fullName"
            type="text"
            placeholder="Search by full name..."
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <!-- Role Filter -->
        <div>
          <label for="role" class="block text-sm font-medium text-gray-700 mb-1">
            Role
          </label>
          <select
            id="role"
            v-model="filters.role"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">All Roles</option>
            <option value="USER">User</option>
            <option value="ADMIN">Admin</option>
          </select>
        </div>

        <!-- Status Filter -->
        <div>
          <label for="status" class="block text-sm font-medium text-gray-700 mb-1">
            Status
          </label>
          <select
            id="status"
            v-model="filters.isActive"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option :value="undefined">All Statuses</option>
            <option :value="true">Active</option>
            <option :value="false">Inactive</option>
          </select>
        </div>

        <!-- Page Size -->
        <div>
          <label for="pageSize" class="block text-sm font-medium text-gray-700 mb-1">
            Items per page
          </label>
          <select
            id="pageSize"
            v-model="filters.size"
            class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
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
            class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors"
          >
            Apply Filters
          </button>
          <button
            type="button"
            @click="clearAllFilters"
            class="bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2 transition-colors"
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
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'
import type { AdminUserFilterRequest } from '@/services/adminApi'

const adminStore = useAdminStore()

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