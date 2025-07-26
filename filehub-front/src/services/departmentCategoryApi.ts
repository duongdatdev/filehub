import api from './api'
import type { AxiosResponse } from 'axios'

export interface DepartmentCategory {
  id: number
  name: string
  description?: string
  departmentId: number
  color?: string
  icon?: string
  displayOrder: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateDepartmentCategoryRequest {
  name: string
  description?: string
  departmentId: number
  color?: string
  icon?: string
  displayOrder?: number
  isActive?: boolean
}

export interface UpdateDepartmentCategoryRequest extends CreateDepartmentCategoryRequest {}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

class DepartmentCategoryApi {
  async getCategoriesByDepartment(departmentId: number): Promise<ApiResponse<DepartmentCategory[]>> {
    const response: AxiosResponse<ApiResponse<DepartmentCategory[]>> = await api.get(`/department-categories/department/${departmentId}`)
    return response.data
  }

  async getAllCategoriesByDepartment(departmentId: number): Promise<ApiResponse<DepartmentCategory[]>> {
    const response: AxiosResponse<ApiResponse<DepartmentCategory[]>> = await api.get(`/department-categories/department/${departmentId}/all`)
    return response.data
  }

  async getCategoryById(id: number): Promise<ApiResponse<DepartmentCategory>> {
    const response: AxiosResponse<ApiResponse<DepartmentCategory>> = await api.get(`/department-categories/${id}`)
    return response.data
  }

  async createCategory(category: CreateDepartmentCategoryRequest): Promise<ApiResponse<DepartmentCategory>> {
    const response: AxiosResponse<ApiResponse<DepartmentCategory>> = await api.post('/department-categories', category)
    return response.data
  }

  async updateCategory(id: number, category: UpdateDepartmentCategoryRequest): Promise<ApiResponse<DepartmentCategory>> {
    const response: AxiosResponse<ApiResponse<DepartmentCategory>> = await api.put(`/department-categories/${id}`, category)
    return response.data
  }

  async deleteCategory(id: number): Promise<ApiResponse<void>> {
    const response: AxiosResponse<ApiResponse<void>> = await api.delete(`/department-categories/${id}`)
    return response.data
  }

  async deactivateCategory(id: number): Promise<ApiResponse<void>> {
    const response: AxiosResponse<ApiResponse<void>> = await api.patch(`/department-categories/${id}/deactivate`)
    return response.data
  }

  async getCategoryCountByDepartment(departmentId: number): Promise<ApiResponse<number>> {
    const response: AxiosResponse<ApiResponse<number>> = await api.get(`/department-categories/department/${departmentId}/count`)
    return response.data
  }
}

export default new DepartmentCategoryApi()
