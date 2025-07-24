import apiService, { type ApiResponse } from './api'

// File interfaces
export interface FileUploadRequest {
  title?: string
  description?: string
  categoryId?: number
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
  contentType?: string
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
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
    if (filters.contentType) params.append('contentType', filters.contentType)
    if (filters.page !== undefined) params.append('page', filters.page.toString())
    if (filters.size !== undefined) params.append('size', filters.size.toString())
    if (filters.sortBy) params.append('sortBy', filters.sortBy)
    if (filters.sortDirection) params.append('sortDirection', filters.sortDirection)

    return await apiService.get(`/files?${params.toString()}`)
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
