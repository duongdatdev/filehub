import apiService, { type ApiResponse } from './api'
import type { FileAnalysisResponse } from './fileApi'
import departmentApi, { type Department } from './departmentApi'
import projectApi, { type Project } from './projectApi'
import fileTypeApi, { type FileType } from './fileTypeApi'
import departmentCategoryApi, { type DepartmentCategory } from './departmentCategoryApi'

export interface FileAnalysisRequest {
  fileName: string
  fileContent?: string
  contentType?: string
  departmentId?: number
  projectId?: number
  description?: string
}

export interface AnalysisRecommendationData {
  userDepartments: Department[]
  userProjects: Project[]
  allDepartments: Department[]
  allProjects: Project[]
  fileTypes: FileType[]
  departmentCategories: DepartmentCategory[]
}

export interface ComprehensiveRecommendations {
  recommendationData: AnalysisRecommendationData
  suggestions: {
    departments: Department[]
    projects: Project[]
    fileTypes: FileType[]
    categories: DepartmentCategory[]
    keywords: string[]
    title: string | null
    description: string | null
    tags: string[]
    priority: 'LOW' | 'MEDIUM' | 'HIGH'
    confidenceScores: {
      department: number
      project: number
      fileType: number
      category: number
    }
  }
}

export interface AnalysisCapabilities {
  supportedFormats: string[]
  supportedFormatsList: string[]
  maxFileSize: number
  geminiEnabled: boolean
  modelName: string
}

class AiAnalysisService {
  /**
   * Analyze a file using AI with enhanced context
   */
  async analyzeFile(file: File, options?: {
    departmentId?: number
    projectId?: number
    description?: string
    userDepartments?: Department[]
    userProjects?: Project[]
    availableFileTypes?: FileType[]
    availableDepartmentCategories?: DepartmentCategory[]
  }): Promise<ApiResponse<FileAnalysisResponse>> {
    const formData = new FormData()
    formData.append('file', file)
    
    if (options?.departmentId) {
      formData.append('departmentId', options.departmentId.toString())
    }
    if (options?.projectId) {
      formData.append('projectId', options.projectId.toString())
    }
    if (options?.description) {
      formData.append('description', options.description)
    }
    
    // Add context for better recommendations
    if (options?.userDepartments && options.userDepartments.length > 0) {
      formData.append('userDepartmentIds', options.userDepartments.map(d => d.id).join(','))
    }
    if (options?.userProjects && options.userProjects.length > 0) {
      formData.append('userProjectIds', options.userProjects.map(p => p.id).join(','))
    }

    try {
      return await apiService.post('/ai/analysis/file', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        timeout: 60000, // 60 seconds timeout for AI analysis
      })
    } catch (error: any) {
      // Handle specific error cases for better user experience
      if (error.response?.status === 503) {
        const customError = {
          ...error,
          message: 'AI service is temporarily overloaded. Please try again in a few minutes.',
          response: {
            ...error.response,
            data: {
              success: false,
              message: 'AI service is temporarily overloaded. Please try again in a few minutes.',
              data: null
            }
          }
        }
        throw customError
      }
      throw error
    }
  }

  /**
   * Analyze text content using AI
   */
  async analyzeText(request: FileAnalysisRequest): Promise<ApiResponse<FileAnalysisResponse>> {
    return await apiService.post('/ai/analysis/text', request, {
      timeout: 60000, // 60 seconds timeout for AI analysis
    })
  }

  /**
   * Get AI analysis capabilities
   */
  async getCapabilities(): Promise<ApiResponse<AnalysisCapabilities>> {
    return await apiService.get('/ai/analysis/capabilities')
  }

  /**
   * Check if a file can be analyzed based on its properties
   */
  async canAnalyzeFile(fileName: string, fileSize: number): Promise<boolean> {
    try {
      // Get capabilities from backend
      const capabilities = await this.getCapabilities()
      if (!capabilities.success || !capabilities.data) {
        console.warn('Could not fetch AI capabilities, using fallback')
        return this.canAnalyzeFileSync(fileName, fileSize)
      }

      const caps = capabilities.data as any
      const supportedFormats = caps.supportedFormatsList || caps.supportedFormats?.split(',').map((f: string) => f.trim()) || []
      const maxSize = typeof caps.maxFileSize === 'number' ? caps.maxFileSize : 10 * 1024 * 1024

      const extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.') + 1)
      const isSupportedType = supportedFormats.includes(extension)
      
      // Enhanced: Now supports metadata-only analysis for large files
      return caps.geminiEnabled && (isSupportedType || this.isCommonFileType(fileName, extension))
    } catch (error) {
      console.warn('Error checking AI capabilities:', error)
      return this.canAnalyzeFileSync(fileName, fileSize)
    }
  }

  /**
   * Get analysis capability for a file (full content vs metadata only)
   */
  async getAnalysisCapability(fileName: string, fileSize: number): Promise<'full' | 'metadata' | 'none'> {
    try {
      const capabilities = await this.getCapabilities()
      if (!capabilities.success || !capabilities.data) {
        return this.getAnalysisCapabilitySync(fileName, fileSize)
      }

      const caps = capabilities.data as any
      const supportedFormats = caps.supportedFormatsList || caps.supportedFormats?.split(',').map((f: string) => f.trim()) || []
      const maxSize = typeof caps.maxFileSize === 'number' ? caps.maxFileSize : 10 * 1024 * 1024

      if (!caps.geminiEnabled) {
        return 'none'
      }

      const extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.') + 1)
      const isSupportedType = supportedFormats.includes(extension)
      
      if (!isSupportedType && !this.isCommonFileType(fileName, extension)) {
        return 'none'
      }

      // If file is too large, only metadata analysis is available
      if (fileSize > maxSize) {
        return 'metadata'
      }

      // If supported format and small enough, full content analysis is available
      return isSupportedType ? 'full' : 'metadata'
    } catch (error) {
      console.warn('Error checking analysis capability:', error)
      return this.getAnalysisCapabilitySync(fileName, fileSize)
    }
  }

  /**
   * Check if file type is commonly supported (for metadata-only analysis)
   */
  private isCommonFileType(fileName: string, extension: string): boolean {
    // Common office documents, media files, etc.
    const commonTypes = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'jpg', 'jpeg', 'png', 'gif', 'mp4', 'mp3', 'wav', 'avi', 'mov', 'zip', 'rar']
    return commonTypes.includes(extension.toLowerCase())
  }

  /**
   * Synchronous fallback for checking if a file can be analyzed
   */
  canAnalyzeFileSync(fileName: string, fileSize: number): boolean {
    // Basic client-side checks (server will do final validation)
    // Match backend configuration: txt,pdf,doc,docx,md,json,xml,csv
    const supportedExtensions = [
      'txt', 'pdf', 'doc', 'docx', 'md', 'json', 'xml', 'csv'
    ]
    
    const extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.') + 1)
    const isSupportedType = supportedExtensions.includes(extension)
    
    // Enhanced: Allow metadata-only analysis for common file types even if large
    return isSupportedType || this.isCommonFileType(fileName, extension)
  }

  /**
   * Synchronous fallback for getting analysis capability
   */
  getAnalysisCapabilitySync(fileName: string, fileSize: number): 'full' | 'metadata' | 'none' {
    const supportedExtensions = ['txt', 'pdf', 'doc', 'docx', 'md', 'json', 'xml', 'csv']
    const extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.') + 1)
    const isSupportedType = supportedExtensions.includes(extension)
    const maxSize = 10 * 1024 * 1024 // 10MB limit

    if (!isSupportedType && !this.isCommonFileType(fileName, extension)) {
      return 'none'
    }

    if (fileSize > maxSize) {
      return 'metadata'
    }

    return isSupportedType ? 'full' : 'metadata'
  }

  /**
   * Format analysis results for display
   */
  formatAnalysisResults(analysis: FileAnalysisResponse): {
    summary: string
    suggestions: string[]
    metadata: Array<{ key: string; value: string }>
  } {
    const suggestions = [
      ...analysis.recommendations,
      ...(analysis.suggestedTitle ? [`Suggested title: ${analysis.suggestedTitle}`] : []),
      ...(analysis.departmentSuggestion ? [`Suggested department: ${analysis.departmentSuggestion}`] : []),
      ...(analysis.projectSuggestion ? [`Suggested project: ${analysis.projectSuggestion}`] : [])
    ]

    const metadata = Object.entries(analysis.extractedMetadata || {}).map(([key, value]) => ({
      key: key.charAt(0).toUpperCase() + key.slice(1),
      value: String(value)
    }))

    return {
      summary: analysis.summary,
      suggestions,
      metadata
    }
  }

  /**
   * Get all departments and projects available to the user for analysis recommendations
   */
  async getAnalysisRecommendationData(): Promise<AnalysisRecommendationData> {
    try {
      const [userDepartmentsResponse, userProjectsResponse, allDepartmentsResponse, allProjectsResponse, fileTypesResponse] = await Promise.all([
        departmentApi.getUserDepartments(),
        projectApi.getUserProjects(),
        departmentApi.getAll(),
        projectApi.getAll(),
        fileTypeApi.getAllFileTypes()
      ])

      const allDepartments = allDepartmentsResponse.data?.data || []
      
      // Get categories for all departments
      const categoryPromises = allDepartments.map(dept => 
        departmentCategoryApi.getCategoriesByDepartment(dept.id).catch(() => ({ data: [] }))
      )
      const categoryResponses = await Promise.all(categoryPromises)
      const allCategories = categoryResponses.flatMap(response => response.data || [])

      return {
        userDepartments: userDepartmentsResponse.data || [],
        userProjects: userProjectsResponse.data || [],
        allDepartments,
        allProjects: allProjectsResponse.data?.data || [],
        fileTypes: fileTypesResponse.data || [],
        departmentCategories: allCategories
      }
    } catch (error) {
      console.error('Error fetching analysis recommendation data:', error)
      return {
        userDepartments: [],
        userProjects: [],
        allDepartments: [],
        allProjects: [],
        fileTypes: [],
        departmentCategories: []
      }
    }
  }

  /**
   * Get smart recommendations based on file analysis and available departments/projects
   * @deprecated Use getComprehensiveRecommendations for full feature support
   */
  async getSmartRecommendations(fileName: string, fileContent?: string): Promise<{
    recommendationData: AnalysisRecommendationData
    suggestions: {
      departments: Department[]
      projects: Project[]
      keywords: string[]
    }
  }> {
    try {
      const comprehensive = await this.getComprehensiveRecommendations(fileName, fileContent)
      
      return {
        recommendationData: comprehensive.recommendationData,
        suggestions: {
          departments: comprehensive.suggestions.departments,
          projects: comprehensive.suggestions.projects,
          keywords: comprehensive.suggestions.keywords
        }
      }
    } catch (error) {
      console.error('Error getting smart recommendations:', error)
      throw error
    }
  }

  /**
   * Get comprehensive recommendations based on file analysis and content
   */
  async getComprehensiveRecommendations(fileName: string, fileContent?: string, fileExtension?: string): Promise<ComprehensiveRecommendations> {
    try {
      // Get all available data
      const recommendationData = await this.getAnalysisRecommendationData()
      
      // Extract and analyze content
      const keywords = this.extractKeywords(fileName, fileContent)
      const contentAnalysis = this.analyzeContentContext(fileContent, keywords)
      
      // Find relevant departments and projects
      const relevantDepartments = this.findRelevantDepartments(keywords, recommendationData.allDepartments)
      const relevantProjects = this.findRelevantProjects(keywords, recommendationData.allProjects)
      
      // Find relevant file types
      const relevantFileTypes = this.findRelevantFileTypes(fileName, contentAnalysis, recommendationData.fileTypes, fileExtension)
      
      // Find relevant categories based on selected department or content
      const relevantCategories = this.findRelevantCategories(keywords, contentAnalysis, recommendationData.departmentCategories)
      
      // Generate title and description suggestions
      const titleSuggestion = this.generateTitleSuggestion(fileName, contentAnalysis)
      const descriptionSuggestion = this.generateDescriptionSuggestion(contentAnalysis, keywords)
      
      // Generate tags
      const suggestedTags = this.generateTags(keywords, contentAnalysis)
      
      // Determine priority based on content analysis
      const priority = this.determinePriority(contentAnalysis, keywords)
      
      // Calculate confidence scores
      const confidenceScores = this.calculateConfidenceScores(
        relevantDepartments,
        relevantProjects,
        relevantFileTypes,
        relevantCategories,
        keywords,
        contentAnalysis
      )

      return {
        recommendationData,
        suggestions: {
          departments: relevantDepartments,
          projects: relevantProjects,
          fileTypes: relevantFileTypes,
          categories: relevantCategories,
          keywords,
          title: titleSuggestion,
          description: descriptionSuggestion,
          tags: suggestedTags,
          priority,
          confidenceScores
        }
      }
    } catch (error) {
      console.error('Error getting comprehensive recommendations:', error)
      throw error
    }
  }

  /**
   * Extract relevant keywords from filename and content
   */
  private extractKeywords(fileName: string, fileContent?: string): string[] {
    const keywords = new Set<string>()
    
    // Extract from filename
    const fileNameWords = fileName
      .replace(/\.[^/.]+$/, '') // Remove extension
      .split(/[_\-\s\.]+/) // Split on common separators
      .filter(word => word.length > 2) // Filter short words
      .map(word => word.toLowerCase())
    
    fileNameWords.forEach(word => keywords.add(word))
    
    // Extract from content (basic keyword extraction)
    if (fileContent) {
      const contentWords = fileContent
        .toLowerCase()
        .match(/\b[a-zA-Z]{3,}\b/g) // Find words 3+ chars
        ?.slice(0, 20) // Limit to first 20 words
        || []
      
      contentWords.forEach(word => keywords.add(word))
    }
    
    return Array.from(keywords)
  }

  /**
   * Find departments that might be relevant based on keywords
   */
  private findRelevantDepartments(keywords: string[], departments: Department[]): Department[] {
    return departments.filter(dept => {
      const deptText = `${dept.name} ${dept.description || ''}`.toLowerCase()
      return keywords.some(keyword => 
        deptText.includes(keyword) || 
        keyword.includes(dept.name.toLowerCase().split(' ')[0])
      )
    }).slice(0, 5) // Limit to top 5 matches
  }

  /**
   * Find projects that might be relevant based on keywords
   */
  private findRelevantProjects(keywords: string[], projects: Project[]): Project[] {
    return projects.filter(project => {
      const projectText = `${project.name} ${project.description || ''}`.toLowerCase()
      return keywords.some(keyword => 
        projectText.includes(keyword) || 
        keyword.includes(project.name.toLowerCase().split(' ')[0])
      )
    }).slice(0, 5) // Limit to top 5 matches
  }

  /**
   * Analyze content context to understand document type and purpose
   */
  private analyzeContentContext(fileContent?: string, keywords: string[] = []): {
    documentType: string
    purpose: string
    complexity: 'simple' | 'medium' | 'complex'
    topics: string[]
    sentiment: 'positive' | 'neutral' | 'negative'
    technicalLevel: 'basic' | 'intermediate' | 'advanced'
  } {
    if (!fileContent) {
      return {
        documentType: 'unknown',
        purpose: 'unknown',
        complexity: 'simple',
        topics: keywords,
        sentiment: 'neutral',
        technicalLevel: 'basic'
      }
    }

    const content = fileContent.toLowerCase()
    const wordCount = content.split(/\s+/).length

    // Determine document type
    let documentType = 'document'
    if (content.includes('meeting') || content.includes('agenda') || content.includes('minutes')) {
      documentType = 'meeting'
    } else if (content.includes('report') || content.includes('analysis') || content.includes('summary')) {
      documentType = 'report'
    } else if (content.includes('proposal') || content.includes('plan') || content.includes('strategy')) {
      documentType = 'planning'
    } else if (content.includes('manual') || content.includes('guide') || content.includes('instruction')) {
      documentType = 'documentation'
    } else if (content.includes('policy') || content.includes('procedure') || content.includes('rule')) {
      documentType = 'policy'
    } else if (content.includes('invoice') || content.includes('budget') || content.includes('financial')) {
      documentType = 'financial'
    }

    // Determine complexity
    let complexity: 'simple' | 'medium' | 'complex' = 'simple'
    if (wordCount > 1000) complexity = 'medium'
    if (wordCount > 3000) complexity = 'complex'

    // Extract topics
    const topicKeywords = [
      'hr', 'human resources', 'recruitment', 'employee',
      'finance', 'budget', 'accounting', 'invoice',
      'marketing', 'sales', 'customer', 'campaign',
      'it', 'technology', 'software', 'system',
      'operations', 'process', 'workflow', 'procedure',
      'legal', 'compliance', 'contract', 'agreement'
    ]
    
    const topics = topicKeywords.filter(topic => content.includes(topic))

    // Basic sentiment analysis
    const positiveWords = ['success', 'achievement', 'excellent', 'good', 'positive', 'improvement']
    const negativeWords = ['problem', 'issue', 'error', 'failure', 'negative', 'concern']
    
    const positiveCount = positiveWords.filter(word => content.includes(word)).length
    const negativeCount = negativeWords.filter(word => content.includes(word)).length
    
    let sentiment: 'positive' | 'neutral' | 'negative' = 'neutral'
    if (positiveCount > negativeCount) sentiment = 'positive'
    else if (negativeCount > positiveCount) sentiment = 'negative'

    // Technical level
    const technicalTerms = ['api', 'database', 'algorithm', 'protocol', 'framework', 'architecture']
    const technicalCount = technicalTerms.filter(term => content.includes(term)).length
    
    let technicalLevel: 'basic' | 'intermediate' | 'advanced' = 'basic'
    if (technicalCount > 2) technicalLevel = 'intermediate'
    if (technicalCount > 5) technicalLevel = 'advanced'

    return {
      documentType,
      purpose: documentType,
      complexity,
      topics: [...new Set([...topics, ...keywords])],
      sentiment,
      technicalLevel
    }
  }

  /**
   * Find relevant file types based on content and extension
   */
  private findRelevantFileTypes(fileName: string, contentAnalysis: any, fileTypes: FileType[], fileExtension?: string): FileType[] {
    const extension = fileExtension || fileName.split('.').pop()?.toLowerCase() || ''
    
    // First, find types that match the file extension
    const extensionMatches = fileTypes.filter(type => 
      type.allowedExtensions.some(ext => ext.toLowerCase() === extension)
    )

    // Then, find types that match content context
    const contentMatches = fileTypes.filter(type => {
      const typeName = type.name.toLowerCase()
      const typeDesc = (type.description || '').toLowerCase()
      
      return contentAnalysis.topics.some((topic: string) => 
        typeName.includes(topic) || typeDesc.includes(topic)
      )
    })

    // Combine and deduplicate
    const combined = [...extensionMatches, ...contentMatches]
    const unique = combined.filter((type, index, self) => 
      index === self.findIndex(t => t.id === type.id)
    )

    return unique.slice(0, 5)
  }

  /**
   * Find relevant categories based on content analysis
   */
  private findRelevantCategories(keywords: string[], contentAnalysis: any, categories: DepartmentCategory[]): DepartmentCategory[] {
    return categories.filter(category => {
      const categoryText = `${category.name} ${category.description || ''}`.toLowerCase()
      
      // Check against keywords
      const keywordMatch = keywords.some(keyword => categoryText.includes(keyword))
      
      // Check against content topics
      const topicMatch = contentAnalysis.topics.some((topic: string) => categoryText.includes(topic))
      
      // Check against document type
      const typeMatch = categoryText.includes(contentAnalysis.documentType)
      
      return keywordMatch || topicMatch || typeMatch
    }).slice(0, 5)
  }

  /**
   * Generate title suggestion based on filename and content analysis
   */
  private generateTitleSuggestion(fileName: string, contentAnalysis: any): string | null {
    const baseName = fileName.replace(/\.[^/.]+$/, '').replace(/[_-]/g, ' ')
    
    // If it's already a good title, suggest minor improvements
    if (baseName.length > 5 && baseName.includes(' ')) {
      const words = baseName.split(' ').map(word => 
        word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
      )
      
      // Add context based on document type
      if (contentAnalysis.documentType !== 'unknown' && contentAnalysis.documentType !== 'document') {
        const typeWord = contentAnalysis.documentType.charAt(0).toUpperCase() + contentAnalysis.documentType.slice(1)
        if (!words.some(word => word.toLowerCase().includes(contentAnalysis.documentType))) {
          words.push(typeWord)
        }
      }
      
      return words.join(' ')
    }
    
    return null
  }

  /**
   * Generate description suggestion based on content analysis
   */
  private generateDescriptionSuggestion(contentAnalysis: any, _keywords: string[]): string | null {
    if (contentAnalysis.documentType === 'unknown') return null
    
    const typeDescriptions: Record<string, string> = {
      meeting: 'Meeting documentation including agenda, discussions, and action items',
      report: 'Analytical report containing findings, insights, and recommendations',
      planning: 'Strategic planning document outlining objectives and implementation steps',
      documentation: 'Instructional guide or manual for processes and procedures',
      policy: 'Organizational policy document defining rules and guidelines',
      financial: 'Financial document including budgets, invoices, or accounting records'
    }
    
    let baseDescription = typeDescriptions[contentAnalysis.documentType] || 'Document containing relevant information'
    
    // Add complexity context
    if (contentAnalysis.complexity === 'complex') {
      baseDescription += ' with detailed analysis and comprehensive coverage'
    } else if (contentAnalysis.complexity === 'medium') {
      baseDescription += ' with moderate detail and scope'
    }
    
    // Add main topics if available
    if (contentAnalysis.topics.length > 0) {
      const mainTopics = contentAnalysis.topics.slice(0, 3).join(', ')
      baseDescription += ` focusing on ${mainTopics}`
    }
    
    return baseDescription
  }

  /**
   * Generate relevant tags based on content analysis
   */
  private generateTags(keywords: string[], contentAnalysis: any): string[] {
    const tags = new Set<string>()
    
    // Add document type as tag
    if (contentAnalysis.documentType !== 'unknown') {
      tags.add(contentAnalysis.documentType)
    }
    
    // Add complexity level
    tags.add(contentAnalysis.complexity)
    
    // Add technical level if not basic
    if (contentAnalysis.technicalLevel !== 'basic') {
      tags.add(contentAnalysis.technicalLevel)
    }
    
    // Add sentiment if not neutral
    if (contentAnalysis.sentiment !== 'neutral') {
      tags.add(contentAnalysis.sentiment)
    }
    
    // Add main topics
    contentAnalysis.topics.slice(0, 3).forEach((topic: string) => tags.add(topic))
    
    // Add relevant keywords (limit to avoid overload)
    keywords.slice(0, 5).forEach(keyword => tags.add(keyword))
    
    return Array.from(tags).slice(0, 8) // Limit to 8 tags
  }

  /**
   * Determine priority based on content analysis
   */
  private determinePriority(contentAnalysis: any, keywords: string[]): 'LOW' | 'MEDIUM' | 'HIGH' {
    let score = 0
    
    // Document type importance
    const highPriorityTypes = ['financial', 'policy', 'legal']
    const mediumPriorityTypes = ['report', 'planning']
    
    if (highPriorityTypes.includes(contentAnalysis.documentType)) score += 3
    else if (mediumPriorityTypes.includes(contentAnalysis.documentType)) score += 2
    else score += 1
    
    // Complexity adds to priority
    if (contentAnalysis.complexity === 'complex') score += 2
    else if (contentAnalysis.complexity === 'medium') score += 1
    
    // Urgent keywords
    const urgentKeywords = ['urgent', 'critical', 'important', 'priority', 'deadline']
    const hasUrgentKeywords = keywords.some(keyword => 
      urgentKeywords.some(urgent => keyword.includes(urgent))
    )
    if (hasUrgentKeywords) score += 2
    
    // Determine final priority
    if (score >= 5) return 'HIGH'
    if (score >= 3) return 'MEDIUM'
    return 'LOW'
  }

  /**
   * Calculate confidence scores for recommendations
   */
  private calculateConfidenceScores(
    departments: Department[],
    projects: Project[],
    fileTypes: FileType[],
    categories: DepartmentCategory[],
    keywords: string[],
    contentAnalysis: any
  ): {
    department: number
    project: number
    fileType: number
    category: number
  } {
    // Base scores on number of matches and relevance
    const departmentScore = Math.min(departments.length * 0.3 + (keywords.length > 0 ? 0.4 : 0), 1)
    const projectScore = Math.min(projects.length * 0.25 + (contentAnalysis.topics.length > 0 ? 0.5 : 0), 1)
    const fileTypeScore = Math.min(fileTypes.length * 0.4 + 0.3, 1) // File type is usually more certain
    const categoryScore = Math.min(categories.length * 0.2 + (contentAnalysis.documentType !== 'unknown' ? 0.6 : 0), 1)
    
    return {
      department: Math.round(departmentScore * 100) / 100,
      project: Math.round(projectScore * 100) / 100,
      fileType: Math.round(fileTypeScore * 100) / 100,
      category: Math.round(categoryScore * 100) / 100
    }
  }
}

export default new AiAnalysisService()
