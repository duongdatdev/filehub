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

export interface DashboardStats {
  totalUsers: number
  totalDepartments: number
  activeProjects: number
  totalFiles: number
}

export interface RecentActivity {
  id: number
  type: 'user' | 'project' | 'department' | 'file'
  description: string
  user: string
  timestamp: string
}

export interface Department {
  id: number
  name: string
  description?: string
  parentId?: number
  managerId?: number
  isActive: boolean
  createdAt: string
}

export interface Project {
  id: number
  name: string
  description?: string
  departmentId: number
  managerId: number
  status: 'PLANNING' | 'IN_PROGRESS' | 'COMPLETED' | 'ON_HOLD' | 'CANCELLED'
  startDate: string
  endDate?: string
  isActive: boolean
  createdAt: string
}

export interface UserAssignmentRequest {
  userId: number
  departmentId?: number
  projectIds?: number[]
}

export interface AdminUserDetailResponse {
  id: number
  username: string
  email: string
  fullName: string
  role: string
  isActive: boolean
  createdAt: string
  departments: Array<{
    id: number
    name: string
    role: string
  }>
  projects: Array<{
    id: number
    name: string
    role: string
  }>
}

export interface AssignUserToDepartmentRequest {
  departmentId: number
  role?: string
}

export interface AssignUserToProjectRequest {
  projectId: number
  role?: string
}

export interface BatchUserAssignmentRequest {
  userIds: number[]
  departmentId?: number
  projectId?: number
  operation: 'ADD' | 'REMOVE' | 'UPDATE_ROLE'
  role?: string
}

class AdminApiService {
  // Dashboard Stats
  async getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
    return apiService.get<ApiResponse<DashboardStats>>('/admin/dashboard/stats')
  }

  async getRecentActivity(): Promise<ApiResponse<RecentActivity[]>> {
    return apiService.get<ApiResponse<RecentActivity[]>>('/admin/dashboard/recent-activity')
  }

  // User Management
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

  async getUserById(id: number): Promise<ApiResponse<User>> {
    return apiService.get<ApiResponse<User>>(`/admin/users/${id}`)
  }

  async updateUserStatus(id: number, isActive: boolean): Promise<ApiResponse<User>> {
    const data: UpdateUserStatusRequest = { isActive }
    return apiService.patch<ApiResponse<User>>(`/admin/users/${id}/status`, data)
  }

  // Department Management
  async getDepartments(): Promise<ApiResponse<Department[]>> {
    return apiService.get<ApiResponse<Department[]>>('/departments')
  }

  async getDepartmentById(id: number): Promise<ApiResponse<Department>> {
    return apiService.get<ApiResponse<Department>>(`/departments/${id}`)
  }

  async createDepartment(department: Omit<Department, 'id' | 'createdAt'>): Promise<ApiResponse<Department>> {
    return apiService.post<ApiResponse<Department>>('/departments', department)
  }

  async updateDepartment(id: number, department: Partial<Department>): Promise<ApiResponse<Department>> {
    return apiService.put<ApiResponse<Department>>(`/departments/${id}`, department)
  }

  async deleteDepartment(id: number): Promise<ApiResponse<boolean>> {
    return apiService.delete<ApiResponse<boolean>>(`/departments/${id}`)
  }

  // Project Management
  async getProjects(): Promise<ApiResponse<Project[]>> {
    return apiService.get<ApiResponse<Project[]>>('/projects')
  }

  async getProjectById(id: number): Promise<ApiResponse<Project>> {
    return apiService.get<ApiResponse<Project>>(`/projects/${id}`)
  }

  async createProject(project: Omit<Project, 'id' | 'createdAt'>): Promise<ApiResponse<Project>> {
    return apiService.post<ApiResponse<Project>>('/projects', project)
  }

  async updateProject(id: number, project: Partial<Project>): Promise<ApiResponse<Project>> {
    return apiService.put<ApiResponse<Project>>(`/projects/${id}`, project)
  }

  async updateProjectStatus(id: number, status: Project['status']): Promise<ApiResponse<Project>> {
    return apiService.patch<ApiResponse<Project>>(`/projects/${id}/status`, { status })
  }

  async deleteProject(id: number): Promise<ApiResponse<boolean>> {
    return apiService.delete<ApiResponse<boolean>>(`/projects/${id}`)
  }

  // Enhanced User Management with Assignments
  async getUserDetails(id: number): Promise<ApiResponse<AdminUserDetailResponse>> {
    return apiService.get<ApiResponse<AdminUserDetailResponse>>(`/admin/users/${id}/details`)
  }

  async getUserDepartments(userId: number): Promise<ApiResponse<Department[]>> {
    return apiService.get<ApiResponse<Department[]>>(`/admin/users/${userId}/departments`)
  }

  async getUserProjects(userId: number): Promise<ApiResponse<Project[]>> {
    return apiService.get<ApiResponse<Project[]>>(`/admin/users/${userId}/projects`)
  }

  // User Assignment Management
  async assignUserToDepartment(userId: number, request: AssignUserToDepartmentRequest): Promise<ApiResponse<User>> {
    return apiService.post<ApiResponse<User>>(`/admin/users/${userId}/department`, request)
  }

  async assignUserToProject(userId: number, request: AssignUserToProjectRequest): Promise<ApiResponse<User>> {
    return apiService.post<ApiResponse<User>>(`/admin/users/${userId}/projects`, request)
  }

  async removeUserFromDepartment(userId: number, departmentId: number): Promise<ApiResponse<User>> {
    return apiService.delete<ApiResponse<User>>(`/admin/users/${userId}/departments/${departmentId}`)
  }

  async removeUserFromProject(userId: number, projectId: number): Promise<ApiResponse<User>> {
    return apiService.delete<ApiResponse<User>>(`/admin/users/${userId}/projects/${projectId}`)
  }

  async updateUserDepartmentRole(userId: number, departmentId: number, role: string): Promise<ApiResponse<User>> {
    return apiService.put<ApiResponse<User>>(`/admin/users/${userId}/departments/${departmentId}/role?role=${role}`)
  }

  async updateUserProjectRole(userId: number, projectId: number, role: string): Promise<ApiResponse<User>> {
    return apiService.put<ApiResponse<User>>(`/admin/users/${userId}/projects/${projectId}/role?role=${role}`)
  }

  // Batch Operations
  async batchUpdateUserAssignments(request: BatchUserAssignmentRequest): Promise<ApiResponse<User[]>> {
    return apiService.post<ApiResponse<User[]>>('/admin/users/batch-update', request)
  }

  // Statistics (for backward compatibility)
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
