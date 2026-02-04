<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Header -->
      <div class="mb-8">
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 class="text-3xl font-bold text-gray-900">My Files</h1>
            <p class="mt-2 text-gray-600">Upload and manage your files with Google Drive integration</p>
          </div>
          <div class="mt-4 sm:mt-0">
            <router-link
              to="/files/shared"
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.684 13.342C8.886 12.938 9 12.482 9 12c0-.482-.114-.938-.316-1.342m0 2.684a3 3 0 110-2.684m0 2.684l6.632 3.316m-6.632-6l6.632-3.316m0 0a3 3 0 105.367-2.684 3 3 0 00-5.367 2.684zm0 9.316a3 3 0 105.367 2.684 3 3 0 00-5.367-2.684z" />
              </svg>
              View Shared Files
            </router-link>
          </div>
        </div>
      </div>

      <!-- Upload Section -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-8">
        <h2 class="text-xl font-semibold text-gray-900 mb-4">Upload Files</h2>
        
        <!-- Drag and Drop Zone -->
        <div
          class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center transition-colors"
          :class="{
            'border-blue-400 bg-blue-50': isDragOver,
            'hover:border-gray-400': !isDragOver
          }"
          @dragover.prevent="handleDragOver"
          @dragleave.prevent="handleDragLeave"
          @drop.prevent="handleDrop"
          @click="triggerFileInput"
        >
          <div class="space-y-4">
            <div class="flex justify-center">
              <svg
                class="w-12 h-12 text-gray-400"
                :class="{ 'text-blue-500': isDragOver }"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                />
              </svg>
            </div>
            <div>
              <p class="text-lg font-medium text-gray-900">
                Drop files here or click to browse
              </p>
              <p class="text-sm text-gray-500 mt-1">
                Supports: PDF, DOC, DOCX, TXT, JPG, PNG, MP4, ZIP and more
              </p>
              <p class="text-xs text-gray-400 mt-1">
                Maximum file size: 2GB
              </p>
            </div>
          </div>
        </div>

        <!-- Hidden File Input -->
        <input
          ref="fileInput"
          type="file"
          multiple
          class="hidden"
          @change="handleFileSelect"
          :accept="allowedFileTypes"
        />

        <!-- Upload Form -->
        <div v-if="selectedFiles.length > 0" class="mt-6">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Selected Files</h3>
          
          <div class="space-y-4">
            <div
              v-for="(file, index) in selectedFiles"
              :key="index"
              class="border border-gray-200 rounded-lg p-4"
            >
              <div class="flex items-start justify-between">
                <div class="flex-1">
                  <h4 class="font-medium text-gray-900">{{ file.name }}</h4>
                  <p class="text-sm text-gray-500">{{ formatFileSize(file.size) }}</p>
                  
                  <!-- AI Suggestions Component -->
                  <PreUploadAiSuggestions
                    :analysis="file.aiSuggestions"
                    :is-analyzing="file.isAnalyzing"
                    :error="file.analysisError"
                    :departments="departments"
                    :projects="projects"
                    :file-types="fileTypes"
                    :department-categories="departmentCategories"
                    @analyze-file="analyzeFileBeforeUpload(file)"
                    @retry-analysis="analyzeFileBeforeUpload(file)"
                    @apply-title="(title) => applyAiSuggestion(index, 'title', title)"
                    @apply-description="(description) => applyAiSuggestion(index, 'description', description)"
                    @apply-department="(departmentId) => applyAiSuggestion(index, 'departmentId', departmentId)"
                    @apply-project="(projectId) => applyAiSuggestion(index, 'projectId', projectId)"
                    @apply-tags="(tags) => applyAiSuggestion(index, 'tags', tags)"
                    @apply-file-type="(fileTypeId) => applyAiSuggestion(index, 'fileTypeId', fileTypeId)"
                    @apply-department-category="(categoryId, departmentId) => applyDepartmentCategoryWithDepartment(index, categoryId, departmentId)"
                    @apply-visibility="(visibility) => applyAiSuggestion(index, 'visibility', visibility)"
                    @apply-all="(suggestions) => applyAllAiSuggestions(index, suggestions)"
                  />
                  
                  <!-- File Details Form -->
                  <div class="mt-3 grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Title
                      </label>
                      <input
                        v-model="file.metadata.title"
                        type="text"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                        :placeholder="file.name"
                      />
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Department Category
                      </label>
                      <select
                        v-model="file.metadata.departmentCategoryId"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                        :disabled="!file.metadata.departmentId"
                      >
                        <option value="">Select a category</option>
                        <option
                          v-for="category in filteredDepartmentCategories(file.metadata.departmentId)"
                          :key="category.id"
                          :value="category.id"
                        >
                          {{ category.name }}
                        </option>
                      </select>
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        File Type
                      </label>
                      <select
                        v-model="file.metadata.fileTypeId"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                      >
                        <option value="">Select a file type</option>
                        <option
                          v-for="fileType in fileTypes"
                          :key="fileType.id"
                          :value="fileType.id"
                        >
                          {{ fileType.name }}
                        </option>
                      </select>
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Department
                      </label>
                      <select
                        v-model="file.metadata.departmentId"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                      >
                        <option value="">Select a department</option>
                        <option
                          v-for="department in departments"
                          :key="department.id"
                          :value="department.id"
                        >
                          {{ department.name }}
                        </option>
                      </select>
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Project
                      </label>
                      <select
                        v-model="file.metadata.projectId"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                        :disabled="!file.metadata.departmentId"
                      >
                        <option value="">Select a project</option>
                        <option
                          v-for="project in filteredProjects(file.metadata.departmentId)"
                          :key="project.id"
                          :value="project.id"
                        >
                          {{ project.name }}
                        </option>
                      </select>
                    </div>
                    
                    <div class="md:col-span-2">
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Description
                      </label>
                      <textarea
                        v-model="file.metadata.description"
                        rows="2"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                        placeholder="Enter file description (optional)"
                      ></textarea>
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Visibility
                      </label>
                      <select
                        v-model="file.metadata.visibility"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                      >
                        <option value="PRIVATE">Private</option>
                        <option value="PUBLIC">Public</option>
                        <option value="SHARED">Shared</option>
                      </select>
                    </div>
                    
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-1">
                        Tags
                      </label>
                      <input
                        v-model="file.metadata.tags"
                        type="text"
                        class="w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 text-gray-900"
                        placeholder="Tag1, Tag2, Tag3"
                      />
                    </div>
                  </div>
                </div>
                
                <button
                  @click="removeFile(index)"
                  class="ml-4 text-red-500 hover:text-red-700"
                >
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              
              <!-- Upload Progress -->
              <div v-if="file.uploading" class="mt-3">
                <div class="flex items-center justify-between text-sm">
                  <span class="text-gray-600">Uploading to Google Drive...</span>
                  <span class="text-gray-600">{{ file.progress }}%</span>
                </div>
                <div class="mt-1 bg-gray-200 rounded-full h-2">
                  <div
                    class="bg-blue-600 h-2 rounded-full transition-all duration-300"
                    :style="{ width: file.progress + '%' }"
                  ></div>
                </div>
              </div>
              
              <!-- Upload Status -->
              <div v-if="file.uploaded" class="mt-3">
                <div class="flex items-center text-sm text-green-600">
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  Successfully uploaded to Google Drive
                </div>
              </div>
              
              <div v-if="file.error" class="mt-3">
                <div class="flex items-center text-sm text-red-600">
                  <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                  {{ file.error }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- Upload Actions -->
          <div class="mt-6 flex justify-between items-center">
            <button
              @click="clearFiles"
              class="px-4 py-2 text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
            >
              Clear All
            </button>
            
            <div class="flex space-x-3">
              <button
                @click="uploadFiles"
                :disabled="isUploading || selectedFiles.every(f => f.uploaded)"
                class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
              >
                <span v-if="isUploading">Uploading...</span>
                <span v-else>Upload to Google Drive</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Files List -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold text-gray-900">Your Files</h2>
            <div class="flex items-center space-x-4">
              <!-- Search -->
              <div class="relative">
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search files..."
                  class="pl-10 pr-4 py-2 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900"
                />
                <svg
                  class="absolute left-3 top-2.5 h-5 w-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              
              <!-- Refresh -->
              <button
                @click="loadFiles"
                class="p-2 text-gray-500 hover:text-gray-700 rounded-lg hover:bg-gray-100"
              >
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                </svg>
              </button>
            </div>
          </div>
        </div>
        
        <!-- Files Table -->
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Name
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Size
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Department
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Project
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Storage
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Uploaded
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="file in filteredFiles" :key="file.id" class="hover:bg-gray-50">
                <td class="px-6 py-4">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 w-8 h-8">
                      <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                        <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      </div>
                    </div>
                    <div class="ml-3">
                      <div class="text-sm font-medium text-gray-900">
                        {{ file.title || file.originalFilename }}
                      </div>
                      <div class="text-sm text-gray-500">
                        {{ file.originalFilename }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatFileSize(file.fileSize) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ file.contentType }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  <span v-if="file.departmentName" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ file.departmentName }}
                  </span>
                  <span v-else class="text-gray-400">-</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  <span v-if="file.projectName" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                    {{ file.projectName }}
                  </span>
                  <span v-else class="text-gray-400">-</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    v-if="file.driveFileId"
                    class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800"
                  >
                    Google Drive
                  </span>
                  <span
                    v-else
                    class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800"
                  >
                    Local Storage
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ formatDate(file.uploadedAt) }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                  <button
                    @click="downloadFile(file)"
                    class="text-blue-600 hover:text-blue-900"
                  >
                    Download
                  </button>
                  <button
                    @click="deleteFile(file)"
                    class="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          
          <div v-if="files.length === 0 && !loading" class="px-6 py-8 text-center">
            <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <h3 class="mt-2 text-sm font-medium text-gray-900">No files</h3>
            <p class="mt-1 text-sm text-gray-500">Get started by uploading your first file.</p>
          </div>
          
          <div v-if="loading" class="px-6 py-8 text-center">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p class="mt-2 text-sm text-gray-500">Loading files...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { fileApi, type FileResponse } from '@/services/fileApi'
import departmentCategoryApi, { type DepartmentCategory } from '@/services/departmentCategoryApi'
import departmentApi, { type Department } from '@/services/departmentApi'
import projectApi, { type Project } from '@/services/projectApi'
import fileTypeApi, { type FileType } from '@/services/fileTypeApi'
import PreUploadAiSuggestions from '@/components/files/PreUploadAiSuggestions.vue'
import aiAnalysisApi from '@/services/aiAnalysisApi'

// Reactive data
const isDragOver = ref(false)
const fileInput = ref<HTMLInputElement>()
const selectedFiles = ref<any[]>([])
const files = ref<FileResponse[]>([])
const departmentCategories = ref<DepartmentCategory[]>([])
const departments = ref<Department[]>([])
const projects = ref<Project[]>([])
const fileTypes = ref<FileType[]>([])
const searchQuery = ref('')
const loading = ref(false)
const isUploading = ref(false)

// Allowed file types based on backend configuration
const allowedFileTypes = '.pdf,.doc,.docx,.txt,.jpg,.jpeg,.png,.gif,.mp4,.avi,.mp3,.wav,.zip,.rar,.json,.xml,.csv'

// Computed properties
const filteredFiles = computed(() => {
  if (!searchQuery.value) return files.value
  return files.value.filter((file: FileResponse) =>
    file.originalFilename.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    (file.title && file.title.toLowerCase().includes(searchQuery.value.toLowerCase()))
  )
})

// Methods
const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
  
  const files = Array.from(event.dataTransfer?.files || [])
  addFiles(files)
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = Array.from(target.files || [])
  addFiles(files)
}

const addFiles = (files: File[]) => {
  const newFiles = files.map(file => ({
    file: file, // Store the original File object
    name: file.name,
    size: file.size,
    type: file.type,
    metadata: {
      title: '',
      description: '',
      departmentCategoryId: '',
      departmentId: '',
      projectId: '',
      fileTypeId: '',
      visibility: 'PRIVATE',
      tags: ''
    },
    uploading: false,
    uploaded: false,
    progress: 0,
    error: null,
    // AI Analysis properties (pre-upload only)
    aiSuggestions: null,
    isAnalyzing: false,
    analysisError: null
  }))
  
  selectedFiles.value.push(...newFiles)
}

const removeFile = (index: number) => {
  selectedFiles.value.splice(index, 1)
}

const clearFiles = () => {
  selectedFiles.value = []
}

const uploadFiles = async () => {
  isUploading.value = true
  
  for (const file of selectedFiles.value) {
    if (file.uploaded) continue
    
    try {
      file.uploading = true
      file.progress = 0
      file.error = null
      
      const formData = new FormData()
      formData.append('file', file.file) // Use the original File object
      formData.append('title', file.metadata.title || file.name)
      formData.append('description', file.metadata.description)
      formData.append('departmentCategoryId', file.metadata.departmentCategoryId)
      formData.append('departmentId', file.metadata.departmentId)
      formData.append('projectId', file.metadata.projectId)
      formData.append('fileTypeId', file.metadata.fileTypeId)
      formData.append('visibility', file.metadata.visibility)
      formData.append('tags', file.metadata.tags)
      
      // Simulate progress
      const progressInterval = setInterval(() => {
        if (file.progress < 90) {
          file.progress += Math.random() * 15
        }
      }, 200)
      
      const response = await fileApi.uploadFile(formData)
      
      clearInterval(progressInterval)
      file.progress = 100
      file.uploading = false
      file.uploaded = true
      
      // Add to files list (use the fileResponse part)
      files.value.unshift(response.data.fileResponse)
      
    } catch (error: any) {
      file.uploading = false
      file.error = error.response?.data?.message || error.message || 'Upload failed'
    }
  }
  
  isUploading.value = false
}

const loadFiles = async () => {
  loading.value = true
  try {
    const response = await fileApi.getUserFiles()
    files.value = response.data.content
  } catch (error) {
    console.error('Failed to load files:', error)
  } finally {
    loading.value = false
  }
}

// Load department categories for a specific department
const loadDepartmentCategories = async (departmentId: number) => {
  try {
    console.log(`Loading categories for department ID: ${departmentId}`)
    
    // Use the user-accessible endpoint (no /all suffix)
    const response = await departmentCategoryApi.getCategoriesByDepartment(departmentId)
    console.log(`Department ${departmentId} categories response:`, response)
    
    // Check if response and data exist
    if (response.success && response.data && Array.isArray(response.data) && response.data.length > 0) {
      console.log(`Found ${response.data.length} categories for department ${departmentId}`)
      // Add categories to the existing array, avoiding duplicates
      const newCategories = response.data.filter(newCat => 
        !departmentCategories.value.some(existingCat => existingCat.id === newCat.id)
      )
      departmentCategories.value.push(...newCategories)
      console.log(`Added ${newCategories.length} new categories for department ${departmentId}`)
    } else if (Array.isArray(response) && response.length > 0) {
      // Handle direct array response
      console.log(`Found ${response.length} categories (direct array) for department ${departmentId}`)
      const newCategories = response.filter(newCat => 
        !departmentCategories.value.some(existingCat => existingCat.id === newCat.id)
      )
      departmentCategories.value.push(...newCategories)
    } else if ((response as any).data && Array.isArray((response as any).data.data) && (response as any).data.data.length > 0) {
      // Handle nested data structure
      const categories = (response as any).data.data
      console.log(`Found ${categories.length} categories (nested) for department ${departmentId}`)
      const newCategories = categories.filter((newCat: any) => 
        !departmentCategories.value.some(existingCat => existingCat.id === newCat.id)
      )
      departmentCategories.value.push(...newCategories)
    } else {
      console.log(`No categories found for department ${departmentId} (this might be normal if no categories are configured)`)
    }
  } catch (error) {
    console.error(`Failed to load department categories for department ${departmentId}:`, error)
  }
}

// Load all department categories for all departments
const loadAllDepartmentCategories = async () => {
  try {
    console.log('Loading all department categories...')
    console.log('Departments value before loading categories:', departments.value)
    
    // Clear existing categories
    departmentCategories.value = []
    
    // Check if departments is properly initialized and is an array
    if (!departments.value || !Array.isArray(departments.value)) {
      console.warn('Departments not loaded or not an array, skipping category loading')
      console.log('Departments type:', typeof departments.value)
      console.log('Is array:', Array.isArray(departments.value))
      return
    }
    
    if (departments.value.length === 0) {
      console.warn('No departments found, skipping category loading')
      return
    }
    
    console.log(`Loading categories for ${departments.value.length} departments`)
    
    // Load categories for each department
    const categoryPromises = departments.value.map(department => {
      console.log(`Queueing category load for department: ${department.name} (ID: ${department.id})`)
      return loadDepartmentCategories(department.id)
    })
    
    // Wait for all category loading to complete
    await Promise.allSettled(categoryPromises)
    
    console.log(`Finished loading all department categories. Total categories loaded: ${departmentCategories.value.length}`)
  } catch (error) {
    console.error('Failed to load all department categories:', error)
  }
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
    console.error('Failed to download file:', error)
  }
}

const deleteFile = async (file: FileResponse) => {
  if (!confirm(`Are you sure you want to delete "${file.originalFilename}"?`)) {
    return
  }
  
  try {
    await fileApi.deleteFile(file.id)
    files.value = files.value.filter((f: FileResponse) => f.id !== file.id)
  } catch (error) {
    console.error('Failed to delete file:', error)
  }
}

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}

// Helper function to filter projects by department
const filteredProjects = (departmentId: string | number) => {
  if (!departmentId) return []
  console.log(`Filtering projects for department ${departmentId}`)
  console.log('Available projects:', projects.value)
  const filtered = projects.value.filter(project => project.departmentId === Number(departmentId))
  console.log(`Found ${filtered.length} projects for department ${departmentId}:`, filtered)
  return filtered
}

// Helper function to filter department categories by department
const filteredDepartmentCategories = (departmentId: string | number) => {
  if (!departmentId) return []
  console.log(`Filtering categories for department ${departmentId}`)
  console.log('Available categories:', departmentCategories.value)
  const filtered = departmentCategories.value.filter(category => category.departmentId === Number(departmentId))
  console.log(`Found ${filtered.length} categories for department ${departmentId}:`, filtered)
  return filtered
}

// Load departments
const loadDepartments = async () => {
  try {
    const response = await departmentApi.getAll()
    console.log('Departments API response:', response.data)
    
    // Check if response has the expected structure
    if (response.data && response.data.data) {
      departments.value = response.data.data
    } else if (Array.isArray(response.data)) {
      // Fallback if data is directly in response.data
      departments.value = response.data
    } else {
      console.warn('Unexpected response structure:', response.data)
      departments.value = []
    }
    
    console.log('Loaded departments:', departments.value)
  } catch (error) {
    console.error('Failed to load departments:', error)
    // Initialize as empty array on error to prevent iteration issues
    departments.value = []
  }
}

// Load projects
const loadProjects = async () => {
  try {
    const response = await projectApi.getAll()
    console.log('Projects API response:', response)
    
    // Check if response has the expected structure
    if (response.data && response.data.data) {
      projects.value = response.data.data
    } else if (Array.isArray(response.data)) {
      // Fallback if data is directly in response.data
      projects.value = response.data
    } else if (Array.isArray(response)) {
      // Direct array response
      projects.value = response
    } else if ((response as any).success && Array.isArray((response as any).data)) {
      // Standard API response structure
      projects.value = (response as any).data
    } else {
      console.warn('Unexpected projects response structure:', response)
      projects.value = []
    }
    
    console.log('Loaded projects:', projects.value)
  } catch (error) {
    console.error('Failed to load projects:', error)
    projects.value = []
  }
}

// Load file types
const loadFileTypes = async () => {
  try {
    const response = await fileTypeApi.getAllFileTypes()
    console.log('File types API raw response:', response)
    console.log('Response type:', typeof response)
    console.log('Response keys:', Object.keys(response))
    
    // Handle different possible response structures
    if (response.success && response.data && Array.isArray(response.data)) {
      // Standard API response with success flag
      console.log('Using standard API response structure')
      fileTypes.value = response.data
    } else if (Array.isArray(response)) {
      // Direct array response
      console.log('Using direct array response structure')
      fileTypes.value = response
    } else if ((response as any).data && Array.isArray((response as any).data.data)) {
      // Nested data structure
      console.log('Using nested data structure')
      fileTypes.value = (response as any).data.data
    } else {
      console.warn('Unexpected file types response structure:', response)
      fileTypes.value = []
    }
    
    console.log('Final fileTypes value:', fileTypes.value)
  } catch (error) {
    console.error('Failed to load file types:', error)
    fileTypes.value = []
  }
}

// AI Analysis Functions
const analyzeFileBeforeUpload = async (file: any) => {
  try {
    file.isAnalyzing = true
    file.analysisError = null
    
    // Check analysis capability
    const capability = await aiAnalysisApi.getAnalysisCapability(file.name, file.size)
    if (capability === 'none') {
      file.analysisError = 'File type not supported for AI analysis'
      file.isAnalyzing = false
      return
    }
    
    // Set analysis type message
    if (capability === 'metadata') {
      file.analysisType = 'metadata-only'
      file.analysisMessage = 'Large file - analyzing metadata only'
    } else {
      file.analysisType = 'full-content'
      file.analysisMessage = 'Analyzing file content and metadata'
    }
    
    // Call AI analysis API
    const response = await aiAnalysisApi.analyzeFile(file.file, {
      departmentId: file.metadata.departmentId ? Number(file.metadata.departmentId) : undefined,
      projectId: file.metadata.projectId ? Number(file.metadata.projectId) : undefined,
      description: file.metadata.description || undefined,
      userDepartments: departments.value,
      userProjects: projects.value,
      availableFileTypes: fileTypes.value,
      availableDepartmentCategories: departmentCategories.value
    })
    
    if (response.success) {
      file.aiSuggestions = response.data
      console.log(`AI analysis completed for file: ${file.name} (${capability})`, response.data)
    } else {
      file.analysisError = 'AI analysis failed. Please try again.'
    }
  } catch (error: any) {
    console.error('AI analysis error:', error)
    file.analysisError = error.message || 'AI analysis failed. Please try again.'
  } finally {
    file.isAnalyzing = false
  }
}

const applyAiSuggestion = (index: number, field: string, value: any) => {
  const file = selectedFiles.value[index]
  if (!file) return
  
  switch (field) {
    case 'title':
      file.metadata.title = value
      break
    case 'description':
      file.metadata.description = value
      break
    case 'departmentId':
      file.metadata.departmentId = value.toString()
      // Load department categories when department changes
      if (value) {
        loadDepartmentCategories(value)
      }
      break
    case 'projectId':
      file.metadata.projectId = value.toString()
      break
    case 'tags':
      file.metadata.tags = value
      break
    case 'fileTypeId':
      file.metadata.fileTypeId = value.toString()
      break
    case 'departmentCategoryId':
      file.metadata.departmentCategoryId = value.toString()
      break
    case 'visibility':
      file.metadata.visibility = value
      break
  }
}

const applyDepartmentCategoryWithDepartment = (index: number, categoryId: number, departmentId: number) => {
  const file = selectedFiles.value[index]
  if (!file) return
  
  // Apply both department category and its parent department
  file.metadata.departmentCategoryId = categoryId.toString()
  file.metadata.departmentId = departmentId.toString()
  
  // Load department categories for the selected department
  loadDepartmentCategories(departmentId)
}

const applyAllAiSuggestions = (index: number, suggestions: any) => {
  const file = selectedFiles.value[index]
  if (!file) return
  
  if (suggestions.title) {
    file.metadata.title = suggestions.title
  }
  if (suggestions.description) {
    file.metadata.description = suggestions.description
  }
  
  // Handle department category with automatic department selection
  if (suggestions.departmentCategoryId) {
    file.metadata.departmentCategoryId = suggestions.departmentCategoryId.toString()
    
    // If a department ID is provided with the category, apply it
    if (suggestions.departmentId) {
      file.metadata.departmentId = suggestions.departmentId.toString()
      loadDepartmentCategories(suggestions.departmentId)
    } else {
      // Find the department for this category
      const category = departmentCategories.value.find(dc => dc.id === suggestions.departmentCategoryId)
      if (category) {
        file.metadata.departmentId = category.departmentId.toString()
        loadDepartmentCategories(category.departmentId)
      }
    }
  } else if (suggestions.departmentId) {
    // Only apply department if no category was specified
    file.metadata.departmentId = suggestions.departmentId.toString()
    loadDepartmentCategories(suggestions.departmentId)
  }
  
  if (suggestions.projectId) {
    file.metadata.projectId = suggestions.projectId.toString()
  }
  if (suggestions.tags) {
    file.metadata.tags = suggestions.tags
  }
  if (suggestions.fileTypeId) {
    file.metadata.fileTypeId = suggestions.fileTypeId.toString()
  }
  if (suggestions.visibility) {
    file.metadata.visibility = suggestions.visibility
  }
}

// Lifecycle hooks
onMounted(async () => {
  loadFiles()
  await loadDepartments()
  loadProjects()
  loadFileTypes()
  // Load department categories for all departments after departments are loaded
  await loadAllDepartmentCategories()
})
</script>
