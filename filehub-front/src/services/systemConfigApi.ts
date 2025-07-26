import api from './api'
import type { AxiosResponse } from 'axios'

export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  dataType: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON'
  description?: string
  isPublic: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateSystemConfigRequest {
  configKey: string
  configValue: string
  dataType?: 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON'
  description?: string
  isPublic?: boolean
}

export interface UpdateSystemConfigRequest {
  configValue: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

class SystemConfigApi {
  async getAllConfigs(): Promise<ApiResponse<SystemConfig[]>> {
    const response: AxiosResponse<ApiResponse<SystemConfig[]>> = await api.get('/system-configs')
    return response.data
  }

  async getPublicConfigs(): Promise<ApiResponse<SystemConfig[]>> {
    const response: AxiosResponse<ApiResponse<SystemConfig[]>> = await api.get('/system-configs/public')
    return response.data
  }

  async getConfigByKey(configKey: string): Promise<ApiResponse<SystemConfig>> {
    const response: AxiosResponse<ApiResponse<SystemConfig>> = await api.get(`/system-configs/${configKey}`)
    return response.data
  }

  async getConfigValue(configKey: string): Promise<ApiResponse<string>> {
    const response: AxiosResponse<ApiResponse<string>> = await api.get(`/system-configs/${configKey}/value`)
    return response.data
  }

  async createOrUpdateConfig(config: CreateSystemConfigRequest): Promise<ApiResponse<SystemConfig>> {
    const response: AxiosResponse<ApiResponse<SystemConfig>> = await api.post('/system-configs', config)
    return response.data
  }

  async updateConfigValue(configKey: string, configValue: string): Promise<ApiResponse<SystemConfig>> {
    const response: AxiosResponse<ApiResponse<SystemConfig>> = await api.put(`/system-configs/${configKey}`, { configValue })
    return response.data
  }

  async deleteConfig(configKey: string): Promise<ApiResponse<void>> {
    const response: AxiosResponse<ApiResponse<void>> = await api.delete(`/system-configs/${configKey}`)
    return response.data
  }

  async getConfigsByDataType(dataType: string): Promise<ApiResponse<SystemConfig[]>> {
    const response: AxiosResponse<ApiResponse<SystemConfig[]>> = await api.get(`/system-configs/type/${dataType}`)
    return response.data
  }

  // Helper methods for common config values
  async getMaxFileSize(): Promise<number> {
    try {
      const response = await this.getConfigValue('file.max-size')
      return parseInt(response.data) || 104857600 // Default 100MB
    } catch {
      return 104857600
    }
  }

  async getAllowedFileTypes(): Promise<string[]> {
    try {
      const response = await this.getConfigValue('file.allowed-types')
      return JSON.parse(response.data) || []
    } catch {
      return []
    }
  }

  async getAppName(): Promise<string> {
    try {
      const response = await this.getConfigValue('app.name')
      return response.data || 'FileHub'
    } catch {
      return 'FileHub'
    }
  }

  async getAppVersion(): Promise<string> {
    try {
      const response = await this.getConfigValue('app.version')
      return response.data || '1.0.0'
    } catch {
      return '1.0.0'
    }
  }
}

export default new SystemConfigApi()
