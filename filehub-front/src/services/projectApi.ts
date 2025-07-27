import api from './api'

export interface ProjectResponse {
  id: number
  name: string
  description?: string
  departmentId: number
  status: string // 'ACTIVE', 'COMPLETED', 'CANCELLED'
  createdAt: string
  updatedAt: string
  department?: Department
}

export interface Project extends ProjectResponse {}

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
  status?: string
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

  // Get projects by status
  getByStatus(status: string): Promise<{ data: { data: Project[] } }> {
    return api.get(`/projects/status/${status}`)
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
  },

  // Get user's assigned projects
  getUserProjects(): Promise<{ data: { data: Project[] } }> {
    return api.get('/projects/user')
  }
}

export default projectApi
