import apiService, { type ApiResponse } from './api'

export interface FileCategory {
  id: number
  name: string
  description?: string
  color?: string
  icon?: string
  parentId?: number
  displayOrder: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

class CategoryApiService {
  /**
   * Get all categories
   */
  async getCategories(): Promise<ApiResponse<FileCategory[]>> {
    return await apiService.get('/categories')
  }

  /**
   * Get category by ID
   */
  async getCategoryById(id: number): Promise<ApiResponse<FileCategory>> {
    return await apiService.get(`/categories/${id}`)
  }

  /**
   * Create new category (admin only)
   */
  async createCategory(category: Partial<FileCategory>): Promise<ApiResponse<FileCategory>> {
    return await apiService.post('/categories', category)
  }

  /**
   * Update category (admin only)
   */
  async updateCategory(id: number, category: Partial<FileCategory>): Promise<ApiResponse<FileCategory>> {
    return await apiService.put(`/categories/${id}`, category)
  }

  /**
   * Delete category (admin only)
   */
  async deleteCategory(id: number): Promise<ApiResponse<void>> {
    return await apiService.delete(`/categories/${id}`)
  }
}

export const categoryApi = new CategoryApiService()
