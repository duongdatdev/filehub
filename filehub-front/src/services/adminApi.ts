import apiService, { type ApiResponse, type User } from './api'

export interface AdminUserFilterRequest {
  username?: string
  email?: string
  fullName?: string
  role?: string
  isActive?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
}

export interface UpdateUserStatusRequest {
  isActive: boolean
}

class AdminApiService {
  // Get all users with filters and pagination
  async getUsers(filters?: AdminUserFilterRequest): Promise<ApiResponse<PageResponse<User>>> {
    const params = new URLSearchParams()
    
    if (filters) {
      if (filters.username) params.append('username', filters.username)
      if (filters.email) params.append('email', filters.email)
      if (filters.fullName) params.append('fullName', filters.fullName)
      if (filters.role) params.append('role', filters.role)
      if (filters.isActive !== undefined) params.append('isActive', filters.isActive.toString())
      if (filters.page !== undefined) params.append('page', filters.page.toString())
      if (filters.size !== undefined) params.append('size', filters.size.toString())
      if (filters.sortBy) params.append('sortBy', filters.sortBy)
      if (filters.sortDir) params.append('sortDir', filters.sortDir)
    }

    const queryString = params.toString()
    const url = `/admin/users${queryString ? `?${queryString}` : ''}`
    
    return apiService.get<ApiResponse<PageResponse<User>>>(url)
  }

  // Get user by ID
  async getUserById(id: number): Promise<ApiResponse<User>> {
    return apiService.get<ApiResponse<User>>(`/admin/users/${id}`)
  }

  // Update user status (active/inactive)
  async updateUserStatus(id: number, isActive: boolean): Promise<ApiResponse<User>> {
    const data: UpdateUserStatusRequest = { isActive }
    return apiService.patch<ApiResponse<User>>(`/admin/users/${id}/status`, data)
  }

  // Get user statistics (optional - if you want to add dashboard features)
  async getUserStats(): Promise<ApiResponse<{
    totalUsers: number
    activeUsers: number
    inactiveUsers: number
    adminUsers: number
    regularUsers: number
  }>> {
    return apiService.get<ApiResponse<any>>('/admin/users/stats')
  }
}

export const adminApi = new AdminApiService()
export default adminApi
