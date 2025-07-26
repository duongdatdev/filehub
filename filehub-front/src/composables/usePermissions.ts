import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export type Permission = 
  | 'admin.users.view'
  | 'admin.users.edit'
  | 'admin.users.create'
  | 'admin.users.delete'
  | 'admin.departments.view'
  | 'admin.departments.edit'
  | 'admin.departments.create'
  | 'admin.departments.delete'
  | 'admin.projects.view'
  | 'admin.projects.edit'
  | 'admin.projects.create'
  | 'admin.projects.delete'
  | 'admin.files.view'
  | 'admin.files.delete'
  | 'admin.assignments.manage'
  | 'admin.reports.view'

export const usePermissions = () => {
  const authStore = useAuthStore()

  // Define role-based permissions
  const rolePermissions: Record<string, Permission[]> = {
    ADMIN: [
      'admin.users.view',
      'admin.users.edit',
      'admin.users.create',
      'admin.users.delete',
      'admin.departments.view',
      'admin.departments.edit',
      'admin.departments.create',
      'admin.departments.delete',
      'admin.projects.view',
      'admin.projects.edit',
      'admin.projects.create',
      'admin.projects.delete',
      'admin.files.view',
      'admin.files.delete',
      'admin.assignments.manage',
      'admin.reports.view'
    ],
    MANAGER: [
      'admin.users.view',
      'admin.users.edit',
      'admin.departments.view',
      'admin.projects.view',
      'admin.projects.edit',
      'admin.projects.create',
      'admin.files.view',
      'admin.assignments.manage',
      'admin.reports.view'
    ],
    USER: []
  }

  const userRole = computed(() => authStore.user?.role || 'USER')
  const userPermissions = computed(() => rolePermissions[userRole.value] || [])

  const hasPermission = (permission: Permission): boolean => {
    return userPermissions.value.includes(permission)
  }

  const hasAnyPermission = (permissions: Permission[]): boolean => {
    return permissions.some(permission => hasPermission(permission))
  }

  const hasAllPermissions = (permissions: Permission[]): boolean => {
    return permissions.every(permission => hasPermission(permission))
  }

  const canAccessAdmin = computed(() => {
    return ['ADMIN', 'MANAGER'].includes(userRole.value)
  })

  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isManager = computed(() => userRole.value === 'MANAGER')
  const isUser = computed(() => userRole.value === 'USER')

  // Specific permission checks for common actions
  const canManageUsers = computed(() => hasPermission('admin.users.edit'))
  const canManageDepartments = computed(() => hasPermission('admin.departments.edit'))
  const canManageProjects = computed(() => hasPermission('admin.projects.edit'))
  const canManageAssignments = computed(() => hasPermission('admin.assignments.manage'))
  const canViewReports = computed(() => hasPermission('admin.reports.view'))

  return {
    userRole,
    userPermissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    canAccessAdmin,
    isAdmin,
    isManager,
    isUser,
    canManageUsers,
    canManageDepartments,
    canManageProjects,
    canManageAssignments,
    canViewReports
  }
}
