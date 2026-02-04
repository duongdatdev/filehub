import apiService, { type ApiResponse } from './api'

// AI Analysis interfaces
export interface FileAnalysisResponse {
  summary: string
  category: string
  tags: string[]
  departmentSuggestion?: string
  projectSuggestion?: string
  confidenceScore: number
  recommendations: string[]
  extractedMetadata: Record<string, any>
  suggestedTitle?: string
  suggestedDescription?: string
  // Enhanced recommendations
  suggestedFileTypeId?: number
  suggestedFileTypeName?: string
  suggestedDepartmentCategoryId?: number
  suggestedDepartmentCategoryName?: string
  suggestedVisibility?: 'PRIVATE' | 'DEPARTMENT' | 'PUBLIC'
  suggestedPriority?: 'LOW' | 'MEDIUM' | 'HIGH'
  contentAnalysis?: {
    documentType: string
    technicalLevel: 'BASIC' | 'INTERMEDIATE' | 'ADVANCED'
    estimatedImportance: 'LOW' | 'MEDIUM' | 'HIGH'
    suggestedAccess: string[]
    relatedKeywords: string[]
  }
}

export interface FileUploadWithAnalysisResponse {
  fileResponse: FileResponse
  analysisResponse?: FileAnalysisResponse
  analysisEnabled: boolean
  analysisMessage: string
}

// File interfaces
export interface FileUploadRequest {
  title?: string
  description?: string
  departmentCategoryId?: number
  departmentId?: number
  projectId?: number
  fileTypeId?: number
  tags?: string
  visibility?: 'PRIVATE' | 'DEPARTMENT' | 'PUBLIC'
  enableAiAnalysis?: boolean
}

export interface FileResponse {
  id: number
  originalFilename: string
  title?: string
  description?: string
  fileSize: number
  contentType: string
  fileHash: string
  uploaderId: number
  departmentId: number
  departmentCategoryId?: number
  fileTypeId: number
  projectId?: number
  visibility: string
  downloadCount: number
  isDeleted: boolean
  uploadedAt: string
  updatedAt: string
  deletedAt?: string
  driveFileId?: string
  driveFolderId?: string
  
  // Relationship data
  uploaderName?: string
  departmentName?: string
  departmentCategoryName?: string
  fileTypeName?: string
  projectName?: string
  tags?: string[]
  
  // URLs
  downloadUrl: string
  previewUrl: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface FileFilters {
  filename?: string
  departmentCategoryId?: number
  departmentId?: number
  projectId?: number
  fileTypeId?: number
  contentType?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export interface AdminFileFilters extends FileFilters {
  uploaderId?: number
}

class FileApiService {
  /**
   * Helper method to build URL search parameters from filters
   */
  private buildFilterParams(filters: FileFilters | AdminFileFilters): URLSearchParams {
    const params = new URLSearchParams()
    
    if (filters.filename) params.append('filename', filters.filename)
    if (filters.departmentCategoryId) params.append('departmentCategoryId', filters.departmentCategoryId.toString())
    if (filters.departmentId) params.append('departmentId', filters.departmentId.toString())
    if (filters.projectId) params.append('projectId', filters.projectId.toString())
    if (filters.fileTypeId) params.append('fileTypeId', filters.fileTypeId.toString())
    if (filters.contentType) params.append('contentType', filters.contentType)
    if (filters.page !== undefined) params.append('page', filters.page.toString())
    if (filters.size !== undefined) params.append('size', filters.size.toString())
    if (filters.sortBy) params.append('sortBy', filters.sortBy)
    if (filters.sortDirection) params.append('sortDirection', filters.sortDirection)
    
    // Add admin-specific filters if present
    if ('uploaderId' in filters && filters.uploaderId) {
      params.append('uploaderId', filters.uploaderId.toString())
    }
    
    return params
  }

  /**
   * Upload a file with metadata and optional AI analysis
   */
  async uploadFile(formData: FormData): Promise<ApiResponse<FileUploadWithAnalysisResponse>> {
    return await apiService.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      timeout: 300000, // 5 minutes timeout for file uploads (large files + Google Drive upload)
    })
  }

  /**
   * Analyze an existing file with AI
   */
  async analyzeExistingFile(
    fileId: number, 
    options?: {
      departmentId?: number
      projectId?: number
      description?: string
    }
  ): Promise<ApiResponse<FileAnalysisResponse>> {
    const params = new URLSearchParams()
    if (options?.departmentId) params.append('departmentId', options.departmentId.toString())
    if (options?.projectId) params.append('projectId', options.projectId.toString())
    if (options?.description) params.append('description', options.description)
    
    const url = `/files/${fileId}/analyze${params.toString() ? `?${params.toString()}` : ''}`
    return await apiService.post(url, undefined, {
      timeout: 60000, // 60 seconds timeout for AI analysis
    })
  }

  /**
   * Get user's files with optional filters
   */
  async getUserFiles(filters: FileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/files?${params.toString()}`)
  }

  /**
   * Get all files with filters (admin only)
   */
  async getAllFiles(filters: AdminFileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/files/admin/all?${params.toString()}`)
  }

  /**
   * Get files by department
   */
  async getFilesByDepartment(departmentId: number): Promise<ApiResponse<FileResponse[]>> {
    return await apiService.get(`/files/department/${departmentId}`)
  }

  /**
   * Get files by project
   */
  async getFilesByProject(projectId: number): Promise<ApiResponse<FileResponse[]>> {
    return await apiService.get(`/files/project/${projectId}`)
  }

  /**
   * Get file by ID
   */
  async getFileById(id: number): Promise<ApiResponse<FileResponse>> {
    return await apiService.get(`/files/${id}`)
  }

  /**
   * Download file
   */
  async downloadFile(id: number): Promise<Blob> {
    return await apiService.get(`/files/${id}/download`, {
      responseType: 'blob',
    })
  }

  /**
   * Preview file (for supported file types)
   */
  async previewFile(id: number): Promise<Blob> {
    return await apiService.get(`/files/${id}/preview`, {
      responseType: 'blob',
    })
  }

  /**
   * Delete file
   */
  async deleteFile(id: number): Promise<ApiResponse<void>> {
    return await apiService.delete(`/files/${id}`)
  }

  /**
   * Get shared files (all accessible files)
   */
  async getSharedFiles(filters: FileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/files/shared?${params.toString()}`)
  }

  /**
   * Get shared files by department
   */
  async getSharedFilesByDepartment(departmentId: number, filters: FileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/files/shared/department/${departmentId}?${params.toString()}`)
  }

  /**
   * Get shared files by project
   */
  async getSharedFilesByProject(projectId: number, filters: FileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/files/shared/project/${projectId}?${params.toString()}`)
  }

  /**
   * Admin: Get all shared files (admin only)
   */
  async getAdminSharedFiles(filters: AdminFileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = this.buildFilterParams(filters)
    return await apiService.get(`/admin/files/shared?${params.toString()}`)
  }
}

export const fileApi = new FileApiService()
