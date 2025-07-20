<template>
  <div>
    <NavBar />
    <div class="min-h-screen bg-gradient-to-br from-green-50 to-teal-100">
      <div class="container mx-auto px-4 py-8">
        <div class="text-center mb-8">
          <h1 class="text-4xl font-bold text-gray-800 mb-4">About FileHub</h1>
          <p class="text-xl text-gray-600">Built with Vue 3, TypeScript, and TailwindCSS</p>
        </div>
        
        <div class="max-w-2xl mx-auto bg-white rounded-lg shadow-lg p-8">
          <div class="grid md:grid-cols-2 gap-6">
            <div class="text-center p-4 border rounded-lg">
              <h3 class="text-lg font-semibold text-gray-800 mb-2">Vue 3</h3>
              <p class="text-gray-600">Progressive JavaScript Framework</p>
            </div>
            
            <div class="text-center p-4 border rounded-lg">
              <h3 class="text-lg font-semibold text-gray-800 mb-2">Pinia</h3>
              <p class="text-gray-600">State Management Library</p>
            </div>
            
            <div class="text-center p-4 border rounded-lg">
              <h3 class="text-lg font-semibold text-gray-800 mb-2">Vue Router</h3>
              <p class="text-gray-600">Official Router for Vue.js</p>
            </div>
            
            <div class="text-center p-4 border rounded-lg">
              <h3 class="text-lg font-semibold text-gray-800 mb-2">TailwindCSS</h3>
              <p class="text-gray-600">Utility-first CSS Framework</p>
            </div>
          </div>
          
          <div class="mt-8 p-4 bg-blue-50 rounded-lg">
            <h3 class="text-lg font-semibold text-gray-800 mb-2">API Integration</h3>
            <p class="text-gray-600 mb-4">Axios is configured for HTTP requests</p>
            <button 
              @click="fetchData"
              :disabled="loading"
              class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition disabled:opacity-50"
            >
              {{ loading ? 'Loading...' : 'Test API Call' }}
            </button>
            <div v-if="apiData" class="mt-4 p-3 bg-white rounded text-sm">
              <pre>{{ JSON.stringify(apiData, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import NavBar from '@/components/NavBar.vue'

const loading = ref(false)
const apiData = ref<any>(null)

async function fetchData() {
  try {
    loading.value = true
    // Example API call to JSONPlaceholder
    const response = await axios.get('https://jsonplaceholder.typicode.com/posts/1')
    apiData.value = response.data
  } catch (error) {
    console.error('API Error:', error)
    apiData.value = { error: 'Failed to fetch data' }
  } finally {
    loading.value = false
  }
}
</script>
