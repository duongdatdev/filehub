import apiService, { type ApiResponse } from './api'

// File interfaces
export interface FileUploadRequest {
  title?: string
  description?: string
  categoryId?: number
  departmentId?: number
  projectId?: number
  tags?: string
  visibility?: 'PRIVATE' | 'PUBLIC' | 'SHARED'
}

export interface FileResponse {
  id: number
  originalFilename: string
  title?: string
  description?: string
  fileSize: number
  contentType: string
  visibility: string
  downloadCount: number
  version: number
  uploadedAt: string
  updatedAt: string
  categoryId?: number
  categoryName?: string
  departmentId?: number
  departmentName?: string
  projectId?: number
  projectName?: string
  driveFileId?: string
  driveFolderId?: string
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
  categoryId?: number
  departmentId?: number
  projectId?: number
  contentType?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export interface AdminFileFilters extends FileFilters {
  userId?: number
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
    if (filters.categoryId) params.append('categoryId', filters.categoryId.toString())
    if (filters.departmentId) params.append('departmentId', filters.departmentId.toString())
    if (filters.projectId) params.append('projectId', filters.projectId.toString())
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
    if (filters.categoryId) params.append('categoryId', filters.categoryId.toString())
    if (filters.departmentId) params.append('departmentId', filters.departmentId.toString())
    if (filters.projectId) params.append('projectId', filters.projectId.toString())
    if (filters.userId) params.append('userId', filters.userId.toString())
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
