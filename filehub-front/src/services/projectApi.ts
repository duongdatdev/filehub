import api from './api'

export interface Project {
  id: number
  name: string
  description?: string
  departmentId: number
  managerId?: number
  startDate?: string
  endDate?: string
  status: string // 'PLANNING', 'ACTIVE', 'COMPLETED', 'CANCELLED'
  priority: string // 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
  budget?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  department?: Department
  manager?: User
}

export interface Department {
  id: number
  name: string
  description?: string
  managerId?: number
  parentId?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface User {
  id: number
  username: string
  email: string
  fullName?: string
  role: string
  departmentId?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface ProjectFilters {
  name?: string
  departmentId?: number
  managerId?: number
  status?: string
  priority?: string
  isActive?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: string
}

export interface ProjectStats {
  fileCount: number
}

const projectApi = {
  // Get all active projects
  getAll(): Promise<{ data: { data: Project[] } }> {
    return api.get('/projects')
  },

  // Get projects with filters
  search(filters: ProjectFilters): Promise<{ data: { data: any } }> {
    const params = new URLSearchParams()
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params.append(key, value.toString())
      }
    })
    return api.get(`/projects/search?${params}`)
  },

  // Get project by ID
  getById(id: number): Promise<{ data: { data: Project } }> {
    return api.get(`/projects/${id}`)
  },

  // Get projects by department
  getByDepartment(departmentId: number): Promise<{ data: { data: Project[] } }> {
    return api.get(`/projects/department/${departmentId}`)
  },

  // Get projects by manager
  getByManager(managerId: number): Promise<{ data: { data: Project[] } }> {
    return api.get(`/projects/manager/${managerId}`)
  },

  // Get projects by status
  getByStatus(status: string): Promise<{ data: { data: Project[] } }> {
    return api.get(`/projects/status/${status}`)
  },

  // Get overdue projects (admin only)
  getOverdue(): Promise<{ data: { data: Project[] } }> {
    return api.get('/projects/overdue')
  },

  // Get projects due soon (admin only)
  getDueSoon(days: number = 7): Promise<{ data: { data: Project[] } }> {
    return api.get(`/projects/due-soon?days=${days}`)
  },

  // Create project (admin only)
  create(project: Partial<Project>): Promise<{ data: { data: Project } }> {
    return api.post('/projects', project)
  },

  // Update project (admin only)
  update(id: number, project: Partial<Project>): Promise<{ data: { data: Project } }> {
    return api.put(`/projects/${id}`, project)
  },

  // Update project status (admin only)
  updateStatus(id: number, status: string): Promise<{ data: { data: Project } }> {
    return api.patch(`/projects/${id}/status?status=${status}`)
  },

  // Delete project (admin only)
  delete(id: number): Promise<{ data: any }> {
    return api.delete(`/projects/${id}`)
  },

  // Get project statistics
  getStats(id: number): Promise<{ data: { data: ProjectStats } }> {
    return api.get(`/projects/${id}/stats`)
  }
}

export default projectApi
