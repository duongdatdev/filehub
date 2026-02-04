<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Chat Toggle Button -->
    <button
      v-if="!isOpen"
      @click="toggleChat"
      class="bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full w-16 h-16 flex items-center justify-center shadow-lg hover:shadow-xl transform hover:scale-105 transition-all duration-300 hover:from-blue-600 hover:to-purple-700"
      :class="{ 'animate-pulse': hasUnreadMessages }"
      title="Chat with AI to find files"
    >
      <svg v-if="!isLoading" class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
              d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
      </svg>
      <div v-else class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
    </button>

    <!-- Chat Window -->
    <div
      v-if="isOpen"
      class="bg-white rounded-2xl shadow-2xl border border-gray-200 w-96 h-[500px] flex flex-col overflow-hidden transform transition-all duration-300 ease-out"
      :class="isOpen ? 'scale-100 opacity-100' : 'scale-95 opacity-0'"
    >
      <!-- Header -->
      <div class="bg-gradient-to-r from-blue-500 to-purple-600 px-6 py-4 flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
            </svg>
          </div>
          <div>
            <h3 class="text-white font-semibold text-sm">AI File Assistant</h3>
            <p class="text-white/80 text-xs">Find files with AI help</p>
          </div>
        </div>
        <button
          @click="toggleChat"
          class="text-white/80 hover:text-white transition-colors duration-200"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
      </div>

      <!-- Chat Messages -->
      <div ref="messagesContainer" class="flex-1 p-4 space-y-3 overflow-y-auto bg-gray-50">
        <!-- Welcome Message -->
        <div v-if="messages.length === 0" class="text-center py-8">
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                    d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
            </svg>
          </div>
          <h4 class="text-gray-800 font-medium mb-1">Welcome to AI File Search!</h4>
          <p class="text-gray-600 text-sm mb-4">Ask me to help you find files. Try phrases like:</p>
          <div class="space-y-2">
            <button
              v-for="suggestion in welcomeSuggestions"
              :key="suggestion"
              @click="sendMessage(suggestion)"
              class="block w-full text-left px-3 py-2 bg-white rounded-lg text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-700 transition-colors duration-200 border border-gray-200"
            >
              "{{ suggestion }}"
            </button>
          </div>
        </div>

        <!-- Chat Messages -->
        <div
          v-for="message in messages"
          :key="message.id"
          class="flex"
          :class="message.type === 'user' ? 'justify-end' : 'justify-start'"
        >
          <div
            class="max-w-[80%] rounded-lg px-3 py-2 text-sm"
            :class="[
              message.type === 'user'
                ? 'bg-blue-500 text-white rounded-br-sm'
                : 'bg-white text-gray-800 rounded-bl-sm border border-gray-200'
            ]"
          >
            <p class="whitespace-pre-wrap">{{ message.content }}</p>
            
            <!-- File Suggestions -->
            <div v-if="message.suggestions && message.suggestions.length > 0" class="mt-3 space-y-2">
              <div
                v-for="suggestion in message.suggestions"
                :key="suggestion.file.id"
                class="bg-gray-50 rounded-lg p-3 border border-gray-200 hover:border-blue-300 transition-colors duration-200 cursor-pointer"
                @click="openFile(suggestion.file)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1 min-w-0">
                    <h5 class="font-medium text-gray-900 text-xs truncate">
                      {{ suggestion.file.title || suggestion.file.originalFilename }}
                    </h5>
                    <p class="text-gray-600 text-xs mt-1" v-if="suggestion.file.description">
                      {{ suggestion.file.description.slice(0, 60) }}{{ suggestion.file.description.length > 60 ? '...' : '' }}
                    </p>
                    <div class="flex items-center justify-between mt-2">
                      <span class="text-blue-600 text-xs font-medium">
                        {{ Math.round(suggestion.relevanceScore * 20) }}% match
                      </span>
                      <span class="text-gray-500 text-xs">
                        {{ formatFileSize(suggestion.file.fileSize) }}
                      </span>
                    </div>
                  </div>
                  <div class="ml-2 flex-shrink-0">
                    <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                    </svg>
                  </div>
                </div>
                <p class="text-gray-500 text-xs mt-1">{{ suggestion.reason }}</p>
              </div>
            </div>

            <!-- Timestamp -->
            <p class="text-xs mt-2 opacity-70">
              {{ formatTime(message.timestamp) }}
            </p>
          </div>
        </div>

        <!-- Thinking Indicator -->
        <div v-if="isThinking" class="flex justify-start">
          <div class="bg-white text-gray-800 rounded-lg rounded-bl-sm px-3 py-2 border border-gray-200">
            <div class="flex items-center space-x-1">
              <div class="flex space-x-1">
                <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                <div class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
              </div>
              <span class="text-xs text-gray-600 ml-2">AI is thinking...</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Follow-up Suggestions -->
      <div v-if="followUpSuggestions.length > 0" class="px-4 py-2 bg-gray-50 border-t border-gray-200">
        <p class="text-xs text-gray-600 mb-2">Try asking:</p>
        <div class="flex flex-wrap gap-1">
          <button
            v-for="suggestion in followUpSuggestions"
            :key="suggestion"
            @click="sendMessage(suggestion)"
            class="text-xs bg-white border border-gray-300 rounded-full px-2 py-1 text-gray-700 hover:bg-blue-50 hover:border-blue-300 hover:text-blue-700 transition-colors duration-200"
          >
            {{ suggestion }}
          </button>
        </div>
      </div>

      <!-- Input Area -->
      <div class="p-4 border-t border-gray-200 bg-white">
        <div class="flex items-end space-x-2">
          <div class="flex-1">
            <textarea
              ref="messageInput"
              v-model="currentMessage"
              @keydown.enter.prevent="handleEnter"
              @input="adjustTextareaHeight"
              placeholder="Ask me to find files... (e.g., 'Find recent PDFs')"
              class="w-full resize-none rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors duration-200"
              rows="1"
              :disabled="isLoading"
            ></textarea>
          </div>
          <button
            @click="sendCurrentMessage"
            :disabled="!currentMessage.trim() || isLoading"
            class="bg-blue-500 text-white rounded-lg px-4 py-2 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 flex-shrink-0"
          >
            <svg v-if="!isLoading" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
            <div v-else class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import aiChatService, { type ChatMessage, type FileSuggestion } from '@/services/aiChatApi'

// Reactive state
const isOpen = ref(false)
const isLoading = ref(false)
const isThinking = ref(false)
const hasUnreadMessages = ref(false)
const currentMessage = ref('')
const messages = ref<ChatMessage[]>([])
const followUpSuggestions = ref<string[]>([])

// Template refs
const messagesContainer = ref<HTMLElement>()
const messageInput = ref<HTMLTextAreaElement>()

// Welcome suggestions
const welcomeSuggestions = [
  'Show me recent PDF files',
  'Find files uploaded this week',
  'What files are available from my department?',
  'Show me the most downloaded files'
]

// Methods
const toggleChat = async () => {
  isOpen.value = !isOpen.value
  
  if (isOpen.value) {
    hasUnreadMessages.value = false
    await nextTick()
    if (messageInput.value) {
      messageInput.value.focus()
    }
  }
}

const sendMessage = async (text: string) => {
  if (!text.trim()) return

  currentMessage.value = text
  await sendCurrentMessage()
}

const sendCurrentMessage = async () => {
  const text = currentMessage.value.trim()
  if (!text || isLoading.value) return

  // Add user message
  const userMessage: ChatMessage = {
    id: Date.now().toString(),
    type: 'user',
    content: text,
    timestamp: new Date()
  }
  messages.value.push(userMessage)
  currentMessage.value = ''

  // Show thinking indicator
  isThinking.value = true
  isLoading.value = true

  try {
    // Scroll to bottom
    await scrollToBottom()

    // Get AI response
    const response = await aiChatService.chatWithAI(text, messages.value)

    // Create assistant message
    const assistantMessage: ChatMessage = {
      id: (Date.now() + 1).toString(),
      type: 'assistant',
      content: response.message,
      timestamp: new Date(),
      suggestions: response.suggestions || []
    }

    messages.value.push(assistantMessage)

    // Update follow-up suggestions
    followUpSuggestions.value = aiChatService.getFollowUpSuggestions(text, response.suggestions || [])

    // Scroll to bottom
    await scrollToBottom()

  } catch (error: any) {
    console.error('Chat error:', error)
    
    const errorMessage: ChatMessage = {
      id: (Date.now() + 2).toString(),
      type: 'assistant',
      content: 'Sorry, I encountered an error while searching for files. Please try again.',
      timestamp: new Date()
    }
    messages.value.push(errorMessage)
  } finally {
    isThinking.value = false
    isLoading.value = false
    
    // Reset textarea height
    if (messageInput.value) {
      messageInput.value.style.height = 'auto'
    }
  }
}

const handleEnter = (event: KeyboardEvent) => {
  if (event.shiftKey) {
    // Allow new line with Shift+Enter
    return
  } else {
    // Send message with Enter
    sendCurrentMessage()
  }
}

const adjustTextareaHeight = () => {
  if (messageInput.value) {
    messageInput.value.style.height = 'auto'
    messageInput.value.style.height = Math.min(messageInput.value.scrollHeight, 100) + 'px'
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const openFile = (file: any) => {
  // Emit event or call method to view/download file
  const url = `/api/files/${file.id}/preview`
  window.open(url, '_blank')
}

const formatTime = (date: Date) => {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

// Watch for new messages to handle auto-scroll
watch(
  () => messages.value.length,
  () => {
    scrollToBottom()
  }
)
</script>

<style scoped>
/* Custom scrollbar */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Smooth animations */
.transform {
  transition: transform 0.2s ease-in-out;
}

/* Textarea auto-resize */
textarea {
  min-height: 38px;
  max-height: 100px;
  line-height: 1.4;
}
</style>
