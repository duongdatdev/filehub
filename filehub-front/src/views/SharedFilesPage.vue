<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Shared Files</h1>
        <p class="mt-2 text-gray-600">Access files from your departments and projects</p>
      </div>

      <!-- View Selector -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div class="flex flex-wrap gap-4 items-center">
          <div class="flex rounded-md shadow-sm">
            <button
              @click="currentView = 'all'"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-l-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500',
                currentView === 'all' ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 hover:bg-gray-50'
              ]"
            >
              All Accessible Files
            </button>
            <button
              @click="currentView = 'department'"
              :class="[
                'px-4 py-2 text-sm font-medium border-t border-b border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500',
                currentView === 'department' ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 hover:bg-gray-50'
              ]"
            >
              By Department
            </button>
            <button
              @click="currentView = 'project'"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-r-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500',
                currentView === 'project' ? 'bg-blue-600 text-white border-blue-600' : 'bg-white text-gray-700 hover:bg-gray-50'
              ]"
            >
              By Project
            </button>
          </div>

          <!-- Department/Project Selector -->
          <div v-if="currentView === 'department'" class="flex items-center space-x-2">
            <label class="text-sm font-medium text-gray-700">Department:</label>
            <select
              v-model="selectedDepartmentId"
              @change="onDepartmentChange"
              class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="">Select Department</option>
              <option
                v-for="dept in userDepartments"
                :key="dept.id"
                :value="dept.id"
              >
                {{ dept.name }}
              </option>
            </select>
          </div>

          <div v-if="currentView === 'project'" class="flex items-center space-x-2">
            <label class="text-sm font-medium text-gray-700">Project:</label>
            <select
              v-model="selectedProjectId"
              @change="onProjectChange"
              class="rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="">Select Project</option>
              <option
                v-for="project in userProjects"
                :key="project.id"
                :value="project.id"
              >
                {{ project.name }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <!-- Filters -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <!-- Search by filename -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Search Files</label>
            <input
              v-model="filters.filename"
              type="text"
              placeholder="Enter filename..."
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
              @input="debouncedSearch"
            />
          </div>

          <!-- File Type Filter -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">File Type</label>
            <select
              v-model="filters.fileTypeId"
              @change="applyFilters"
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="">All Types</option>
              <option
                v-for="type in fileTypes"
                :key="type.id"
                :value="type.id"
              >
                {{ type.name }}
              </option>
            </select>
          </div>

          <!-- Content Type Filter -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Content Type</label>
            <select
              v-model="filters.contentType"
              @change="applyFilters"
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="">All Types</option>
              <option value="image/">Images</option>
              <option value="application/pdf">PDF</option>
              <option value="application/">Documents</option>
              <option value="video/">Videos</option>
            </select>
          </div>

          <!-- Sort -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Sort By</label>
            <select
              v-model="sortBy"
              @change="applyFilters"
              class="w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
            >
              <option value="uploadedAt">Upload Date</option>
              <option value="originalFilename">Filename</option>
              <option value="fileSize">File Size</option>
              <option value="downloadCount">Downloads</option>
            </select>
          </div>
        </div>

        <div class="mt-4 flex justify-between items-center">
          <div class="flex items-center space-x-4">
            <button
              @click="clearFilters"
              class="text-sm text-gray-600 hover:text-gray-900"
            >
              Clear Filters
            </button>
            <div class="flex items-center space-x-2">
              <label class="text-sm text-gray-700">Sort Order:</label>
              <button
                @click="toggleSortDirection"
                class="flex items-center space-x-1 text-sm text-blue-600 hover:text-blue-800"
              >
                <span>{{ sortDirection === 'DESC' ? 'Newest First' : 'Oldest First' }}</span>
                <svg
                  class="w-4 h-4"
                  :class="{ 'rotate-180': sortDirection === 'ASC' }"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>
            </div>
          </div>
          
          <div class="text-sm text-gray-600">
            Showing {{ files.length }} of {{ totalElements }} files
          </div>
        </div>
      </div>

      <!-- Files Grid -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <p class="mt-2 text-gray-600">Loading files...</p>
      </div>

      <div v-else-if="files.length === 0" class="text-center py-12">
        <svg
          class="mx-auto h-12 w-12 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
          />
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">No files found</h3>
        <p class="mt-1 text-sm text-gray-500">
          {{ getNoFilesMessage() }}
        </p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="file in files"
          :key="file.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow"
        >
          <!-- File Header -->
          <div class="p-4 border-b border-gray-200">
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <h3 class="text-sm font-medium text-gray-900 truncate">
                  {{ file.title || file.originalFilename }}
                </h3>
                <p class="text-xs text-gray-500 mt-1">
                  by {{ file.uploaderName }} • {{ formatDate(file.uploadedAt) }}
                </p>
              </div>
              <div class="flex items-center space-x-1 ml-2">
                <!-- Visibility Badge -->
                <span
                  :class="getVisibilityBadgeClass(file.visibility)"
                  class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                >
                  {{ file.visibility }}
                </span>
              </div>
            </div>
          </div>

          <!-- File Content -->
          <div class="p-4">
            <!-- File Info -->
            <div class="space-y-2 text-sm text-gray-600">
              <div class="flex justify-between">
                <span>Size:</span>
                <span>{{ formatFileSize(file.fileSize) }}</span>
              </div>
              <div class="flex justify-between">
                <span>Type:</span>
                <span>{{ file.fileTypeName || getFileTypeFromContent(file.contentType) }}</span>
              </div>
              <div class="flex justify-between">
                <span>Downloads:</span>
                <span>{{ file.downloadCount }}</span>
              </div>
              <div v-if="file.departmentName" class="flex justify-between">
                <span>Department:</span>
                <span class="truncate">{{ file.departmentName }}</span>
              </div>
              <div v-if="file.projectName" class="flex justify-between">
                <span>Project:</span>
                <span class="truncate">{{ file.projectName }}</span>
              </div>
            </div>

            <!-- Description -->
            <div v-if="file.description" class="mt-3">
              <p class="text-sm text-gray-700 line-clamp-2">{{ file.description }}</p>
            </div>

            <!-- Tags -->
            <div v-if="file.tags && file.tags.length > 0" class="mt-3">
              <div class="flex flex-wrap gap-1">
                <span
                  v-for="tag in file.tags.slice(0, 3)"
                  :key="tag"
                  class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                >
                  {{ tag }}
                </span>
                <span
                  v-if="file.tags.length > 3"
                  class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800"
                >
                  +{{ file.tags.length - 3 }}
                </span>
              </div>
            </div>
          </div>

          <!-- File Actions -->
          <div class="px-4 py-3 bg-gray-50 border-t border-gray-200 rounded-b-lg">
            <div class="flex justify-between items-center">
              <div class="flex space-x-2">
                <button
                  @click="downloadFile(file)"
                  class="inline-flex items-center px-3 py-1 border border-transparent text-xs font-medium rounded text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-4-4m4 4l4-4m5 10v1a2 2 0 01-2 2H5a2 2 0 01-2-2v-1" />
                  </svg>
                  Download
                </button>
                <button
                  @click="viewFile(file)"
                  class="inline-flex items-center px-3 py-1 border border-gray-300 text-xs font-medium rounded text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  <svg class="w-3 h-3 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  View
                </button>
              </div>
              
              <!-- Owner indicator -->
              <div v-if="file.uploaderId === currentUserId" class="text-xs text-green-600 font-medium">
                Your File
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="mt-8 flex justify-center">
        <nav class="flex items-center space-x-2">
          <button
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Previous
          </button>
          
          <span class="px-3 py-2 text-sm text-gray-700">
            Page {{ currentPage + 1 }} of {{ totalPages }}
          </span>
          
          <button
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { fileApi, type FileResponse, type FileFilters } from '@/services/fileApi'
import departmentApi, { type DepartmentResponse } from '@/services/departmentApi'
import projectApi, { type ProjectResponse } from '@/services/projectApi'
import fileTypeApi, { type FileType as FileTypeResponse } from '@/services/fileTypeApi'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Reactive data
const currentView = ref<'all' | 'department' | 'project'>('all')
const files = ref<FileResponse[]>([])
const loading = ref(false)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(12)

// User data
const userDepartments = ref<DepartmentResponse[]>([])
const userProjects = ref<ProjectResponse[]>([])
const fileTypes = ref<FileTypeResponse[]>([])

// Selections
const selectedDepartmentId = ref<number | ''>('')
const selectedProjectId = ref<number | ''>('')

// Filters
const filters = reactive<FileFilters>({
  filename: '',
  departmentCategoryId: undefined,
  departmentId: undefined,
  projectId: undefined,
  fileTypeId: undefined,
  contentType: '',
  page: 0,
  size: 12
})

const sortBy = ref('uploadedAt')
const sortDirection = ref<'ASC' | 'DESC'>('DESC')

// Computed
const currentUserId = computed(() => authStore.user?.id)

// Debounced search
let searchTimeout: NodeJS.Timeout
const debouncedSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    applyFilters()
  }, 500)
}

// Watchers
watch(currentView, () => {
  selectedDepartmentId.value = ''
  selectedProjectId.value = ''
  clearFilters()
})

// Methods
const loadSharedFiles = async () => {
  loading.value = true
  try {
    const params = {
      ...filters,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: sortBy.value,
      sortDirection: sortDirection.value
    }

    let response
    if (currentView.value === 'all') {
      response = await fileApi.getSharedFiles(params)
    } else if (currentView.value === 'department' && selectedDepartmentId.value) {
      response = await fileApi.getSharedFilesByDepartment(selectedDepartmentId.value as number, params)
    } else if (currentView.value === 'project' && selectedProjectId.value) {
      response = await fileApi.getSharedFilesByProject(selectedProjectId.value as number, params)
    } else {
      files.value = []
      totalPages.value = 0
      totalElements.value = 0
      return
    }

    if (response.success && response.data) {
      files.value = response.data.content
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements
    }
  } catch (error) {
    console.error('Error loading shared files:', error)
  } finally {
    loading.value = false
  }
}

const loadUserData = async () => {
  try {
    // Load user's departments and projects
    const [deptResponse, projResponse, typeResponse] = await Promise.all([
      departmentApi.getUserDepartments(),
      projectApi.getUserProjects(),
      fileTypeApi.getAllFileTypes()
    ])

    if (deptResponse.data?.data) {
      userDepartments.value = deptResponse.data.data
    }

    if (projResponse.data?.data) {
      userProjects.value = projResponse.data.data
    }

    if (typeResponse.success && typeResponse.data) {
      fileTypes.value = typeResponse.data
    }
  } catch (error) {
    console.error('Error loading user data:', error)
  }
}

const onDepartmentChange = () => {
  currentPage.value = 0
  loadSharedFiles()
}

const onProjectChange = () => {
  currentPage.value = 0
  loadSharedFiles()
}

const applyFilters = () => {
  currentPage.value = 0
  loadSharedFiles()
}

const clearFilters = () => {
  filters.filename = ''
  filters.departmentCategoryId = undefined
  filters.departmentId = undefined
  filters.projectId = undefined
  filters.fileTypeId = undefined
  filters.contentType = ''
  currentPage.value = 0
  loadSharedFiles()
}

const toggleSortDirection = () => {
  sortDirection.value = sortDirection.value === 'DESC' ? 'ASC' : 'DESC'
  applyFilters()
}

const goToPage = (page: number) => {
  currentPage.value = page
  loadSharedFiles()
}

const downloadFile = async (file: FileResponse) => {
  try {
    const blob = await fileApi.downloadFile(file.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = file.originalFilename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Error downloading file:', error)
  }
}

const viewFile = (file: FileResponse) => {
  // Open file details or preview modal
  console.log('View file:', file)
}

const getNoFilesMessage = () => {
  if (currentView.value === 'department' && !selectedDepartmentId.value) {
    return 'Please select a department to view files.'
  }
  if (currentView.value === 'project' && !selectedProjectId.value) {
    return 'Please select a project to view files.'
  }
  return 'No files found matching your criteria.'
}

const getVisibilityBadgeClass = (visibility: string) => {
  switch (visibility) {
    case 'PUBLIC':
      return 'bg-green-100 text-green-800'
    case 'DEPARTMENT':
      return 'bg-blue-100 text-blue-800'
    case 'PROJECT':
      return 'bg-purple-100 text-purple-800'
    case 'PRIVATE':
      return 'bg-gray-100 text-gray-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const getFileTypeFromContent = (contentType: string): string => {
  if (contentType.startsWith('image/')) return 'Image'
  if (contentType.startsWith('video/')) return 'Video'
  if (contentType.startsWith('audio/')) return 'Audio'
  if (contentType === 'application/pdf') return 'PDF'
  if (contentType.includes('document') || contentType.includes('word')) return 'Document'
  if (contentType.includes('spreadsheet') || contentType.includes('excel')) return 'Spreadsheet'
  return 'File'
}

// Lifecycle
onMounted(async () => {
  await loadUserData()
  await loadSharedFiles()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
