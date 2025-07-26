import api from './api'
import type { AxiosResponse } from 'axios'

export interface FileType {
  id: number
  name: string
  description?: string
  allowedExtensions: string[]
  color?: string
  icon?: string
  maxSize?: number
  createdAt: string
  updatedAt: string
}

export interface CreateFileTypeRequest {
  name: string
  description?: string
  allowedExtensions: string[]
  color?: string
  icon?: string
  maxSize?: number
}

export interface UpdateFileTypeRequest extends CreateFileTypeRequest {}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

class FileTypeApi {
  async getAllFileTypes(): Promise<ApiResponse<FileType[]>> {
    const response: AxiosResponse<ApiResponse<FileType[]>> = await api.get('/file-types')
    return response.data
  }

  async getFileTypeById(id: number): Promise<ApiResponse<FileType>> {
    const response: AxiosResponse<ApiResponse<FileType>> = await api.get(`/file-types/${id}`)
    return response.data
  }

  async getFileTypeByName(name: string): Promise<ApiResponse<FileType>> {
    const response: AxiosResponse<ApiResponse<FileType>> = await api.get(`/file-types/name/${name}`)
    return response.data
  }

  async createFileType(fileType: CreateFileTypeRequest): Promise<ApiResponse<FileType>> {
    const response: AxiosResponse<ApiResponse<FileType>> = await api.post('/file-types', fileType)
    return response.data
  }

  async updateFileType(id: number, fileType: UpdateFileTypeRequest): Promise<ApiResponse<FileType>> {
    const response: AxiosResponse<ApiResponse<FileType>> = await api.put(`/file-types/${id}`, fileType)
    return response.data
  }

  async deleteFileType(id: number): Promise<ApiResponse<void>> {
    const response: AxiosResponse<ApiResponse<void>> = await api.delete(`/file-types/${id}`)
    return response.data
  }

  async getAllowedExtensions(typeName: string): Promise<ApiResponse<string[]>> {
    const response: AxiosResponse<ApiResponse<string[]>> = await api.get(`/file-types/${typeName}/extensions`)
    return response.data
  }
}

export default new FileTypeApi()
