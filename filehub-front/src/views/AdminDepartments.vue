<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="px-4 py-4 sm:px-0">
        <div class="border-b border-gray-200 pb-4">
          <div class="flex justify-between items-center">
            <div>
              <h1 class="text-3xl font-bold leading-tight text-gray-900">Department Management</h1>
              <p class="mt-2 text-sm text-gray-600">Manage organization departments and hierarchies</p>
            </div>
            <button
              @click="showCreateModal = true"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Create Department
            </button>
          </div>
        </div>
      </div>

      <!-- Stats Cards -->
      <div class="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.75 21h16.5M4.5 3h15M5.25 3v18m13.5-18v18M9 6.75h1.5m-1.5 3h1.5m-1.5 3h1.5m3-6H15m-1.5 3H15m-1.5 3H15M9 21v-3.375c0-.621.504-1.125 1.125-1.125h3.75c.621 0 1.125.504 1.125 1.125V21" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Departments</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.totalDepartments }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 718.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Users</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.totalUsers }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 713.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Active Projects</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.activeProjects }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="p-5">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Active Departments</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.activeDepartments }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="mt-6 bg-white shadow rounded-lg">
        <div class="px-6 py-4">
          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <div>
              <label for="name-filter" class="block text-sm font-medium text-gray-700">Department Name</label>
              <input
                id="name-filter"
                v-model="filters.name"
                type="text"
                placeholder="Search by name..."
                class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label for="manager-filter" class="block text-sm font-medium text-gray-700">Manager</label>
              <select
                id="manager-filter"
                v-model="filters.managerId"
                class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              >
                <option value="">All Managers</option>
                <option v-for="manager in managers" :key="manager.id" :value="manager.id">
                  {{ manager.fullName }}
                </option>
              </select>
            </div>
            <div>
              <label for="status-filter" class="block text-sm font-medium text-gray-700">Status</label>
              <select
                id="status-filter"
                v-model="filters.isActive"
                class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              >
                <option value="">All Statuses</option>
                <option :value="true">Active</option>
                <option :value="false">Inactive</option>
              </select>
            </div>
            <div class="flex items-end">
              <button
                @click="applyFilters"
                class="w-full inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Apply Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Department Table -->
      <div class="mt-6 bg-white shadow overflow-hidden sm:rounded-md">
        <div class="px-4 py-5 sm:p-6">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Department
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Manager
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Users Count
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Projects Count
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="department in departments" :key="department.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-10 w-10">
                        <div class="h-10 w-10 rounded-lg bg-blue-100 flex items-center justify-center">
                          <svg class="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.75 21h16.5M4.5 3h15M5.25 3v18m13.5-18v18M9 6.75h1.5m-1.5 3h1.5m-1.5 3h1.5m3-6H15m-1.5 3H15m-1.5 3H15M9 21v-3.375c0-.621.504-1.125 1.125-1.125h3.75c.621 0 1.125.504 1.125 1.125V21" />
                          </svg>
                        </div>
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ department.name }}</div>
                        <div class="text-sm text-gray-500" v-if="department.description">{{ department.description }}</div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ getManagerName(department.managerId) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ department.userCount || 0 }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ department.projectCount || 0 }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      :class="[
                        'inline-flex px-2 text-xs font-semibold rounded-full',
                        department.isActive
                          ? 'bg-green-100 text-green-800'
                          : 'bg-red-100 text-red-800'
                      ]"
                    >
                      {{ department.isActive ? 'Active' : 'Inactive' }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex space-x-2">
                      <button
                        @click="viewDepartment(department)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        View
                      </button>
                      <button
                        @click="editDepartment(department)"
                        class="text-indigo-600 hover:text-indigo-900"
                      >
                        Edit
                      </button>
                      <button
                        @click="manageDepartmentUsers(department)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Manage Users
                      </button>
                      <button
                        @click="toggleDepartmentStatus(department)"
                        :class="department.isActive ? 'text-red-600 hover:text-red-900' : 'text-green-600 hover:text-green-900'"
                      >
                        {{ department.isActive ? 'Deactivate' : 'Activate' }}
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty State -->
          <div v-if="departments.length === 0" class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.75 21h16.5M4.5 3h15M5.25 3v18m13.5-18v18M9 6.75h1.5m-1.5 3h1.5m-1.5 3h1.5m3-6H15m-1.5 3H15m-1.5 3H15M9 21v-3.375c0-.621.504-1.125 1.125-1.125h3.75c.621 0 1.125.504 1.125 1.125V21" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">No departments found</h3>
            <p class="mt-1 text-sm text-gray-500">Get started by creating a new department.</p>
            <div class="mt-6">
              <button
                @click="showCreateModal = true"
                class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                </svg>
                Create Department
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Create/Edit Department Modal -->
      <div v-if="showCreateModal || showEditModal" class="fixed inset-0 z-50 overflow-y-auto">
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
          <div class="fixed inset-0 transition-opacity" aria-hidden="true">
            <div class="absolute inset-0 bg-gray-500 opacity-75" @click="closeModal"></div>
          </div>

          <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
            <form @submit.prevent="saveDepartment">
              <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div class="sm:flex sm:items-start">
                  <div class="mt-3 text-center sm:mt-0 sm:text-left w-full">
                    <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
                      {{ showCreateModal ? 'Create New Department' : 'Edit Department' }}
                    </h3>
                    
                    <div class="space-y-4">
                      <div>
                        <label for="department-name" class="block text-sm font-medium text-gray-700">Name</label>
                        <input
                          id="department-name"
                          v-model="formData.name"
                          type="text"
                          required
                          class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          placeholder="Enter department name"
                        />
                      </div>

                      <div>
                        <label for="department-description" class="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                          id="department-description"
                          v-model="formData.description"
                          rows="3"
                          class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          placeholder="Enter department description"
                        ></textarea>
                      </div>

                      <div>
                        <label for="department-manager" class="block text-sm font-medium text-gray-700">Manager</label>
                        <select
                          id="department-manager"
                          v-model="formData.managerId"
                          class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        >
                          <option value="">Select a manager</option>
                          <option v-for="manager in managers" :key="manager.id" :value="manager.id">
                            {{ manager.fullName }}
                          </option>
                        </select>
                      </div>

                      <div>
                        <label for="department-parent" class="block text-sm font-medium text-gray-700">Parent Department</label>
                        <select
                          id="department-parent"
                          v-model="formData.parentId"
                          class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        >
                          <option value="">No parent (Root department)</option>
                          <option v-for="dept in rootDepartments" :key="dept.id" :value="dept.id">
                            {{ dept.name }}
                          </option>
                        </select>
                      </div>

                      <div class="flex items-center">
                        <input
                          id="department-active"
                          v-model="formData.isActive"
                          type="checkbox"
                          class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                        <label for="department-active" class="ml-2 block text-sm text-gray-900">
                          Active Department
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
                <button
                  type="submit"
                  :disabled="loading"
                  class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50"
                >
                  {{ loading ? 'Saving...' : (showCreateModal ? 'Create Department' : 'Update Department') }}
                </button>
                <button
                  type="button"
                  @click="closeModal"
                  class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

interface Department {
  id: number
  name: string
  description?: string
  managerId?: number
  parentId?: number
  isActive: boolean
  userCount?: number
  projectCount?: number
  createdAt?: string
  updatedAt?: string
}

interface User {
  id: number
  username: string
  email: string
  fullName: string
  role: string
}

const authStore = useAuthStore()
const router = useRouter()

// Reactive data
const departments = ref<Department[]>([])
const managers = ref<User[]>([])
const rootDepartments = ref<Department[]>([])
const loading = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentDepartment = ref<Department | null>(null)

// Stats
const stats = ref({
  totalDepartments: 0,
  totalUsers: 0,
  activeProjects: 0,
  activeDepartments: 0
})

// Filters
const filters = ref({
  name: '',
  managerId: '',
  isActive: ''
})

// Form data
const formData = ref({
  name: '',
  description: '',
  managerId: '',
  parentId: '',
  isActive: true
})

// Computed
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

// Methods
const loadDepartments = async () => {
  try {
    loading.value = true
    // TODO: Replace with actual API call
    const mockDepartments: Department[] = [
      {
        id: 1,
        name: 'Engineering',
        description: 'Software development and technical teams',
        managerId: 1,
        isActive: true,
        userCount: 25,
        projectCount: 8
      },
      {
        id: 2,
        name: 'Marketing',
        description: 'Marketing and brand management',
        managerId: 2,
        isActive: true,
        userCount: 12,
        projectCount: 5
      },
      {
        id: 3,
        name: 'Frontend Team',
        description: 'User interface development',
        managerId: 3,
        parentId: 1,
        isActive: true,
        userCount: 8,
        projectCount: 4
      }
    ]
    departments.value = mockDepartments
    stats.value.totalDepartments = mockDepartments.length
    stats.value.activeDepartments = mockDepartments.filter(d => d.isActive).length
  } catch (error) {
    console.error('Failed to load departments:', error)
  } finally {
    loading.value = false
  }
}

const loadManagers = async () => {
  try {
    // TODO: Replace with actual API call to get users with ADMIN role or potential managers
    const mockManagers: User[] = [
      { id: 1, username: 'admin', email: 'admin@company.com', fullName: 'System Administrator', role: 'ADMIN' },
      { id: 2, username: 'john.doe', email: 'john@company.com', fullName: 'John Doe', role: 'USER' },
      { id: 3, username: 'jane.smith', email: 'jane@company.com', fullName: 'Jane Smith', role: 'USER' }
    ]
    managers.value = mockManagers
  } catch (error) {
    console.error('Failed to load managers:', error)
  }
}

const loadRootDepartments = async () => {
  try {
    // Load departments that can be parents (no parentId)
    rootDepartments.value = departments.value.filter(d => !d.parentId)
  } catch (error) {
    console.error('Failed to load root departments:', error)
  }
}

const getManagerName = (managerId?: number): string => {
  if (!managerId) return 'No manager assigned'
  const manager = managers.value.find(m => m.id === managerId)
  return manager ? manager.fullName : 'Unknown manager'
}

const applyFilters = async () => {
  // TODO: Implement filtering logic
  await loadDepartments()
}

const viewDepartment = (department: Department) => {
  // TODO: Navigate to department detail view
  router.push(`/admin/departments/${department.id}`)
}

const editDepartment = (department: Department) => {
  currentDepartment.value = department
  formData.value = {
    name: department.name,
    description: department.description || '',
    managerId: department.managerId?.toString() || '',
    parentId: department.parentId?.toString() || '',
    isActive: department.isActive
  }
  showEditModal.value = true
}

const manageDepartmentUsers = (department: Department) => {
  // TODO: Navigate to department user management
  router.push(`/admin/departments/${department.id}/users`)
}

const toggleDepartmentStatus = async (department: Department) => {
  try {
    loading.value = true
    // TODO: Implement API call to toggle department status
    department.isActive = !department.isActive
    console.log(`Department ${department.name} ${department.isActive ? 'activated' : 'deactivated'}`)
  } catch (error) {
    console.error('Failed to toggle department status:', error)
  } finally {
    loading.value = false
  }
}

const saveDepartment = async () => {
  try {
    loading.value = true
    
    if (showCreateModal.value) {
      // TODO: Implement create department API call
      const newDepartment: Department = {
        id: Math.max(...departments.value.map(d => d.id)) + 1,
        name: formData.value.name,
        description: formData.value.description,
        managerId: formData.value.managerId ? parseInt(formData.value.managerId) : undefined,
        parentId: formData.value.parentId ? parseInt(formData.value.parentId) : undefined,
        isActive: formData.value.isActive,
        userCount: 0,
        projectCount: 0
      }
      departments.value.push(newDepartment)
    } else if (showEditModal.value && currentDepartment.value) {
      // TODO: Implement update department API call
      const index = departments.value.findIndex(d => d.id === currentDepartment.value!.id)
      if (index !== -1) {
        departments.value[index] = {
          ...departments.value[index],
          name: formData.value.name,
          description: formData.value.description,
          managerId: formData.value.managerId ? parseInt(formData.value.managerId) : undefined,
          parentId: formData.value.parentId ? parseInt(formData.value.parentId) : undefined,
          isActive: formData.value.isActive
        }
      }
    }
    
    closeModal()
    await loadDepartments()
  } catch (error) {
    console.error('Failed to save department:', error)
  } finally {
    loading.value = false
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  currentDepartment.value = null
  formData.value = {
    name: '',
    description: '',
    managerId: '',
    parentId: '',
    isActive: true
  }
}

// Lifecycle
onMounted(async () => {
  // Check admin access
  if (!isAdmin.value) {
    console.warn('Access denied: Admin role required')
    router.push('/')
    return
  }

  // Load initial data
  await Promise.all([
    loadDepartments(),
    loadManagers()
  ])
  await loadRootDepartments()
  
  // Mock additional stats
  stats.value.totalUsers = 156
  stats.value.activeProjects = 23
})
</script>
