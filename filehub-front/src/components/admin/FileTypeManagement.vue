<template>
  <div class="file-type-management">
    <div class="mb-6">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold text-gray-900">File Type Management</h2>
        <button
          @click="openCreateModal"
          class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
        >
          <i class="fas fa-plus mr-2"></i>
          Add File Type
        </button>
      </div>
    </div>

    <!-- File Types List -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Description
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Extensions
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Max Size
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="fileType in fileTypes" :key="fileType.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div
                    class="flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center"
                    :style="{ backgroundColor: fileType.color || '#6B7280' }"
                  >
                    <i :class="fileType.icon || 'fas fa-file'" class="text-white text-sm"></i>
                  </div>
                  <div class="ml-3">
                    <div class="text-sm font-medium text-gray-900">{{ fileType.name }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ fileType.description || '-' }}</div>
              </td>
              <td class="px-6 py-4">
                <div class="flex flex-wrap gap-1">
                  <span
                    v-for="ext in getExtensions(fileType.allowedExtensions)"
                    :key="ext"
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                  >
                    .{{ ext }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                {{ formatFileSize(fileType.maxSize) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editFileType(fileType)"
                  class="text-indigo-600 hover:text-indigo-900 mr-3"
                >
                  <i class="fas fa-edit"></i>
                </button>
                <button
                  @click="deleteFileType(fileType.id)"
                  class="text-red-600 hover:text-red-900"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">
            {{ editingFileType ? 'Edit File Type' : 'Create File Type' }}
          </h3>
          <form @submit.prevent="saveFileType">
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">Name</label>
              <input
                v-model="form.name"
                type="text"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
              <textarea
                v-model="form.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              ></textarea>
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                Allowed Extensions (comma-separated)
              </label>
              <input
                v-model="extensionsInput"
                type="text"
                placeholder="pdf,doc,docx,txt"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">Color</label>
              <input
                v-model="form.color"
                type="color"
                class="w-full h-10 border border-gray-300 rounded-md"
              />
            </div>
            
            <div class="mb-4">
              <label class="block text-sm font-medium text-gray-700 mb-2">Icon (FontAwesome class)</label>
              <input
                v-model="form.icon"
                type="text"
                placeholder="fas fa-file-pdf"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="mb-6">
              <label class="block text-sm font-medium text-gray-700 mb-2">Max Size (MB)</label>
              <input
                v-model.number="maxSizeMB"
                type="number"
                min="1"
                max="1000"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            
            <div class="flex justify-end space-x-3">
              <button
                type="button"
                @click="closeModal"
                class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300"
              >
                Cancel
              </button>
              <button
                type="submit"
                :disabled="loading"
                class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {{ loading ? 'Saving...' : 'Save' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import fileTypeApi, { type FileType, type CreateFileTypeRequest } from '@/services/fileTypeApi'

const fileTypes = ref<FileType[]>([])
const loading = ref(false)
const showModal = ref(false)
const editingFileType = ref<FileType | null>(null)

const form = ref<CreateFileTypeRequest>({
  name: '',
  description: '',
  allowedExtensions: [],
  color: '#6B7280',
  icon: 'fas fa-file',
  maxSize: 104857600 // 100MB default
})

const extensionsInput = ref('')
const maxSizeMB = ref(100)

const formatFileSize = (bytes: number | undefined): string => {
  if (!bytes) return '-'
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

const getExtensions = (extensions: string[] | string): string[] => {
  if (Array.isArray(extensions)) return extensions
  try {
    return JSON.parse(extensions)
  } catch {
    return []
  }
}

const loadFileTypes = async () => {
  try {
    loading.value = true
    const response = await fileTypeApi.getAllFileTypes()
    if (response.success) {
      fileTypes.value = response.data
    }
  } catch (error) {
    console.error('Error loading file types:', error)
  } finally {
    loading.value = false
  }
}

const openCreateModal = () => {
  editingFileType.value = null
  form.value = {
    name: '',
    description: '',
    allowedExtensions: [],
    color: '#6B7280',
    icon: 'fas fa-file',
    maxSize: 104857600
  }
  extensionsInput.value = ''
  maxSizeMB.value = 100
  showModal.value = true
}

const editFileType = (fileType: FileType) => {
  editingFileType.value = fileType
  form.value = { ...fileType }
  extensionsInput.value = getExtensions(fileType.allowedExtensions).join(', ')
  maxSizeMB.value = Math.round((fileType.maxSize || 0) / 1024 / 1024)
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingFileType.value = null
}

const saveFileType = async () => {
  try {
    loading.value = true
    
    // Prepare extensions
    const extensions = extensionsInput.value
      .split(',')
      .map(ext => ext.trim())
      .filter(ext => ext.length > 0)
    
    const formData = {
      ...form.value,
      allowedExtensions: extensions,
      maxSize: maxSizeMB.value * 1024 * 1024 // Convert MB to bytes
    }

    if (editingFileType.value) {
      await fileTypeApi.updateFileType(editingFileType.value.id, formData)
    } else {
      await fileTypeApi.createFileType(formData)
    }
    
    await loadFileTypes()
    closeModal()
  } catch (error) {
    console.error('Error saving file type:', error)
  } finally {
    loading.value = false
  }
}

const deleteFileType = async (id: number) => {
  if (!confirm('Are you sure you want to delete this file type?')) return
  
  try {
    await fileTypeApi.deleteFileType(id)
    await loadFileTypes()
  } catch (error) {
    console.error('Error deleting file type:', error)
  }
}

onMounted(() => {
  loadFileTypes()
})
</script>
