import { defineStore } from 'pinia'
import type { User } from '@/services/api'
import { adminApi, type AdminUserFilterRequest, type PageResponse } from '@/services/adminApi'

export interface AdminState {
  users: User[]
  totalElements: number
  totalPages: number
  currentPage: number
  pageSize: number
  loading: boolean
  error: string | null
  filters: AdminUserFilterRequest
  selectedUserIds: Set<number>
  bulkActionLoading: boolean
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
    },
    selectedUserIds: new Set<number>(),
    bulkActionLoading: false
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
    },
    selectedCount: (state) => state.selectedUserIds.size,
    allCurrentPageSelected: (state) => {
      if (state.users.length === 0) return false
      return state.users.every(user => state.selectedUserIds.has(user.id))
    },
    someCurrentPageSelected: (state) => {
      if (state.users.length === 0) return false
      return state.users.some(user => state.selectedUserIds.has(user.id)) && 
             !state.users.every(user => state.selectedUserIds.has(user.id))
    },
    selectedUsers: (state) => {
      return state.users.filter(user => state.selectedUserIds.has(user.id))
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

        const response = await adminApi.getUsers(this.filters)
        
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
        const response = await adminApi.getUserById(id)
        
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
        const response = await adminApi.updateUserStatus(id, isActive)
        
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
    },

    // Selection methods
    selectUser(userId: number) {
      this.selectedUserIds.add(userId)
    },

    deselectUser(userId: number) {
      this.selectedUserIds.delete(userId)
    },

    toggleUserSelection(userId: number) {
      if (this.selectedUserIds.has(userId)) {
        this.selectedUserIds.delete(userId)
      } else {
        this.selectedUserIds.add(userId)
      }
    },

    selectAllCurrentPage() {
      this.users.forEach(user => {
        this.selectedUserIds.add(user.id)
      })
    },

    deselectAllCurrentPage() {
      this.users.forEach(user => {
        this.selectedUserIds.delete(user.id)
      })
    },

    toggleSelectAllCurrentPage() {
      if (this.allCurrentPageSelected) {
        this.deselectAllCurrentPage()
      } else {
        this.selectAllCurrentPage()
      }
    },

    clearSelection() {
      this.selectedUserIds.clear()
    },

    // Bulk actions
    async bulkUpdateUserStatus(isActive: boolean) {
      if (this.selectedUserIds.size === 0) return false

      this.bulkActionLoading = true
      this.error = null

      try {
        const selectedIds = Array.from(this.selectedUserIds)
        const updatePromises = selectedIds.map(id => 
          adminApi.updateUserStatus(id, isActive)
        )

        const results = await Promise.allSettled(updatePromises)
        
        let successCount = 0
        let errorCount = 0

        results.forEach((result, index) => {
          const userId = selectedIds[index]
          if (result.status === 'fulfilled' && result.value.success) {
            // Update the user in the local state
            const userIndex = this.users.findIndex(user => user.id === userId)
            if (userIndex !== -1) {
              this.users[userIndex].isActive = isActive
            }
            successCount++
          } else {
            errorCount++
          }
        })

        if (errorCount > 0) {
          this.error = `${successCount} users updated successfully, ${errorCount} failed`
        }

        // Clear selection after bulk action
        this.clearSelection()
        
        return errorCount === 0
      } catch (error: any) {
        this.error = error.message || 'An error occurred during bulk update'
        console.error('Error in bulk update:', error)
        return false
      } finally {
        this.bulkActionLoading = false
      }
    }
  }
})
