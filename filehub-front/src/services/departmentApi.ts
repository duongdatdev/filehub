import api from './api'

export interface Department {
  id: number
  name: string
  description?: string
  managerId?: number
  parentId?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  manager?: User
  parent?: Department
  subDepartments?: Department[]
  users?: User[]
  projects?: Project[]
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
  department?: Department
}

export interface Project {
  id: number
  name: string
  description?: string
  departmentId: number
  managerId?: number
  startDate?: string
  endDate?: string
  status: string
  priority: string
  budget?: number
  isActive: boolean
  createdAt: string
  updatedAt: string
  department?: Department
  manager?: User
}

export interface DepartmentFilters {
  name?: string
  managerId?: number
  isActive?: boolean
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: string
}

export interface DepartmentStats {
  userCount: number
  projectCount: number
}

const departmentApi = {
  // Get all active departments
  getAll(): Promise<{ data: { data: Department[] } }> {
    return api.get('/departments')
  },

  // Get departments with filters (admin only)
  search(filters: DepartmentFilters): Promise<{ data: { data: any } }> {
    const params = new URLSearchParams()
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params.append(key, value.toString())
      }
    })
    return api.get(`/departments/search?${params}`)
  },

  // Get department by ID
  getById(id: number): Promise<{ data: { data: Department } }> {
    return api.get(`/departments/${id}`)
  },

  // Get root departments
  getRoots(): Promise<{ data: { data: Department[] } }> {
    return api.get('/departments/root')
  },

  // Get sub-departments
  getSubDepartments(id: number): Promise<{ data: { data: Department[] } }> {
    return api.get(`/departments/${id}/subdepartments`)
  },

  // Create department (admin only)
  create(department: Partial<Department>): Promise<{ data: { data: Department } }> {
    return api.post('/departments', department)
  },

  // Update department (admin only)
  update(id: number, department: Partial<Department>): Promise<{ data: { data: Department } }> {
    return api.put(`/departments/${id}`, department)
  },

  // Delete department (admin only)
  delete(id: number): Promise<{ data: any }> {
    return api.delete(`/departments/${id}`)
  },

  // Get department statistics (admin only)
  getStats(id: number): Promise<{ data: { data: DepartmentStats } }> {
    return api.get(`/departments/${id}/stats`)
  }
}

export default departmentApi
