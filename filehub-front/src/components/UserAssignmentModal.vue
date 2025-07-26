<template>
  <div class="fixed inset-0 z-50 overflow-y-auto" v-if="isOpen">
    <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <!-- Background overlay -->
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="closeModal"></div>

      <!-- Modal panel -->
      <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
        <form @submit.prevent="handleSubmit">
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div class="sm:flex sm:items-start">
              <div class="mt-3 text-center sm:mt-0 sm:text-left w-full">
                <h3 class="text-lg leading-6 font-medium text-gray-900 mb-4">
                  {{ mode === 'single' ? 'Assign User' : 'Bulk Assignment' }}
                </h3>

                <!-- User Selection (for single mode) -->
                <div v-if="mode === 'single'" class="mb-6">
                  <label for="user-select" class="block text-sm font-medium text-gray-700 mb-2">
                    Select User
                  </label>
                  <select
                    id="user-select"
                    v-model="selectedUserId"
                    required
                    class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="">Choose a user...</option>
                    <option v-for="user in availableUsers" :key="user.id" :value="user.id">
                      {{ user.fullName }} ({{ user.username }})
                    </option>
                  </select>
                </div>

                <!-- Multi-user selection (for bulk mode) -->
                <div v-if="mode === 'bulk'" class="mb-6">
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Select Users
                  </label>
                  <div class="max-h-40 overflow-y-auto border border-gray-300 rounded-md">
                    <div v-for="user in availableUsers" :key="user.id" class="flex items-center px-3 py-2 hover:bg-gray-50">
                      <input
                        :id="`user-${user.id}`"
                        v-model="selectedUserIds"
                        :value="user.id"
                        type="checkbox"
                        class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      >
                      <label :for="`user-${user.id}`" class="ml-3 text-sm text-gray-700 cursor-pointer">
                        {{ user.fullName }} ({{ user.username }})
                        <span v-if="user.role === 'ADMIN'" class="text-xs text-blue-600 font-medium">Admin</span>
                        <span v-else-if="user.role === 'MANAGER'" class="text-xs text-green-600 font-medium">Manager</span>
                      </label>
                    </div>
                  </div>
                  <p class="mt-1 text-sm text-gray-500">{{ selectedUserIds.length }} user(s) selected</p>
                </div>

                <!-- Department Selection -->
                <div class="mb-6">
                  <label for="department-select" class="block text-sm font-medium text-gray-700 mb-2">
                    Assign to Department
                  </label>
                  <select
                    id="department-select"
                    v-model="selectedDepartmentId"
                    class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="">No department assignment</option>
                    <option v-for="department in departments" :key="department.id" :value="department.id">
                      {{ department.name }}
                    </option>
                  </select>
                </div>

                <!-- Project Selection -->
                <div class="mb-6">
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    Assign to Projects
                  </label>
                  <div class="max-h-32 overflow-y-auto border border-gray-300 rounded-md">
                    <div v-for="project in filteredProjects" :key="project.id" class="flex items-center px-3 py-2 hover:bg-gray-50">
                      <input
                        :id="`project-${project.id}`"
                        v-model="selectedProjectIds"
                        :value="project.id"
                        type="checkbox"
                        class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      >
                      <label :for="`project-${project.id}`" class="ml-3 text-sm text-gray-700 cursor-pointer flex-1">
                        <div>{{ project.name }}</div>
                        <div class="text-xs text-gray-500">{{ project.status }}</div>
                      </label>
                    </div>
                  </div>
                  <p class="mt-1 text-sm text-gray-500">{{ selectedProjectIds.length }} project(s) selected</p>
                </div>

                <!-- Assignment Summary -->
                <div v-if="hasAssignments" class="bg-blue-50 border border-blue-200 rounded-md p-3 mb-4">
                  <h4 class="text-sm font-medium text-blue-800 mb-2">Assignment Summary:</h4>
                  <ul class="text-sm text-blue-700 space-y-1">
                    <li v-if="selectedDepartmentId">
                      Department: {{ departments.find(d => d.id === selectedDepartmentId)?.name }}
                    </li>
                    <li v-if="selectedProjectIds.length > 0">
                      Projects: {{ selectedProjectIds.length }} selected
                    </li>
                    <li>
                      Users: {{ mode === 'single' ? 1 : selectedUserIds.length }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button
              type="submit"
              :disabled="!canSubmit || isLoading"
              class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg v-if="isLoading" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ isLoading ? 'Assigning...' : 'Assign Users' }}
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
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { adminApi, type Department, type Project } from '@/services/adminApi'
import { useNotificationStore } from '@/stores/notification'
import type { User } from '@/services/api'

interface Props {
  isOpen: boolean
  mode: 'single' | 'bulk'
  preselectedUsers?: User[]
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const notificationStore = useNotificationStore()

// Form data
const selectedUserId = ref<number | null>(null)
const selectedUserIds = ref<number[]>([])
const selectedDepartmentId = ref<number | null>(null)
const selectedProjectIds = ref<number[]>([])

// Data
const availableUsers = ref<User[]>([])
const departments = ref<Department[]>([])
const projects = ref<Project[]>([])

// Loading states
const isLoading = ref(false)
const isLoadingData = ref(false)

// Computed
const filteredProjects = computed(() => {
  if (!selectedDepartmentId.value) return projects.value
  return projects.value.filter(project => project.departmentId === selectedDepartmentId.value)
})

const hasAssignments = computed(() => {
  return selectedDepartmentId.value || selectedProjectIds.value.length > 0
})

const canSubmit = computed(() => {
  const hasUsers = props.mode === 'single' ? selectedUserId.value : selectedUserIds.value.length > 0
  return hasUsers && hasAssignments.value && !isLoading.value
})

// Methods
const loadData = async () => {
  isLoadingData.value = true
  try {
    const [usersResponse, departmentsResponse, projectsResponse] = await Promise.all([
      adminApi.getUsers({ isActive: true, size: 100 }),
      adminApi.getDepartments(),
      adminApi.getProjects()
    ])

    if (usersResponse.success) {
      availableUsers.value = usersResponse.data.content
    }
    if (departmentsResponse.success) {
      departments.value = departmentsResponse.data
    }
    if (projectsResponse.success) {
      projects.value = projectsResponse.data
    }
  } catch (error) {
    console.error('Failed to load assignment data:', error)
    notificationStore.error('Failed to load data', 'Could not load users, departments, or projects.')
  } finally {
    isLoadingData.value = false
  }
}

const handleSubmit = async () => {
  if (!canSubmit.value) return

  isLoading.value = true
  try {
    const userIds = props.mode === 'single' 
      ? [selectedUserId.value!] 
      : selectedUserIds.value

    // Perform assignments
    const assignments = []
    for (const userId of userIds) {
      // Assign to department if selected
      if (selectedDepartmentId.value) {
        await adminApi.assignUserToDepartment(userId, selectedDepartmentId.value)
      }

      // Assign to projects if selected
      for (const projectId of selectedProjectIds.value) {
        await adminApi.assignUserToProject(userId, projectId)
      }
      
      assignments.push({ userId, departmentId: selectedDepartmentId.value, projectIds: selectedProjectIds.value })
    }

    const userCount = userIds.length
    const assignmentCount = (selectedDepartmentId.value ? userCount : 0) + (selectedProjectIds.value.length * userCount)

    notificationStore.success(
      'Assignment Complete',
      `Successfully completed ${assignmentCount} assignment(s) for ${userCount} user(s).`
    )

    emit('success')
    closeModal()
  } catch (error) {
    console.error('Assignment failed:', error)
    notificationStore.error(
      'Assignment Failed',
      error instanceof Error ? error.message : 'Failed to assign users.'
    )
  } finally {
    isLoading.value = false
  }
}

const closeModal = () => {
  // Reset form
  selectedUserId.value = null
  selectedUserIds.value = []
  selectedDepartmentId.value = null
  selectedProjectIds.value = []
  
  emit('close')
}

// Watch for preselected users
watch(() => props.preselectedUsers, (users) => {
  if (users && users.length > 0) {
    if (props.mode === 'single') {
      selectedUserId.value = users[0].id
    } else {
      selectedUserIds.value = users.map(u => u.id)
    }
  }
}, { immediate: true })

// Load data when modal opens
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    loadData()
  }
})

onMounted(() => {
  if (props.isOpen) {
    loadData()
  }
})
</script>
