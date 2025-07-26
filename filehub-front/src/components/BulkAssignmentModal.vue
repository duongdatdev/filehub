<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75"></div>
      
      <div 
        class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full sm:p-6"
        @click.stop
      >
        <form @submit.prevent="handleSubmit">
          <!-- Header -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900">
              Bulk Assignment
            </h3>
            <p class="text-sm text-gray-500 mt-1">
              Assign {{ selectedUserIds.length }} selected {{ selectedUserIds.length === 1 ? 'user' : 'users' }} to departments or projects
            </p>
          </div>

          <!-- Operation Type -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Operation Type
            </label>
            <select
              v-model="operationType"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select operation...</option>
              <option value="ADD">Add Assignment</option>
              <option value="REMOVE">Remove Assignment</option>
              <option value="UPDATE_ROLE">Update Role</option>
            </select>
          </div>

          <!-- Assignment Type -->
          <div v-if="operationType" class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Assignment Type
            </label>
            <div class="space-y-2">
              <label class="flex items-center">
                <input
                  v-model="assignmentType"
                  value="department"
                  type="radio"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                  :disabled="loading"
                >
                <span class="ml-2 text-sm text-gray-700">Department</span>
              </label>
              <label class="flex items-center">
                <input
                  v-model="assignmentType"
                  value="project"
                  type="radio"
                  class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                  :disabled="loading"
                >
                <span class="ml-2 text-sm text-gray-700">Project</span>
              </label>
            </div>
          </div>

          <!-- Department Selection -->
          <div v-if="assignmentType === 'department'" class="mb-4">
            <label for="department-select" class="block text-sm font-medium text-gray-700 mb-2">
              Department
            </label>
            <select
              id="department-select"
              v-model="selectedDepartmentId"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select a department...</option>
              <option 
                v-for="department in availableDepartments" 
                :key="department.id" 
                :value="department.id"
              >
                {{ department.name }}
              </option>
            </select>
          </div>

          <!-- Project Selection -->
          <div v-if="assignmentType === 'project'" class="mb-4">
            <label for="project-select" class="block text-sm font-medium text-gray-700 mb-2">
              Project
            </label>
            <select
              id="project-select"
              v-model="selectedProjectId"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Select a project...</option>
              <option 
                v-for="project in availableProjects" 
                :key="project.id" 
                :value="project.id"
              >
                {{ project.name }}
              </option>
            </select>
          </div>

          <!-- Role Selection (for ADD and UPDATE_ROLE operations) -->
          <div v-if="(operationType === 'ADD' || operationType === 'UPDATE_ROLE') && assignmentType" class="mb-6">
            <label for="role-select" class="block text-sm font-medium text-gray-700 mb-2">
              Role
            </label>
            <select
              id="role-select"
              v-model="selectedRole"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="MEMBER">Member</option>
              <option value="LEAD">Lead</option>
              <option value="MANAGER">Manager</option>
            </select>
          </div>

          <!-- Footer -->
          <div class="flex justify-end space-x-3">
            <button
              type="button"
              @click="closeModal"
              :disabled="loading"
              class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="loading || !isFormValid"
              class="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ loading ? 'Processing...' : getSubmitButtonText() }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useAdminStore } from '@/stores/admin'
import type { BatchUserAssignmentRequest } from '@/services/adminApi'

interface Props {
  isOpen: boolean
  selectedUserIds: number[]
}

interface Emits {
  (e: 'close'): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const adminStore = useAdminStore()
const operationType = ref<'ADD' | 'REMOVE' | 'UPDATE_ROLE' | ''>('')
const assignmentType = ref<'department' | 'project' | ''>('')
const selectedDepartmentId = ref<number | null>(null)
const selectedProjectId = ref<number | null>(null)
const selectedRole = ref('MEMBER')

// Computed properties
const loading = computed(() => adminStore.bulkActionLoading)
const availableDepartments = computed(() => adminStore.availableDepartments)
const availableProjects = computed(() => adminStore.availableProjects)

const isFormValid = computed(() => {
  if (!operationType.value || !assignmentType.value) return false
  
  if (assignmentType.value === 'department' && !selectedDepartmentId.value) return false
  if (assignmentType.value === 'project' && !selectedProjectId.value) return false
  
  if ((operationType.value === 'ADD' || operationType.value === 'UPDATE_ROLE') && !selectedRole.value) return false
  
  return true
})

// Watch for modal open/close to reset form and load data
watch(() => props.isOpen, async (isOpen) => {
  if (isOpen) {
    // Load available departments and projects
    await Promise.all([
      adminStore.fetchAvailableDepartments(),
      adminStore.fetchAvailableProjects()
    ])
  } else {
    // Reset form
    operationType.value = ''
    assignmentType.value = ''
    selectedDepartmentId.value = null
    selectedProjectId.value = null
    selectedRole.value = 'MEMBER'
  }
})

// Methods
const closeModal = () => {
  emit('close')
}

const getSubmitButtonText = () => {
  switch (operationType.value) {
    case 'ADD': return 'Add Assignment'
    case 'REMOVE': return 'Remove Assignment'
    case 'UPDATE_ROLE': return 'Update Role'
    default: return 'Execute'
  }
}

const handleSubmit = async () => {
  if (!isFormValid.value) return

  const request: BatchUserAssignmentRequest = {
    userIds: props.selectedUserIds,
    operation: operationType.value as 'ADD' | 'REMOVE' | 'UPDATE_ROLE',
    role: selectedRole.value
  }

  if (assignmentType.value === 'department' && selectedDepartmentId.value) {
    request.departmentId = selectedDepartmentId.value
  } else if (assignmentType.value === 'project' && selectedProjectId.value) {
    request.projectId = selectedProjectId.value
  }

  const success = await adminStore.batchUpdateAssignments(request)
  
  if (success) {
    emit('success')
  }
}

// Load data when component mounts if modal is already open
onMounted(async () => {
  if (props.isOpen) {
    await Promise.all([
      adminStore.fetchAvailableDepartments(),
      adminStore.fetchAvailableProjects()
    ])
  }
})
</script>
