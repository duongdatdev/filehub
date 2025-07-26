<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75"></div>
      
      <div 
        class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-md sm:w-full sm:p-6"
        @click.stop
      >
        <form @submit.prevent="handleSubmit">
          <!-- Header -->
          <div class="mb-6">
            <h3 class="text-lg font-medium text-gray-900">
              Assign to Department
            </h3>
            <p class="text-sm text-gray-500 mt-1">
              Select a department and role for the user
            </p>
          </div>

          <!-- Department Selection -->
          <div class="mb-4">
            <label for="department-select" class="block text-sm font-medium text-gray-700 mb-2">
              Department
            </label>
            <select
              id="department-select"
              v-model="selectedDepartmentId"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50"
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

          <!-- Role Selection -->
          <div class="mb-6">
            <label for="role-select" class="block text-sm font-medium text-gray-700 mb-2">
              Role
            </label>
            <select
              id="role-select"
              v-model="selectedRole"
              required
              :disabled="loading"
              class="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 disabled:opacity-50"
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
              :disabled="loading || !selectedDepartmentId"
              class="inline-flex items-center px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <svg v-if="loading" class="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              {{ loading ? 'Assigning...' : 'Assign' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'

interface Props {
  isOpen: boolean
  userId: number | null
}

interface Emits {
  (e: 'close'): void
  (e: 'assigned'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const adminStore = useAdminStore()
const selectedDepartmentId = ref<number | null>(null)
const selectedRole = ref('MEMBER')

// Computed properties
const loading = computed(() => adminStore.userAssignmentLoading)
const availableDepartments = computed(() => adminStore.availableDepartments)

// Watch for modal open/close to reset form
watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    selectedDepartmentId.value = null
    selectedRole.value = 'MEMBER'
  }
})

// Methods
const closeModal = () => {
  emit('close')
}

const handleSubmit = async () => {
  if (!props.userId || !selectedDepartmentId.value) return

  const success = await adminStore.assignUserToDepartment(
    props.userId,
    selectedDepartmentId.value,
    selectedRole.value
  )

  if (success) {
    emit('assigned')
  }
}
</script>
