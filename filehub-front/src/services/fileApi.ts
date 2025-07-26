import apiService, { type ApiResponse } from './api'

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
   * Upload a file with metadata
   */
  async uploadFile(formData: FormData): Promise<ApiResponse<FileResponse>> {
    return await apiService.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  }

  /**
   * Get user's files with optional filters
   */
  async getUserFiles(filters: FileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
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

    return await apiService.get(`/files?${params.toString()}`)
  }

  /**
   * Get all files with filters (admin only)
   */
  async getAllFiles(filters: AdminFileFilters = {}): Promise<ApiResponse<PageResponse<FileResponse>>> {
    const params = new URLSearchParams()
    
    if (filters.filename) params.append('filename', filters.filename)
    if (filters.departmentCategoryId) params.append('departmentCategoryId', filters.departmentCategoryId.toString())
    if (filters.departmentId) params.append('departmentId', filters.departmentId.toString())
    if (filters.projectId) params.append('projectId', filters.projectId.toString())
    if (filters.fileTypeId) params.append('fileTypeId', filters.fileTypeId.toString())
    if (filters.uploaderId) params.append('uploaderId', filters.uploaderId.toString())
    if (filters.contentType) params.append('contentType', filters.contentType)
    if (filters.page !== undefined) params.append('page', filters.page.toString())
    if (filters.size !== undefined) params.append('size', filters.size.toString())
    if (filters.sortBy) params.append('sortBy', filters.sortBy)
    if (filters.sortDirection) params.append('sortDirection', filters.sortDirection)

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
}

export const fileApi = new FileApiService()
