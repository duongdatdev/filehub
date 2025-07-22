import { defineStore } from 'pinia'
import type { User } from '@/services/api'
import { adminApi, type AdminUserFilterRequest, type PageResponse } from '@/services/adminApi'

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

export interface AdminState {
  users: User[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
  loading: boolean
  error: string | null
  filters: AdminUserFilterRequest
}

export const useAdminStore = defineStore('admin', {
  state: (): AdminState => ({
    users: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 10,
    loading: false,
    error: null,
    filters: {
      page: 0,
      size: 10,
      sortBy: 'createdAt',
      sortDir: 'desc'
    }
  }),

  getters: {
    hasUsers: (state) => state.users.length > 0,
    isFirstPage: (state) => state.currentPage === 0,
    isLastPage: (state) => state.currentPage >= state.totalPages - 1,
    activeFiltersCount: (state) => {
      let count = 0
      if (state.filters.username) count++
      if (state.filters.email) count++
      if (state.filters.fullName) count++
      if (state.filters.role) count++
      if (state.filters.isActive !== undefined) count++
      return count
    }
  },

  actions: {
    async fetchUsers(filters?: Partial<AdminUserFilterRequest>) {
      this.loading = true
      this.error = null

      try {
        // Update filters if provided
        if (filters) {
          this.filters = { ...this.filters, ...filters }
        }

        const response = await this.adminApi.getUsers(this.filters)
        
        if (response.success) {
          const pageData = response.data as PageResponse<User>
          this.users = pageData.content
          this.totalElements = pageData.totalElements
          this.totalPages = pageData.totalPages
          this.currentPage = pageData.number
          this.pageSize = pageData.size
        } else {
          throw new Error(response.message || 'Failed to fetch users')
        }
      } catch (error: any) {
        this.error = error.message || 'An error occurred while fetching users'
        console.error('Error fetching users:', error)
      } finally {
        this.loading = false
      }
    },

    async getUserById(id: number): Promise<User | null> {
      this.loading = true
      this.error = null

      try {
        const response = await this.adminApi.getUserById(id)
        
        if (response.success) {
          return response.data as User
        } else {
          throw new Error(response.message || 'Failed to fetch user')
        }
      } catch (error: any) {
        this.error = error.message || 'An error occurred while fetching user'
        console.error('Error fetching user:', error)
        return null
      } finally {
        this.loading = false
      }
    },

    async updateUserStatus(id: number, isActive: boolean) {
      this.loading = true
      this.error = null

      try {
        const response = await this.adminApi.updateUserStatus(id, isActive)
        
        if (response.success) {
          // Update the user in the local state
          const userIndex = this.users.findIndex(user => user.id === id)
          if (userIndex !== -1) {
            this.users[userIndex].isActive = isActive
          }
          return true
        } else {
          throw new Error(response.message || 'Failed to update user status')
        }
      } catch (error: any) {
        this.error = error.message || 'An error occurred while updating user status'
        console.error('Error updating user status:', error)
        return false
      } finally {
        this.loading = false
      }
    },

    setPage(page: number) {
      this.filters.page = page
      this.fetchUsers()
    },

    setPageSize(size: number) {
      this.filters.size = size
      this.filters.page = 0 // Reset to first page
      this.fetchUsers()
    },

    setSorting(sortBy: string, sortDir: 'asc' | 'desc' = 'asc') {
      this.filters.sortBy = sortBy
      this.filters.sortDir = sortDir
      this.filters.page = 0 // Reset to first page
      this.fetchUsers()
    },

    applyFilters(filters: Partial<AdminUserFilterRequest>) {
      this.filters = { ...this.filters, ...filters, page: 0 } // Reset to first page
      this.fetchUsers()
    },

    clearFilters() {
      this.filters = {
        page: 0,
        size: this.pageSize,
        sortBy: 'createdAt',
        sortDir: 'desc'
      }
      this.fetchUsers()
    },

    clearError() {
      this.error = null
    }
  }
})

// Import admin API methods (we'll add these to the API service)
import { adminApi } from '@/services/adminApi'
useAdminStore.prototype.adminApi = adminApi
