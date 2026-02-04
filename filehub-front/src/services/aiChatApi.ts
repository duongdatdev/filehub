import { fileApi, type FileResponse } from './fileApi'

export interface ChatMessage {
  id: string
  type: 'user' | 'assistant'
  content: string
  timestamp: Date
  suggestions?: FileSuggestion[]
  thinking?: boolean
}

export interface FileSuggestion {
  file: FileResponse
  relevanceScore: number
  reason: string
}

export interface ChatResponse {
  message: string
  suggestions?: FileSuggestion[]
  searchQuery?: string
  filters?: {
    filename?: string
    departmentId?: number
    projectId?: number
    fileTypeId?: number
    tags?: string[]
  }
}

// Backend API interfaces
interface AiChatRequest {
  message: string
  conversationId?: string
}

interface AiChatResponse {
  message: string
  suggestions: Array<{
    file: FileResponse
    relevanceScore: number
    reason: string
  }>
  conversationId: string
}

class AiChatService {
  private baseUrl = 'http://localhost:8080/api/ai'
  private conversationId: string | null = null

  /**
   * Send a message to AI and get file search recommendations
   */
  async chatWithAI(message: string, _conversationHistory: ChatMessage[] = []): Promise<ChatResponse> {
    try {
      // Call the backend AI chat API
      const response = await this.callBackendApi(message)
      
      return {
        message: response.message,
        suggestions: response.suggestions,
        searchQuery: this.extractSearchQuery(message)
      }
    } catch (error: any) {
      console.error('AI Chat API error:', error)
      
      // Fallback to local search if backend is unavailable
      try {
        const searchResults = await this.intelligentFileSearch(message)
        return {
          message: this.generateResponseMessage(message, searchResults),
          suggestions: searchResults,
          searchQuery: this.extractSearchQuery(message)
        }
      } catch (fallbackError: any) {
        throw new Error(`AI Chat error: ${error.message}`)
      }
    }
  }

  /**
   * Call the backend AI chat API
   */
  private async callBackendApi(message: string): Promise<AiChatResponse> {
    const token = localStorage.getItem('token')
    if (!token) {
      throw new Error('Authentication token not found')
    }

    const requestBody: AiChatRequest = {
      message,
      conversationId: this.conversationId || undefined
    }

    const response = await fetch(`${this.baseUrl}/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(requestBody)
    })

    if (!response.ok) {
      if (response.status === 401) {
        throw new Error('Authentication failed. Please log in again.')
      }
      throw new Error(`Backend API error: ${response.status} ${response.statusText}`)
    }

    const data: AiChatResponse = await response.json()
    
    // Store conversation ID for future requests
    if (data.conversationId) {
      this.conversationId = data.conversationId
    }
    
    return data
  }

  /**
   * Perform intelligent file search based on user query
   */
  private async intelligentFileSearch(query: string): Promise<FileSuggestion[]> {
    try {
      // Extract keywords and intent from the query
      const { keywords, filters } = this.parseQuery(query)
      
      // Search for files using the extracted information
      const searchParams = {
        filename: keywords.join(' '),
        departmentId: filters.departmentId,
        projectId: filters.projectId,
        fileTypeId: filters.fileTypeId,
        page: 0,
        size: 10
      }

      const response = await fileApi.getSharedFiles(searchParams)
      
      if (response.data && response.data.content) {
        const files = response.data.content
        
        // Score and rank files based on relevance
        return this.scoreFileRelevance(files, query, keywords)
      }

      return []
    } catch (error) {
      console.error('Error in intelligent file search:', error)
      return []
    }
  }

  /**
   * Parse user query to extract keywords, intent, and filters
   */
  private parseQuery(query: string): {
    keywords: string[]
    intent: string
    filters: {
      departmentId?: number
      projectId?: number
      fileTypeId?: number
      dateRange?: { start: Date, end: Date }
    }
  } {
    const lowerQuery = query.toLowerCase()
    
    // Extract keywords (remove common words)
    const commonWords = ['find', 'search', 'show', 'get', 'me', 'the', 'a', 'an', 'and', 'or', 'but', 'for', 'from', 'to', 'with', 'by']
    const keywords = lowerQuery
      .split(/\s+/)
      .filter(word => word.length > 2 && !commonWords.includes(word))
      .slice(0, 5) // Limit to 5 keywords

    // Determine intent
    let intent = 'search'
    if (lowerQuery.includes('latest') || lowerQuery.includes('recent') || lowerQuery.includes('new')) {
      intent = 'recent'
    } else if (lowerQuery.includes('popular') || lowerQuery.includes('downloaded')) {
      intent = 'popular'
    } else if (lowerQuery.includes('my') || lowerQuery.includes('mine')) {
      intent = 'personal'
    }

    // Extract filters (this could be enhanced with NLP)
    const filters: any = {}
    
    // File type detection
    const fileTypeWords = {
      'pdf': ['pdf', 'document'],
      'image': ['image', 'picture', 'photo', 'img'],
      'video': ['video', 'movie', 'clip'],
      'audio': ['audio', 'sound', 'music'],
      'spreadsheet': ['excel', 'spreadsheet', 'csv'],
      'presentation': ['powerpoint', 'presentation', 'slides']
    }

    for (const [_type, words] of Object.entries(fileTypeWords)) {
      if (words.some(word => lowerQuery.includes(word))) {
        // You might want to map these to actual fileTypeId from your backend
        break
      }
    }

    return { keywords, intent, filters }
  }

  /**
   * Score files based on relevance to the query
   */
  private scoreFileRelevance(files: FileResponse[], _query: string, keywords: string[]): FileSuggestion[] {
    
    return files.map(file => {
      let score = 0
      let reasons: string[] = []

      // Score based on filename match
      const filename = (file.title || file.originalFilename || '').toLowerCase()
      keywords.forEach(keyword => {
        if (filename.includes(keyword)) {
          score += 3
          reasons.push(`filename contains "${keyword}"`)
        }
      })

      // Score based on description match
      if (file.description) {
        const description = file.description.toLowerCase()
        keywords.forEach(keyword => {
          if (description.includes(keyword)) {
            score += 2
            reasons.push(`description mentions "${keyword}"`)
          }
        })
      }

      // Score based on tags match
      if (file.tags && file.tags.length > 0) {
        const fileTags = file.tags.join(' ').toLowerCase()
        keywords.forEach(keyword => {
          if (fileTags.includes(keyword)) {
            score += 2
            reasons.push(`tagged with "${keyword}"`)
          }
        })
      }

      // Boost score for recent files
      if (file.uploadedAt) {
        const uploadDate = new Date(file.uploadedAt)
        const daysSinceUpload = (Date.now() - uploadDate.getTime()) / (1000 * 60 * 60 * 24)
        if (daysSinceUpload < 7) {
          score += 1
          reasons.push('recently uploaded')
        }
      }

      // Boost popular files (high download count)
      if (file.downloadCount && file.downloadCount > 10) {
        score += 1
        reasons.push('frequently downloaded')
      }

      return {
        file,
        relevanceScore: score,
        reason: reasons.length > 0 ? reasons.join(', ') : 'general match'
      }
    })
    .filter(suggestion => suggestion.relevanceScore > 0)
    .sort((a, b) => b.relevanceScore - a.relevanceScore)
    .slice(0, 6) // Return top 6 results
  }

  /**
   * Generate a conversational response message
   */
  private generateResponseMessage(query: string, suggestions: FileSuggestion[]): string {
    const lowerQuery = query.toLowerCase()
    
    if (suggestions.length === 0) {
      return `I couldn't find any files matching "${query}". Try using different keywords or check if the files are shared with you.`
    }

    let greeting = ''
    if (lowerQuery.includes('help') || lowerQuery.includes('find')) {
      greeting = "I'd be happy to help you find files! "
    } else if (lowerQuery.includes('show') || lowerQuery.includes('search')) {
      greeting = "Here's what I found: "
    } else {
      greeting = "Based on your request, "
    }

    const resultCount = suggestions.length
    let resultText = ''
    
    if (resultCount === 1) {
      resultText = `I found 1 file that matches your search.`
    } else {
      resultText = `I found ${resultCount} files that match your search.`
    }

    const topFile = suggestions[0]
    const highlight = ` The most relevant one is "${topFile.file.title || topFile.file.originalFilename}" because it ${topFile.reason}.`

    return greeting + resultText + highlight
  }

  /**
   * Extract search query from conversational text
   */
  private extractSearchQuery(message: string): string {
    // Remove conversational words and extract the core search terms
    const conversationalPhrases = [
      'can you help me find',
      'i need to find',
      'show me',
      'search for',
      'find me',
      'looking for',
      'where is',
      'do you have'
    ]

    let cleanQuery = message.toLowerCase()
    
    conversationalPhrases.forEach(phrase => {
      cleanQuery = cleanQuery.replace(phrase, '').trim()
    })

    return cleanQuery || message
  }

  /**
   * Get suggested follow-up questions based on current context
   */
  getFollowUpSuggestions(_lastQuery: string, results: FileSuggestion[]): string[] {
    const suggestions = []

    if (results.length > 0) {
      suggestions.push("Show me more recent files")
      suggestions.push("Find files from the same department")
      suggestions.push("What are the most downloaded files?")
    } else {
      suggestions.push("Show me all recent files")
      suggestions.push("What files are available in my department?")
      suggestions.push("Find the most popular files")
    }

    return suggestions.slice(0, 3)
  }

  /**
   * Format file information for chat display
   */
  formatFileInfo(file: FileResponse): string {
    const parts = []
    
    if (file.title) parts.push(`Title: ${file.title}`)
    if (file.description) parts.push(`Description: ${file.description}`)
    if (file.uploaderName) parts.push(`Uploaded by: ${file.uploaderName}`)
    if (file.uploadedAt) {
      const date = new Date(file.uploadedAt).toLocaleDateString()
      parts.push(`Date: ${date}`)
    }

    return parts.join('\n')
  }
}

export default new AiChatService()
