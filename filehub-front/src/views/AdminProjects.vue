<template>
  <div class="min-h-screen bg-gray-50 pt-16">
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="px-4 py-4 sm:px-0">
        <div class="border-b border-gray-200 pb-4">
          <div class="flex justify-between items-center">
            <div>
              <h1 class="text-3xl font-bold leading-tight text-gray-900">Project Management</h1>
              <p class="mt-2 text-sm text-gray-600">Manage projects and team assignments</p>
            </div>
            <button
              @click="showCreateModal = true"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
              </svg>
              Create Project
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 013.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Total Projects</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.totalProjects }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19.128a9.38 9.38 0 002.625.372 9.337 9.337 0 004.121-.952 4.125 4.125 0 00-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 718.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0111.964-3.07M12 6.375a3.375 3.375 0 11-6.75 0 3.375 3.375 0 016.75 0zm8.25 2.25a2.625 2.625 0 11-5.25 0 2.625 2.625 0 015.25 0z" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Team Members</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.totalMembers }}</dd>
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
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <div class="ml-5 w-0 flex-1">
                <dl>
                  <dt class="text-sm font-medium text-gray-500 truncate">Pending Projects</dt>
                  <dd class="text-lg font-medium text-gray-900">{{ stats.pendingProjects }}</dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="mt-6 bg-white shadow rounded-lg">
        <div class="px-6 py-4">
          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
            <div>
              <label for="name-filter" class="block text-sm font-medium text-gray-700">Project Name</label>
              <input
                id="name-filter"
                v-model="filters.name"
                type="text"
                placeholder="Search by name..."
                class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
              />
            </div>
            <div>
              <label for="department-filter" class="block text-sm font-medium text-gray-700">Department</label>
              <select
                id="department-filter"
                v-model="filters.departmentId"
                class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              >
                <option value="">All Departments</option>
                <option v-for="dept in departments" :key="dept.id" :value="dept.id">
                  {{ dept.name }}
                </option>
              </select>
            </div>
            <div>
              <label for="manager-filter" class="block text-sm font-medium text-gray-700">Project Manager</label>
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
                v-model="filters.status"
                class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              >
                <option value="">All Statuses</option>
                <option value="ACTIVE">Active</option>
                <option value="PENDING">Pending</option>
                <option value="COMPLETED">Completed</option>
                <option value="ARCHIVED">Archived</option>
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

      <!-- Project Table -->
      <div class="mt-6 bg-white shadow overflow-hidden sm:rounded-md">
        <div class="px-4 py-5 sm:p-6">
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Project
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Department
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Manager
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Team Size
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Progress
                  </th>
                  <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="project in projects" :key="project.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="flex-shrink-0 h-10 w-10">
                        <div class="h-10 w-10 rounded-lg bg-indigo-100 flex items-center justify-center">
                          <svg class="h-6 w-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 713.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
                          </svg>
                        </div>
                      </div>
                      <div class="ml-4">
                        <div class="text-sm font-medium text-gray-900">{{ project.name }}</div>
                        <div class="text-sm text-gray-500" v-if="project.description">{{ project.description }}</div>
                        <div class="text-xs text-gray-400" v-if="project.startDate">
                          Started: {{ formatDate(project.startDate) }}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ getDepartmentName(project.departmentId) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ getManagerName(project.managerId) }}
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ project.teamSize || 0 }} members
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span
                      :class="[
                        'inline-flex px-2 text-xs font-semibold rounded-full',
                        getStatusColor(project.status)
                      ]"
                    >
                      {{ project.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="w-full bg-gray-200 rounded-full h-2">
                      <div
                        class="bg-blue-600 h-2 rounded-full"
                        :style="{ width: `${project.progress || 0}%` }"
                      ></div>
                    </div>
                    <span class="text-xs text-gray-500 mt-1">{{ project.progress || 0 }}%</span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex space-x-2">
                      <button
                        @click="viewProject(project)"
                        class="text-blue-600 hover:text-blue-900"
                      >
                        View
                      </button>
                      <button
                        @click="editProject(project)"
                        class="text-indigo-600 hover:text-indigo-900"
                      >
                        Edit
                      </button>
                      <button
                        @click="manageProjectTeam(project)"
                        class="text-green-600 hover:text-green-900"
                      >
                        Team
                      </button>
                      <button
                        @click="updateProjectStatus(project)"
                        class="text-yellow-600 hover:text-yellow-900"
                      >
                        Status
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Empty State -->
          <div v-if="projects.length === 0" class="text-center py-12">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.25 14.15v4.25c0 1.094-.787 2.036-1.872 2.18-2.087.277-4.216.42-6.378.42s-4.291-.143-6.378-.42c-1.085-.144-1.872-1.086-1.872-2.18v-4.25m16.5 0a2.18 2.18 0 00.75-1.661V8.706c0-1.081-.768-2.015-1.837-2.175a48.114 48.114 0 00-3.413-.387m4.5 8.006c-.194.165-.42.295-.673.38A23.978 23.978 0 0112 15.75c-2.648 0-5.195-.429-7.577-1.22a2.016 2.016 0 01-.673-.38m0 0A2.18 2.18 0 013 12.489V8.706c0-1.081.768-2.015 1.837-2.175a48.111 48.111 0 713.413-.387m7.5 0V5.25A2.25 2.25 0 0013.5 3h-3A2.25 2.25 0 008.25 5.25v3.131" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">No projects found</h3>
            <p class="mt-1 text-sm text-gray-500">Get started by creating a new project.</p>
            <div class="mt-6">
              <button
                @click="showCreateModal = true"
                class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                </svg>
                Create Project
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Create/Edit Project Modal -->
      <div v-if="showCreateModal || showEditModal" class="fixed inset-0 z-50 overflow-y-auto">
        <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
          <div class="fixed inset-0 transition-opacity" aria-hidden="true">
            <div class="absolute inset-0 bg-gray-500 opacity-75" @click="closeModal"></div>
          </div>

          <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
            <form @submit.prevent="saveProject">
              <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                <div class="sm:flex sm:items-start">
                  <div class="mt-3 text-center sm:mt-0 sm:text-left w-full">
                    <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
                      {{ showCreateModal ? 'Create New Project' : 'Edit Project' }}
                    </h3>
                    
                    <div class="space-y-4">
                      <div>
                        <label for="project-name" class="block text-sm font-medium text-gray-700">Name</label>
                        <input
                          id="project-name"
                          v-model="formData.name"
                          type="text"
                          required
                          class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          placeholder="Enter project name"
                        />
                      </div>

                      <div>
                        <label for="project-description" class="block text-sm font-medium text-gray-700">Description</label>
                        <textarea
                          id="project-description"
                          v-model="formData.description"
                          rows="3"
                          class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          placeholder="Enter project description"
                        ></textarea>
                      </div>

                      <div>
                        <label for="project-department" class="block text-sm font-medium text-gray-700">Department</label>
                        <select
                          id="project-department"
                          v-model="formData.departmentId"
                          required
                          class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        >
                          <option value="">Select a department</option>
                          <option v-for="dept in departments" :key="dept.id" :value="dept.id">
                            {{ dept.name }}
                          </option>
                        </select>
                      </div>

                      <div>
                        <label for="project-manager" class="block text-sm font-medium text-gray-700">Project Manager</label>
                        <select
                          id="project-manager"
                          v-model="formData.managerId"
                          class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        >
                          <option value="">Select a manager</option>
                          <option v-for="manager in managers" :key="manager.id" :value="manager.id">
                            {{ manager.fullName }}
                          </option>
                        </select>
                      </div>

                      <div class="grid grid-cols-2 gap-4">
                        <div>
                          <label for="project-start-date" class="block text-sm font-medium text-gray-700">Start Date</label>
                          <input
                            id="project-start-date"
                            v-model="formData.startDate"
                            type="date"
                            class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          />
                        </div>
                        <div>
                          <label for="project-end-date" class="block text-sm font-medium text-gray-700">End Date</label>
                          <input
                            id="project-end-date"
                            v-model="formData.endDate"
                            type="date"
                            class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          />
                        </div>
                      </div>

                      <div>
                        <label for="project-status" class="block text-sm font-medium text-gray-700">Status</label>
                        <select
                          id="project-status"
                          v-model="formData.status"
                          class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                        >
                          <option value="PENDING">Pending</option>
                          <option value="ACTIVE">Active</option>
                          <option value="COMPLETED">Completed</option>
                          <option value="ARCHIVED">Archived</option>
                        </select>
                      </div>

                      <div>
                        <label for="project-progress" class="block text-sm font-medium text-gray-700">Progress (%)</label>
                        <input
                          id="project-progress"
                          v-model.number="formData.progress"
                          type="number"
                          min="0"
                          max="100"
                          class="mt-1 focus:ring-blue-500 focus:border-blue-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md"
                          placeholder="0"
                        />
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
                  {{ loading ? 'Saving...' : (showCreateModal ? 'Create Project' : 'Update Project') }}
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

interface Project {
  id: number
  name: string
  description?: string
  departmentId: number
  managerId?: number
  status: 'PENDING' | 'ACTIVE' | 'COMPLETED' | 'ARCHIVED'
  progress?: number
  teamSize?: number
  startDate?: string
  endDate?: string
  createdAt?: string
  updatedAt?: string
}

interface Department {
  id: number
  name: string
  description?: string
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
const projects = ref<Project[]>([])
const departments = ref<Department[]>([])
const managers = ref<User[]>([])
const loading = ref(false)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentProject = ref<Project | null>(null)

// Stats
const stats = ref({
  totalProjects: 0,
  activeProjects: 0,
  totalMembers: 0,
  pendingProjects: 0
})

// Filters
const filters = ref({
  name: '',
  departmentId: '',
  managerId: '',
  status: ''
})

// Form data
const formData = ref({
  name: '',
  description: '',
  departmentId: '',
  managerId: '',
  status: 'PENDING' as Project['status'],
  progress: 0,
  startDate: '',
  endDate: ''
})

// Computed
const isAdmin = computed(() => authStore.user?.role === 'ADMIN')

// Methods
const loadProjects = async () => {
  try {
    loading.value = true
    // TODO: Replace with actual API call
    const mockProjects: Project[] = [
      {
        id: 1,
        name: 'FileHub Web Application',
        description: 'Complete file management system with web interface',
        departmentId: 1,
        managerId: 1,
        status: 'ACTIVE',
        progress: 75,
        teamSize: 8,
        startDate: '2024-01-01',
        endDate: '2024-06-30'
      },
      {
        id: 2,
        name: 'Mobile App Development',
        description: 'FileHub mobile application for iOS and Android',
        departmentId: 1,
        managerId: 3,
        status: 'PENDING',
        progress: 25,
        teamSize: 5,
        startDate: '2024-03-01',
        endDate: '2024-09-30'
      },
      {
        id: 3,
        name: 'Marketing Campaign Q2',
        description: 'Digital marketing campaign for product launch',
        departmentId: 2,
        managerId: 2,
        status: 'ACTIVE',
        progress: 60,
        teamSize: 6,
        startDate: '2024-04-01',
        endDate: '2024-06-30'
      }
    ]
    projects.value = mockProjects
    
    // Update stats
    stats.value.totalProjects = mockProjects.length
    stats.value.activeProjects = mockProjects.filter(p => p.status === 'ACTIVE').length
    stats.value.pendingProjects = mockProjects.filter(p => p.status === 'PENDING').length
    stats.value.totalMembers = mockProjects.reduce((sum, p) => sum + (p.teamSize || 0), 0)
  } catch (error) {
    console.error('Failed to load projects:', error)
  } finally {
    loading.value = false
  }
}

const loadDepartments = async () => {
  try {
    // TODO: Replace with actual API call
    const mockDepartments: Department[] = [
      { id: 1, name: 'Engineering', description: 'Software development' },
      { id: 2, name: 'Marketing', description: 'Marketing and promotion' },
      { id: 3, name: 'Design', description: 'UI/UX Design' }
    ]
    departments.value = mockDepartments
  } catch (error) {
    console.error('Failed to load departments:', error)
  }
}

const loadManagers = async () => {
  try {
    // TODO: Replace with actual API call
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

const getDepartmentName = (departmentId: number): string => {
  const dept = departments.value.find(d => d.id === departmentId)
  return dept ? dept.name : 'Unknown Department'
}

const getManagerName = (managerId?: number): string => {
  if (!managerId) return 'No manager assigned'
  const manager = managers.value.find(m => m.id === managerId)
  return manager ? manager.fullName : 'Unknown manager'
}

const getStatusColor = (status: Project['status']): string => {
  switch (status) {
    case 'ACTIVE':
      return 'bg-green-100 text-green-800'
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-800'
    case 'COMPLETED':
      return 'bg-blue-100 text-blue-800'
    case 'ARCHIVED':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const applyFilters = async () => {
  // TODO: Implement filtering logic
  await loadProjects()
}

const viewProject = (project: Project) => {
  // TODO: Navigate to project detail view
  router.push(`/admin/projects/${project.id}`)
}

const editProject = (project: Project) => {
  currentProject.value = project
  formData.value = {
    name: project.name,
    description: project.description || '',
    departmentId: project.departmentId.toString(),
    managerId: project.managerId?.toString() || '',
    status: project.status,
    progress: project.progress || 0,
    startDate: project.startDate || '',
    endDate: project.endDate || ''
  }
  showEditModal.value = true
}

const manageProjectTeam = (project: Project) => {
  // TODO: Navigate to project team management
  router.push(`/admin/projects/${project.id}/team`)
}

const updateProjectStatus = async (project: Project) => {
  // TODO: Show status update modal or implement quick status toggle
  console.log('Update status for project:', project.name)
}

const saveProject = async () => {
  try {
    loading.value = true
    
    if (showCreateModal.value) {
      // TODO: Implement create project API call
      const newProject: Project = {
        id: Math.max(...projects.value.map(p => p.id)) + 1,
        name: formData.value.name,
        description: formData.value.description,
        departmentId: parseInt(formData.value.departmentId),
        managerId: formData.value.managerId ? parseInt(formData.value.managerId) : undefined,
        status: formData.value.status,
        progress: formData.value.progress,
        teamSize: 0,
        startDate: formData.value.startDate,
        endDate: formData.value.endDate
      }
      projects.value.push(newProject)
    } else if (showEditModal.value && currentProject.value) {
      // TODO: Implement update project API call
      const index = projects.value.findIndex(p => p.id === currentProject.value!.id)
      if (index !== -1) {
        projects.value[index] = {
          ...projects.value[index],
          name: formData.value.name,
          description: formData.value.description,
          departmentId: parseInt(formData.value.departmentId),
          managerId: formData.value.managerId ? parseInt(formData.value.managerId) : undefined,
          status: formData.value.status,
          progress: formData.value.progress,
          startDate: formData.value.startDate,
          endDate: formData.value.endDate
        }
      }
    }
    
    closeModal()
    await loadProjects()
  } catch (error) {
    console.error('Failed to save project:', error)
  } finally {
    loading.value = false
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  currentProject.value = null
  formData.value = {
    name: '',
    description: '',
    departmentId: '',
    managerId: '',
    status: 'PENDING',
    progress: 0,
    startDate: '',
    endDate: ''
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
    loadProjects(),
    loadDepartments(),
    loadManagers()
  ])
})
</script>
