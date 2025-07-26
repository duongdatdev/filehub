<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" @click="closeModal">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity bg-gray-500 bg-opacity-75"></div>
      
      <div 
        class="inline-block align-bottom bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full sm:p-6"
        @click.stop
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-medium text-gray-900">
              Manage Assignments for {{ userDetails?.fullName || 'User' }}
            </h3>
            <p class="text-sm text-gray-500">
              {{ userDetails?.email }}
            </p>
          </div>
          <button
            @click="closeModal"
            class="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="flex items-center justify-center py-12">
          <svg class="animate-spin h-8 w-8 text-blue-600" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <span class="ml-2 text-gray-600">Loading user details...</span>
        </div>

        <!-- Content -->
        <div v-else-if="userDetails" class="space-y-8">
          <!-- Department Assignments -->
          <div>
            <div class="flex items-center justify-between mb-4">
              <h4 class="text-md font-medium text-gray-900">Department Assignments</h4>
              <button
                @click="showAddDepartment = true"
                class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded text-blue-700 bg-blue-100 hover:bg-blue-200"
              >
                <svg class="h-4 w-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Add Department
              </button>
            </div>

            <!-- Department List -->
            <div v-if="userDetails.departments?.length > 0" class="space-y-2">
              <div
                v-for="department in userDetails.departments"
                :key="department.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div class="flex items-center space-x-3">
                  <div class="flex-shrink-0">
                    <div class="h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center">
                      <svg class="h-4 w-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-2M5 21h2m0 0h2" />
                      </svg>
                    </div>
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ department.name }}</p>
                    <p class="text-sm text-gray-500">Role: {{ department.role }}</p>
                  </div>
                </div>
                <div class="flex items-center space-x-2">
                  <select
                    :value="department.role"
                    @change="updateDepartmentRole(department.id, ($event.target as HTMLSelectElement).value)"
                    :disabled="assignmentLoading"
                    class="text-xs border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="MEMBER">Member</option>
                    <option value="LEAD">Lead</option>
                    <option value="MANAGER">Manager</option>
                  </select>
                  <button
                    @click="removeDepartment(department.id)"
                    :disabled="assignmentLoading"
                    class="text-red-600 hover:text-red-800 disabled:opacity-50"
                  >
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-6 text-gray-500">
              <p>No department assignments</p>
            </div>
          </div>

          <!-- Project Assignments -->
          <div>
            <div class="flex items-center justify-between mb-4">
              <h4 class="text-md font-medium text-gray-900">Project Assignments</h4>
              <button
                @click="showAddProject = true"
                class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded text-blue-700 bg-blue-100 hover:bg-blue-200"
              >
                <svg class="h-4 w-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Add Project
              </button>
            </div>

            <!-- Project List -->
            <div v-if="userDetails.projects?.length > 0" class="space-y-2">
              <div
                v-for="project in userDetails.projects"
                :key="project.id"
                class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
              >
                <div class="flex items-center space-x-3">
                  <div class="flex-shrink-0">
                    <div class="h-8 w-8 bg-green-100 rounded-full flex items-center justify-center">
                      <svg class="h-4 w-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                      </svg>
                    </div>
                  </div>
                  <div>
                    <p class="font-medium text-gray-900">{{ project.name }}</p>
                    <p class="text-sm text-gray-500">Role: {{ project.role }}</p>
                  </div>
                </div>
                <div class="flex items-center space-x-2">
                  <select
                    :value="project.role"
                    @change="updateProjectRole(project.id, ($event.target as HTMLSelectElement).value)"
                    :disabled="assignmentLoading"
                    class="text-xs border-gray-300 rounded focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="MEMBER">Member</option>
                    <option value="LEAD">Lead</option>
                    <option value="MANAGER">Manager</option>
                  </select>
                  <button
                    @click="removeProject(project.id)"
                    :disabled="assignmentLoading"
                    class="text-red-600 hover:text-red-800 disabled:opacity-50"
                  >
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-6 text-gray-500">
              <p>No project assignments</p>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="mt-6 flex justify-end">
          <button
            @click="closeModal"
            class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Add Department Modal -->
  <AddDepartmentAssignmentModal
    :is-open="showAddDepartment"
    :user-id="userId"
    @close="showAddDepartment = false"
    @assigned="handleDepartmentAssigned"
  />

  <!-- Add Project Modal -->
  <AddProjectAssignmentModal
    :is-open="showAddProject"
    :user-id="userId"
    @close="showAddProject = false"
    @assigned="handleProjectAssigned"
  />
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAdminStore } from '@/stores/admin'
import AddDepartmentAssignmentModal from './AddDepartmentAssignmentModal.vue'
import AddProjectAssignmentModal from './AddProjectAssignmentModal.vue'

interface Props {
  isOpen: boolean
  userId: number | null
}

interface Emits {
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const adminStore = useAdminStore()
const showAddDepartment = ref(false)
const showAddProject = ref(false)

// Computed properties
const loading = computed(() => adminStore.userAssignmentLoading)
const assignmentLoading = computed(() => adminStore.userAssignmentLoading)
const userDetails = computed(() => adminStore.selectedUserDetails)

// Watch for userId changes to fetch user details
watch(() => props.userId, async (newUserId) => {
  if (newUserId && props.isOpen) {
    await adminStore.fetchUserDetails(newUserId)
  }
})

// Watch for modal open/close
watch(() => props.isOpen, async (isOpen) => {
  if (isOpen && props.userId) {
    await adminStore.fetchUserDetails(props.userId)
    // Also fetch available departments and projects for the add modals
    await Promise.all([
      adminStore.fetchAvailableDepartments(),
      adminStore.fetchAvailableProjects()
    ])
  } else if (!isOpen) {
    adminStore.clearUserDetails()
  }
})

// Methods
const closeModal = () => {
  emit('close')
}

const updateDepartmentRole = async (departmentId: number, role: string) => {
  if (!props.userId) return
  
  const success = await adminStore.updateUserDepartmentRole(props.userId, departmentId, role)
  if (!success) {
    // Handle error - could show a toast notification
    console.error('Failed to update department role')
  }
}

const updateProjectRole = async (projectId: number, role: string) => {
  if (!props.userId) return
  
  const success = await adminStore.updateUserProjectRole(props.userId, projectId, role)
  if (!success) {
    // Handle error - could show a toast notification
    console.error('Failed to update project role')
  }
}

const removeDepartment = async (departmentId: number) => {
  if (!props.userId) return
  
  const success = await adminStore.removeUserFromDepartment(props.userId, departmentId)
  if (!success) {
    // Handle error - could show a toast notification
    console.error('Failed to remove user from department')
  }
}

const removeProject = async (projectId: number) => {
  if (!props.userId) return
  
  const success = await adminStore.removeUserFromProject(props.userId, projectId)
  if (!success) {
    // Handle error - could show a toast notification
    console.error('Failed to remove user from project')
  }
}

const handleDepartmentAssigned = () => {
  showAddDepartment.value = false
  // User details will be automatically refreshed by the store
}

const handleProjectAssigned = () => {
  showAddProject.value = false
  // User details will be automatically refreshed by the store
}
</script>
