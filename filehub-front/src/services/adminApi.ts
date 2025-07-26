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
  userCount?: number
  projectCount?: number
}

export interface UserDepartment {
  id: number
  userId: number
  departmentId: number
  role: string
  isActive: boolean
  assignedAt: string
  assignedBy?: number
}

export interface UserProject {
  id: number
  userId: number
  projectId: number
  role: string
  isActive: boolean
  assignedAt: string
  assignedBy?: number
}

export interface Project {
  id: number
  name: string
  description?: string
  departmentId: number
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED'
  createdAt: string
  updatedAt?: string
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
    return apiService.get<ApiResponse<Department[]>>('/admin/departments')
  }

  async getDepartmentsWithStats(): Promise<ApiResponse<Department[]>> {
    const response = await this.getDepartments()
    if (response.success && response.data) {
      // Get stats for each department
      const departmentsWithStats = await Promise.all(
        response.data.map(async (dept) => {
          try {
            const statsResponse = await apiService.get<ApiResponse<{userCount: number, projectCount: number}>>(`/admin/departments/${dept.id}/stats`)
            if (statsResponse.success && statsResponse.data) {
              return {
                ...dept,
                userCount: statsResponse.data.userCount,
                projectCount: statsResponse.data.projectCount
              }
            }
          } catch (error) {
            console.warn(`Failed to get stats for department ${dept.id}:`, error)
          }
          return {
            ...dept,
            userCount: 0,
            projectCount: 0
          }
        })
      )
      return { ...response, data: departmentsWithStats }
    }
    return response
  }

  async getDepartmentById(id: number): Promise<ApiResponse<Department>> {
    return apiService.get<ApiResponse<Department>>(`/admin/departments/${id}`)
  }

  async createDepartment(department: Omit<Department, 'id' | 'createdAt'>): Promise<ApiResponse<Department>> {
    return apiService.post<ApiResponse<Department>>('/admin/departments', department)
  }

  async updateDepartment(id: number, department: Partial<Department>): Promise<ApiResponse<Department>> {
    return apiService.put<ApiResponse<Department>>(`/admin/departments/${id}`, department)
  }

  async deleteDepartment(id: number): Promise<ApiResponse<boolean>> {
    return apiService.delete<ApiResponse<boolean>>(`/admin/departments/${id}`)
  }

  // Project Management
  async getProjects(): Promise<ApiResponse<Project[]>> {
    return apiService.get<ApiResponse<Project[]>>('/admin/projects')
  }

  async getProjectById(id: number): Promise<ApiResponse<Project>> {
    return apiService.get<ApiResponse<Project>>(`/admin/projects/${id}`)
  }

  async createProject(project: Omit<Project, 'id' | 'createdAt'>): Promise<ApiResponse<Project>> {
    return apiService.post<ApiResponse<Project>>('/admin/projects', project)
  }

  async updateProject(id: number, project: Partial<Project>): Promise<ApiResponse<Project>> {
    return apiService.put<ApiResponse<Project>>(`/admin/projects/${id}`, project)
  }

  async updateProjectStatus(id: number, status: Project['status']): Promise<ApiResponse<Project>> {
    return apiService.patch<ApiResponse<Project>>(`/admin/projects/${id}/status?status=${status}`)
  }

  async deleteProject(id: number): Promise<ApiResponse<boolean>> {
    return apiService.delete<ApiResponse<boolean>>(`/admin/projects/${id}`)
  }

  // Enhanced User Management with Assignments
  async getUserDetails(id: number): Promise<ApiResponse<AdminUserDetailResponse>> {
    return apiService.get<ApiResponse<AdminUserDetailResponse>>(`/admin/users/${id}/details`)
  }

  async getUserDepartments(userId: number): Promise<ApiResponse<UserDepartment[]>> {
    return apiService.get<ApiResponse<UserDepartment[]>>(`/admin/users/${userId}/departments`)
  }

  async getUserProjects(userId: number): Promise<ApiResponse<UserProject[]>> {
    return apiService.get<ApiResponse<UserProject[]>>(`/admin/users/${userId}/projects`)
  }

  // Get projects available for assignment to a specific user (only from departments they belong to)
  async getAvailableProjectsForUser(userId: number): Promise<ApiResponse<Project[]>> {
    try {
      // Get user's departments
      const userDepartmentsResponse = await this.getUserDepartments(userId)
      if (!userDepartmentsResponse.success || !userDepartmentsResponse.data) {
        throw new Error('Failed to fetch user departments')
      }

      // Get all projects
      const allProjectsResponse = await this.getProjects()
      if (!allProjectsResponse.success || !allProjectsResponse.data) {
        throw new Error('Failed to fetch projects')
      }

      // Extract department IDs from user departments
      const userDepartmentIds = userDepartmentsResponse.data.map(userDept => userDept.departmentId)
      
      // Filter projects to only include those from departments where user is a member
      const availableProjects = allProjectsResponse.data.filter(project => 
        userDepartmentIds.includes(project.departmentId)
      )

      return {
        success: true,
        message: 'Available projects retrieved successfully',
        data: availableProjects
      }
    } catch (error: any) {
      return {
        success: false,
        message: error.message || 'Failed to fetch available projects for user',
        data: []
      }
    }
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
