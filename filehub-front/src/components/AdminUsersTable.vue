<template>
  <div class="bg-white shadow-sm rounded-lg border border-gray-200 overflow-hidden">
    <!-- Loading State -->
    <div v-if="loading" class="p-8 text-center">
      <div class="inline-flex items-center justify-center">
        <svg class="animate-spin -ml-1 mr-3 h-8 w-8 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span class="text-lg text-gray-600">Loading users...</span>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="p-8 text-center">
      <div class="text-red-600 mb-4">
        <svg class="mx-auto h-12 w-12" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">Error Loading Users</h3>
      <p class="text-gray-600 mb-4">{{ error }}</p>
      <button
        @click="retryFetch"
        class="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        Try Again
      </button>
    </div>

    <!-- Empty State -->
    <div v-else-if="!hasUsers" class="p-8 text-center">
      <div class="text-gray-400 mb-4">
        <svg class="mx-auto h-12 w-12" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
        </svg>
      </div>
      <h3 class="text-lg font-medium text-gray-900 mb-2">No Users Found</h3>
      <p class="text-gray-600">No users match your current search criteria.</p>
    </div>

    <!-- Table -->
    <div v-else class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th scope="col" class="px-6 py-3 text-left">
              <input
                type="checkbox"
                :checked="allCurrentPageSelected"
                :indeterminate="someCurrentPageSelected"
                @change="toggleSelectAllCurrentPage"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('id')"
            >
              <div class="flex items-center">
                ID
                <SortIcon :direction="getSortDirection('id')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('username')"
            >
              <div class="flex items-center">
                Username
                <SortIcon :direction="getSortDirection('username')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('email')"
            >
              <div class="flex items-center">
                Email
                <SortIcon :direction="getSortDirection('email')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('fullName')"
            >
              <div class="flex items-center">
                Full Name
                <SortIcon :direction="getSortDirection('fullName')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('role')"
            >
              <div class="flex items-center">
                Role
                <SortIcon :direction="getSortDirection('role')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('isActive')"
            >
              <div class="flex items-center">
                Status
                <SortIcon :direction="getSortDirection('isActive')" />
              </div>
            </th>
            <th
              scope="col"
              class="group px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
              @click="toggleSort('createdAt')"
            >
              <div class="flex items-center">
                Created
                <SortIcon :direction="getSortDirection('createdAt')" />
              </div>
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              Actions
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="user in users" :key="user.id" class="hover:bg-gray-50">
            <td class="px-6 py-4 whitespace-nowrap">
              <input
                type="checkbox"
                :checked="isUserSelected(user.id)"
                @change="toggleUserSelection(user.id)"
                class="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
              {{ user.id }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div class="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                  <span class="text-sm font-medium text-blue-600">
                    {{ user.username.charAt(0).toUpperCase() }}
                  </span>
                </div>
                <div class="ml-3">
                  <div class="text-sm font-medium text-gray-900">{{ user.username }}</div>
                </div>
              </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ user.email }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
              {{ user.fullName }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'inline-flex px-2 text-xs font-semibold rounded-full',
                  user.role === 'ADMIN'
                    ? 'bg-purple-100 text-purple-800'
                    : 'bg-green-100 text-green-800'
                ]"
              >
                {{ user.role }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <span
                :class="[
                  'inline-flex px-2 text-xs font-semibold rounded-full',
                  user.isActive
                    ? 'bg-green-100 text-green-800'
                    : 'bg-red-100 text-red-800'
                ]"
              >
                {{ user.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              {{ formatDate(user.createdAt) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <div class="flex items-center space-x-2">
                <button
                  @click="toggleUserStatus(user)"
                  :disabled="updatingUserId === user.id"
                  :class="[
                    'px-3 py-1 rounded text-xs font-medium transition-colors',
                    user.isActive
                      ? 'bg-red-100 text-red-700 hover:bg-red-200'
                      : 'bg-green-100 text-green-700 hover:bg-green-200',
                    updatingUserId === user.id ? 'opacity-50 cursor-not-allowed' : ''
                  ]"
                >
                  <span v-if="updatingUserId === user.id" class="inline-flex items-center">
                    <svg class="animate-spin -ml-1 mr-1 h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Updating...
                  </span>
                  <span v-else>
                    {{ user.isActive ? 'Deactivate' : 'Activate' }}
                  </span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useAdminStore } from '@/stores/admin'
import SortIcon from './SortIcon.vue'
import type { User } from '@/services/api'

const adminStore = useAdminStore()
const updatingUserId = ref<number | null>(null)

// Computed properties
const loading = computed(() => adminStore.loading)
const error = computed(() => adminStore.error)
const users = computed(() => adminStore.users)
const hasUsers = computed(() => adminStore.hasUsers)
const currentSort = computed(() => adminStore.filters.sortBy)
const currentSortDir = computed(() => adminStore.filters.sortDir)
const allCurrentPageSelected = computed(() => adminStore.allCurrentPageSelected)
const someCurrentPageSelected = computed(() => adminStore.someCurrentPageSelected)

// Methods
const getSortDirection = (field: string): 'asc' | 'desc' | null => {
  if (currentSort.value === field) {
    return currentSortDir.value || 'asc'
  }
  return null
}

const toggleSort = (field: string) => {
  const currentDirection = getSortDirection(field)
  let newDirection: 'asc' | 'desc' = 'asc'
  
  if (currentDirection === 'asc') {
    newDirection = 'desc'
  } else if (currentDirection === 'desc') {
    newDirection = 'asc'
  }
  
  adminStore.setSorting(field, newDirection)
}

const toggleUserStatus = async (user: User) => {
  if (updatingUserId.value === user.id) return
  
  updatingUserId.value = user.id
  
  try {
    const success = await adminStore.updateUserStatus(user.id, !user.isActive)
    if (!success) {
      // Handle error - could show a toast notification
      console.error('Failed to update user status')
    }
  } catch (error) {
    console.error('Error updating user status:', error)
  } finally {
    updatingUserId.value = null
  }
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const retryFetch = () => {
  adminStore.clearError()
  adminStore.fetchUsers()
}

const toggleSelectAllCurrentPage = () => {
  adminStore.toggleSelectAllCurrentPage()
}

const toggleUserSelection = (userId: number) => {
  adminStore.toggleUserSelection(userId)
}

const isUserSelected = (userId: number) => {
  return adminStore.selectedUserIds.has(userId)
}
</script>