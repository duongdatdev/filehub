<template>
  <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-4 mb-4 border border-blue-200">
    <div class="flex items-center justify-between mb-3">
      <h4 class="font-semibold text-blue-900 flex items-center">
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
        </svg>
        AI Smart Suggestions
      </h4>
      
      <div class="flex space-x-2">
        <button 
          v-if="!analysis && !isAnalyzing"
          @click="emit('analyze-file')"
          class="px-3 py-1 bg-blue-600 text-white text-sm rounded-md hover:bg-blue-700 transition-colors"
        >
          Analyze File
        </button>
        
        <button 
          v-if="error"
          @click="emit('retry-analysis')"
          class="px-3 py-1 bg-orange-600 text-white text-sm rounded-md hover:bg-orange-700 transition-colors"
        >
          Retry Analysis
        </button>
        
        <button 
          v-if="analysis && hasApplicableSuggestions"
          @click="applySelectedSuggestions"
          :disabled="selectedSuggestionsCount === 0"
          :class="[
            'px-3 py-1 text-sm rounded-md transition-colors',
            selectedSuggestionsCount > 0 
              ? 'bg-green-600 text-white hover:bg-green-700' 
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          ]"
        >
          Apply Selected ({{ selectedSuggestionsCount }})
        </button>
        
        <button 
          v-if="analysis && hasApplicableSuggestions"
          @click="selectAllSuggestions"
          class="px-3 py-1 bg-blue-100 text-blue-700 text-sm rounded-md hover:bg-blue-200 transition-colors"
        >
          {{ allSelected ? 'Deselect All' : 'Select All' }}
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isAnalyzing" class="text-center py-4">
      <div class="inline-flex items-center">
        <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <div class="text-left">
          <span class="text-blue-700 block">Analyzing file with AI...</span>
          <span class="text-blue-500 text-xs">This may take up to 60 seconds for complex documents</span>
        </div>
      </div>
    </div>

    <!-- Error State -->
    <div v-if="error" class="bg-red-50 border border-red-200 rounded-md p-3">
      <div class="flex items-center">
        <svg class="w-5 h-5 text-red-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="text-red-700 text-sm">{{ error }}</span>
      </div>
      <div v-if="error.includes('File type not supported')" class="mt-2 text-xs text-red-600">
        <p class="font-medium">Supported file types for AI analysis:</p>
        <p class="mt-1">txt, pdf, doc, docx, md, json, xml, csv</p>
        <p class="mt-1">Maximum file size: 10MB</p>
      </div>
    </div>

    <!-- AI Suggestions -->
    <div v-if="analysis" class="space-y-3">
      <div class="text-xs text-gray-600 mb-3 p-2 bg-blue-50 rounded border border-blue-200">
        <p class="flex items-center">
          <svg class="w-4 h-4 mr-1 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          Check the suggestions you want to apply, then click "Apply Selected". You can also apply individual suggestions immediately.
        </p>
      </div>
      <!-- Title Suggestion -->
      <div v-if="analysis.suggestedTitle" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.title ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.title"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Title</label>
              <p class="text-gray-900 mt-1">{{ analysis.suggestedTitle }}</p>
            </div>
          </div>
          <button 
            @click="emit('apply-title', analysis.suggestedTitle)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Description Suggestion -->
      <div v-if="analysis.suggestedDescription" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.description ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.description"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Description</label>
              <p class="text-gray-900 mt-1 text-sm leading-relaxed">{{ analysis.suggestedDescription }}</p>
            </div>
          </div>
          <button 
            @click="emit('apply-description', analysis.suggestedDescription)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Department Suggestion -->
      <div v-if="suggestedDepartment" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.department ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.department"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Department</label>
              <p class="text-gray-900 mt-1">{{ suggestedDepartment.name }}</p>
              <p class="text-gray-500 text-xs mt-1">Based on: "{{ analysis.departmentSuggestion }}"</p>
            </div>
          </div>
          <button 
            @click="emit('apply-department', suggestedDepartment.id)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Project Suggestion -->
      <div v-if="suggestedProject" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.project ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.project"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Project</label>
              <p class="text-gray-900 mt-1">{{ suggestedProject.name }}</p>
              <p class="text-gray-500 text-xs mt-1">Based on: "{{ analysis.projectSuggestion }}"</p>
            </div>
          </div>
          <button 
            @click="emit('apply-project', suggestedProject.id)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Tags Suggestion -->
      <div v-if="analysis.tags && analysis.tags.length > 0" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.tags ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.tags"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Tags</label>
              <div class="mt-2 flex flex-wrap gap-1">
                <span 
                  v-for="tag in analysis.tags" 
                  :key="tag"
                  class="inline-block bg-gray-100 text-gray-700 px-2 py-1 rounded-full text-xs"
                >
                  {{ tag }}
                </span>
              </div>
            </div>
          </div>
          <button 
            @click="emit('apply-tags', analysis.tags.join(', '))"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- File Type Suggestion -->
      <div v-if="suggestedFileType" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.fileType ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.fileType"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested File Type</label>
              <p class="text-gray-900 mt-1">{{ suggestedFileType.name }}</p>
              <p v-if="suggestedFileType.description" class="text-gray-500 text-xs mt-1">{{ suggestedFileType.description }}</p>
            </div>
          </div>
          <button 
            @click="emit('apply-file-type', suggestedFileType.id)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Department Category Suggestion -->
      <div v-if="suggestedDepartmentCategory" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.departmentCategory ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.departmentCategory"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Category</label>
              <p class="text-gray-900 mt-1">{{ suggestedDepartmentCategory.name }}</p>
              <p v-if="suggestedDepartmentCategory" class="text-blue-600 text-xs mt-1">
                Department: {{ departments.find(d => d.id === suggestedDepartmentCategory.departmentId)?.name || 'Unknown' }}
              </p>
              <p v-if="suggestedDepartmentCategory.description" class="text-gray-500 text-xs mt-1">{{ suggestedDepartmentCategory.description }}</p>
            </div>
          </div>
          <button 
            @click="emit('apply-department-category', suggestedDepartmentCategory.id, suggestedDepartmentCategory.departmentId)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- Visibility Suggestion -->
      <div v-if="analysis.suggestedVisibility" 
           :class="['rounded-md p-3 border transition-colors', 
                   selectedSuggestions.visibility ? 'bg-blue-50 border-blue-300' : 'bg-white border-gray-200']">
        <div class="flex items-start justify-between">
          <div class="flex items-start space-x-3 flex-1">
            <input 
              type="checkbox" 
              v-model="selectedSuggestions.visibility"
              class="mt-1 h-4 w-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
            />
            <div class="flex-1">
              <label class="text-sm font-medium text-gray-700">Suggested Visibility</label>
              <p class="text-gray-900 mt-1">
                <span :class="[
                  'inline-flex items-center px-2 py-1 rounded-full text-xs font-medium',
                  analysis.suggestedVisibility === 'PUBLIC' ? 'bg-green-100 text-green-800' :
                  analysis.suggestedVisibility === 'DEPARTMENT' ? 'bg-blue-100 text-blue-800' :
                  'bg-gray-100 text-gray-800'
                ]">
                  {{ analysis.suggestedVisibility }}
                </span>
              </p>
              <p class="text-gray-500 text-xs mt-1">
                Based on content analysis and organizational context
              </p>
            </div>
          </div>
          <button 
            @click="emit('apply-visibility', analysis.suggestedVisibility)"
            class="ml-3 px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded hover:bg-blue-200 transition-colors flex-shrink-0"
          >
            Apply Now
          </button>
        </div>
      </div>

      <!-- No suggestions message -->
      <div v-if="!analysis.suggestedTitle && 
                 !analysis.suggestedDescription && 
                 !suggestedDepartment && 
                 !suggestedProject && 
                 !suggestedFileType &&
                 !suggestedDepartmentCategory &&
                 !analysis.suggestedVisibility &&
                 (!analysis.tags || analysis.tags.length === 0)" 
           class="text-center py-4 text-gray-500">
        <svg class="w-8 h-8 mx-auto mb-2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 12h6m-6-4h6m2 5.291A7.962 7.962 0 0112 20.4a7.962 7.962 0 01-8-7.109m0 0A7.962 7.962 0 0112 4.6a7.962 7.962 0 018 7.109M12 10V6" />
        </svg>
        <p class="text-sm">AI analysis complete, but no specific suggestions found for this file.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { FileAnalysisResponse } from '@/services/fileApi'
import type { Department } from '@/services/departmentApi'
import type { Project } from '@/services/projectApi'

interface Props {
  analysis?: FileAnalysisResponse
  isAnalyzing: boolean
  error?: string
  departments: Department[]
  projects: Project[]
  fileTypes?: FileType[]
  departmentCategories?: DepartmentCategory[]
}

const props = defineProps<Props>()

// Import required types
interface FileType {
  id: number
  name: string
  description?: string
}

interface DepartmentCategory {
  id: number
  name: string
  description?: string
  departmentId: number
}

// Selection state for individual suggestions
const selectedSuggestions = ref({
  title: false,
  description: false,
  department: false,
  project: false,
  tags: false,
  fileType: false,
  departmentCategory: false,
  visibility: false
})

// Watch for analysis changes to reset selections
watch(() => props.analysis, () => {
  selectedSuggestions.value = {
    title: false,
    description: false,
    department: false,
    project: false,
    tags: false,
    fileType: false,
    departmentCategory: false,
    visibility: false
  }
}, { immediate: true })

// Find suggested department by name
const suggestedDepartment = computed(() => {
  if (!props.analysis?.departmentSuggestion) return null
  return props.departments.find(dept => 
    dept.name.toLowerCase().includes(props.analysis!.departmentSuggestion!.toLowerCase()) ||
    props.analysis!.departmentSuggestion!.toLowerCase().includes(dept.name.toLowerCase())
  )
})

// Find suggested project by name
const suggestedProject = computed(() => {
  if (!props.analysis?.projectSuggestion) return null
  return props.projects.find(project => 
    project.name.toLowerCase().includes(props.analysis!.projectSuggestion!.toLowerCase()) ||
    props.analysis!.projectSuggestion!.toLowerCase().includes(project.name.toLowerCase())
  )
})

// Find suggested file type
const suggestedFileType = computed(() => {
  if (!props.analysis?.suggestedFileTypeId && !props.analysis?.suggestedFileTypeName) return null
  
  // First try to match by ID
  if (props.analysis.suggestedFileTypeId) {
    return props.fileTypes?.find(ft => ft.id === props.analysis!.suggestedFileTypeId)
  }
  
  // Then try to match by name
  if (props.analysis.suggestedFileTypeName) {
    return props.fileTypes?.find(ft => 
      ft.name.toLowerCase().includes(props.analysis!.suggestedFileTypeName!.toLowerCase()) ||
      props.analysis!.suggestedFileTypeName!.toLowerCase().includes(ft.name.toLowerCase())
    )
  }
  
  return null
})

// Find suggested department category
const suggestedDepartmentCategory = computed(() => {
  if (!props.analysis?.suggestedDepartmentCategoryId && !props.analysis?.suggestedDepartmentCategoryName) return null
  
  // First try to match by ID
  if (props.analysis.suggestedDepartmentCategoryId) {
    return props.departmentCategories?.find(dc => dc.id === props.analysis!.suggestedDepartmentCategoryId)
  }
  
  // Then try to match by name
  if (props.analysis.suggestedDepartmentCategoryName) {
    return props.departmentCategories?.find(dc => 
      dc.name.toLowerCase().includes(props.analysis!.suggestedDepartmentCategoryName!.toLowerCase()) ||
      props.analysis!.suggestedDepartmentCategoryName!.toLowerCase().includes(dc.name.toLowerCase())
    )
  }
  
  return null
})

// Check if there are any applicable suggestions
const hasApplicableSuggestions = computed(() => {
  return !!(props.analysis?.suggestedTitle || 
           props.analysis?.suggestedDescription || 
           suggestedDepartment.value || 
           suggestedProject.value || 
           suggestedFileType.value ||
           suggestedDepartmentCategory.value ||
           props.analysis?.suggestedVisibility ||
           (props.analysis?.tags && props.analysis.tags.length > 0))
})

// Count selected suggestions
const selectedSuggestionsCount = computed(() => {
  let count = 0
  if (selectedSuggestions.value.title && props.analysis?.suggestedTitle) count++
  if (selectedSuggestions.value.description && props.analysis?.suggestedDescription) count++
  if (selectedSuggestions.value.department && suggestedDepartment.value) count++
  if (selectedSuggestions.value.project && suggestedProject.value) count++
  if (selectedSuggestions.value.tags && props.analysis?.tags?.length) count++
  if (selectedSuggestions.value.fileType && suggestedFileType.value) count++
  if (selectedSuggestions.value.departmentCategory && suggestedDepartmentCategory.value) count++
  if (selectedSuggestions.value.visibility && props.analysis?.suggestedVisibility) count++
  return count
})

// Check if all applicable suggestions are selected
const allSelected = computed(() => {
  const applicableSuggestions = []
  if (props.analysis?.suggestedTitle) applicableSuggestions.push('title')
  if (props.analysis?.suggestedDescription) applicableSuggestions.push('description')
  if (suggestedDepartment.value) applicableSuggestions.push('department')
  if (suggestedProject.value) applicableSuggestions.push('project')
  if (props.analysis?.tags?.length) applicableSuggestions.push('tags')
  if (suggestedFileType.value) applicableSuggestions.push('fileType')
  if (suggestedDepartmentCategory.value) applicableSuggestions.push('departmentCategory')
  if (props.analysis?.suggestedVisibility) applicableSuggestions.push('visibility')
  
  return applicableSuggestions.length > 0 && 
         applicableSuggestions.every(key => selectedSuggestions.value[key as keyof typeof selectedSuggestions.value])
})

const emit = defineEmits<{
  'analyze-file': []
  'retry-analysis': []
  'apply-title': [title: string]
  'apply-description': [description: string]
  'apply-department': [departmentId: number]
  'apply-project': [projectId: number]
  'apply-tags': [tags: string]
  'apply-file-type': [fileTypeId: number]
  'apply-department-category': [categoryId: number, departmentId: number]
  'apply-visibility': [visibility: 'PRIVATE' | 'DEPARTMENT' | 'PUBLIC']
  'apply-all': [suggestions: {
    title?: string
    description?: string
    departmentId?: number
    projectId?: number
    tags?: string
    fileTypeId?: number
    departmentCategoryId?: number
    visibility?: 'PRIVATE' | 'DEPARTMENT' | 'PUBLIC'
  }]
}>()

const selectAllSuggestions = () => {
  const newValue = !allSelected.value
  if (props.analysis?.suggestedTitle) selectedSuggestions.value.title = newValue
  if (props.analysis?.suggestedDescription) selectedSuggestions.value.description = newValue
  if (suggestedDepartment.value) selectedSuggestions.value.department = newValue
  if (suggestedProject.value) selectedSuggestions.value.project = newValue
  if (props.analysis?.tags?.length) selectedSuggestions.value.tags = newValue
  if (suggestedFileType.value) selectedSuggestions.value.fileType = newValue
  if (suggestedDepartmentCategory.value) selectedSuggestions.value.departmentCategory = newValue
  if (props.analysis?.suggestedVisibility) selectedSuggestions.value.visibility = newValue
}

const applySelectedSuggestions = () => {
  const suggestions: any = {}
  
  if (selectedSuggestions.value.title && props.analysis?.suggestedTitle) {
    suggestions.title = props.analysis.suggestedTitle
  }
  if (selectedSuggestions.value.description && props.analysis?.suggestedDescription) {
    suggestions.description = props.analysis.suggestedDescription
  }
  if (selectedSuggestions.value.department && suggestedDepartment.value) {
    suggestions.departmentId = suggestedDepartment.value.id
  }
  if (selectedSuggestions.value.project && suggestedProject.value) {
    suggestions.projectId = suggestedProject.value.id
  }
  if (selectedSuggestions.value.tags && props.analysis?.tags?.length) {
    suggestions.tags = props.analysis.tags.join(', ')
  }
  if (selectedSuggestions.value.fileType && suggestedFileType.value) {
    suggestions.fileTypeId = suggestedFileType.value.id
  }
  if (selectedSuggestions.value.departmentCategory && suggestedDepartmentCategory.value) {
    suggestions.departmentCategoryId = suggestedDepartmentCategory.value.id
    suggestions.departmentId = suggestedDepartmentCategory.value.departmentId
  }
  if (selectedSuggestions.value.visibility && props.analysis?.suggestedVisibility) {
    suggestions.visibility = props.analysis.suggestedVisibility
  }
  
  emit('apply-all', suggestions)
}
</script>