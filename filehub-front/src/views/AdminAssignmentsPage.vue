<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="px-4 py-4 sm:px-0">
        <div class="border-b border-gray-200 pb-4">
          <div class="flex justify-between items-center">
            <div>
              <h1 class="text-3xl font-bold leading-tight text-gray-900">User Assignments</h1>
              <p class="mt-2 text-sm text-gray-600">Manage user assignments to departments and projects</p>
            </div>
            <div class="flex space-x-3">
              <button
                @click="openSingleAssignmentModal"
                class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <svg class="h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Assign User
              </button>
              <button
                @click="openBulkAssignmentModal"
                class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <svg class="h-4 w-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                Bulk Assignment
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Assignment Overview Cards -->
      <div class="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-3">
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 018.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Users with Departments</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ assignmentStats.usersWithDepartments }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 013.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Users with Projects</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ assignmentStats.usersWithProjects }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Unassigned Users</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ assignmentStats.unassignedUsers }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Assignment Table -->
      <div class="mt-8">
        <div class="bg-white shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">Assignment Overview</h3>
            
            <!-- Search and Filters -->
            <div class="mb-4 flex flex-col sm:flex-row gap-4">
              <div class="flex-1">
                <input
                  type="text"
                  v-model="searchQuery"
                  placeholder="Search users..."
                  class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
              </div>
              <div class="flex gap-2">
                <select
                  v-model="filterDepartment"
                  class="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">All Departments</option>
                  <option value="Engineering">Engineering</option>
                  <option value="Marketing">Marketing</option>
                  <option value="Sales">Sales</option>
                </select>
                <select
                  v-model="filterRole"
                  class="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="">All Roles</option>
                  <option value="ADMIN">Admin</option>
                  <option value="MANAGER">Manager</option>
                  <option value="USER">User</option>
                </select>
              </div>
            </div>

            <!-- Assignment Table -->
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Projects</th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="user in filteredUsers" :key="user.id" class="hover:bg-gray-50">
                    <td class="px-6 py-4 whitespace-nowrap">
                      <div class="flex items-center">
                        <div>
                          <div class="text-sm font-medium text-gray-900">{{ user.fullName }}</div>
                          <div class="text-sm text-gray-500">{{ user.email }}</div>
                        </div>
                      </div>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap">
                      <span :class="[
                        'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                        user.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' :
                        user.role === 'MANAGER' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      ]">
                        {{ user.role }}
                      </span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ user.department || 'Unassigned' }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div v-if="user.projects && user.projects.length > 0" class="flex flex-wrap gap-1">
                        <span
                          v-for="project in user.projects.slice(0, 2)"
                          :key="project"
                          class="inline-flex px-2 py-1 text-xs font-medium bg-green-100 text-green-800 rounded-full"
                        >
                          {{ project }}
                        </span>
                        <span
                          v-if="user.projects.length > 2"
                          class="inline-flex px-2 py-1 text-xs font-medium bg-gray-100 text-gray-800 rounded-full"
                        >
                          +{{ user.projects.length - 2 }} more
                        </span>
                      </div>
                      <span v-else class="text-gray-500">No projects</span>
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        @click="editUserAssignments(user)"
                        class="text-blue-600 hover:text-blue-900 mr-4"
                      >
                        Edit
                      </button>
                      <button
                        @click="viewUserAssignments(user)"
                        class="text-green-600 hover:text-green-900"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Assignment Modals -->
    <UserAssignmentModal
      :isOpen="showSingleAssignmentModal"
      mode="single"
      @close="showSingleAssignmentModal = false"
      @success="handleAssignmentSuccess"
    />

    <UserAssignmentModal
      :isOpen="showBulkAssignmentModal"
      mode="bulk"
      @close="showBulkAssignmentModal = false"
      @success="handleAssignmentSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { usePermissions } from '@/composables/usePermissions'
import { useRouter } from 'vue-router'
import UserAssignmentModal from '@/components/UserAssignmentModal.vue'

const notificationStore = useNotificationStore()
const permissions = usePermissions()
const router = useRouter()

// Modal states
const showSingleAssignmentModal = ref(false)
const showBulkAssignmentModal = ref(false)

// Search and filter
const searchQuery = ref('')
const filterDepartment = ref('')
const filterRole = ref('')

// Stats data
const assignmentStats = ref({
  usersWithDepartments: 89,
  usersWithProjects: 67,
  unassignedUsers: 12
})

// Mock user data with assignments
const users = ref([
  {
    id: 1,
    fullName: 'John Doe',
    email: 'john.doe@example.com',
    role: 'USER',
    department: 'Engineering',
    projects: ['Website Redesign', 'Mobile App']
  },
  {
    id: 2,
    fullName: 'Jane Smith',
    email: 'jane.smith@example.com',
    role: 'MANAGER',
    department: 'Marketing',
    projects: ['Marketing Campaign', 'Brand Refresh']
  },
  {
    id: 3,
    fullName: 'Bob Johnson',
    email: 'bob.johnson@example.com',
    role: 'USER',
    department: 'Sales',
    projects: ['Lead Generation']
  },
  {
    id: 4,
    fullName: 'Alice Wilson',
    email: 'alice.wilson@example.com',
    role: 'ADMIN',
    department: 'Engineering',
    projects: ['Infrastructure', 'Security Audit', 'API Development']
  },
  {
    id: 5,
    fullName: 'Charlie Brown',
    email: 'charlie.brown@example.com',
    role: 'USER',
    department: null,
    projects: []
  }
])

// Computed
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch = !searchQuery.value || 
      user.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesDepartment = !filterDepartment.value || user.department === filterDepartment.value
    const matchesRole = !filterRole.value || user.role === filterRole.value
    
    return matchesSearch && matchesDepartment && matchesRole
  })
})

// Methods
const openSingleAssignmentModal = () => {
  if (!permissions.canManageAssignments.value) {
    notificationStore.error('Permission Denied', 'You do not have permission to manage user assignments.')
    return
  }
  showSingleAssignmentModal.value = true
}

const openBulkAssignmentModal = () => {
  if (!permissions.canManageAssignments.value) {
    notificationStore.error('Permission Denied', 'You do not have permission to manage user assignments.')
    return
  }
  showBulkAssignmentModal.value = true
}

const editUserAssignments = (user: any) => {
  if (!permissions.canManageAssignments.value) {
    notificationStore.error('Permission Denied', 'You do not have permission to edit user assignments.')
    return
  }
  // TODO: Open edit modal with pre-selected user
  notificationStore.info('Edit Assignments', `Opening assignment editor for ${user.fullName}`)
}

const viewUserAssignments = (user: any) => {
  notificationStore.info('View Assignments', `Viewing assignments for ${user.fullName}`)
  // TODO: Open view modal or navigate to user detail page
}

const handleAssignmentSuccess = () => {
  // Refresh the user data
  notificationStore.success('Assignment Updated', 'User assignments have been updated successfully.')
  // TODO: Reload data from API
}

// Lifecycle
onMounted(() => {
  // Check permissions
  if (!permissions.canAccessAdmin.value) {
    notificationStore.error('Access Denied', 'You do not have permission to access this page.')
    router.push('/')
    return
  }

  // Load assignment data
  // TODO: Load real data from API
})
</script>
