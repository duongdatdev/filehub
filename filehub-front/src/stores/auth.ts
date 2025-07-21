import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiService, type User, type LoginRequest, type RegisterRequest } from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // Actions
  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null

      const response = await apiService.login(credentials)
      
      if (response.success && response.data) {
        user.value = response.data
        // In a real app, you'd get the token from the response
        token.value = 'dummy-token'
        
        localStorage.setItem('user', JSON.stringify(response.data))
        localStorage.setItem('token', token.value)
        
        return true
      } else {
        error.value = response.message || 'Login failed'
        return false
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  const register = async (userData: RegisterRequest): Promise<boolean> => {
    try {
      loading.value = true
      error.value = null

      const response = await apiService.register(userData)
      
      if (response.success && response.data) {
        user.value = response.data
        token.value = 'dummy-token'
        
        localStorage.setItem('user', JSON.stringify(response.data))
        localStorage.setItem('token', token.value)
        
        return true
      } else {
        error.value = response.message || 'Registration failed'
        return false
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Registration failed'
      return false
    } finally {
      loading.value = false
    }
  }

  const logout = async (): Promise<void> => {
    try {
      await apiService.logout()
    } catch (err) {
      console.error('Logout error:', err)
    } finally {
      user.value = null
      token.value = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    }
  }

  const initializeAuth = (): void => {
    const storedUser = localStorage.getItem('user')
    const storedToken = localStorage.getItem('token')

    if (storedUser && storedToken) {
      try {
        user.value = JSON.parse(storedUser)
        token.value = storedToken
      } catch (err) {
        console.error('Failed to parse stored user data:', err)
        logout()
      }
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    user,
    token,
    loading,
    error,
    // Getters
    isAuthenticated,
    isAdmin,
    // Actions
    login,
    register,
    logout,
    initializeAuth,
    clearError
  }
})
