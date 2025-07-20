<template>
  <nav class="bg-white shadow-lg">
    <div class="max-w-7xl mx-auto px-4">
      <div class="flex justify-between h-16">
        <div class="flex items-center">
          <router-link 
            to="/" 
            class="flex-shrink-0 flex items-center"
          >
            <h1 class="text-xl font-bold text-gray-800">FileHub</h1>
          </router-link>
        </div>
        
        <div class="flex items-center space-x-4">
          <router-link 
            to="/" 
            class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition"
            :class="{ 'text-blue-600 bg-blue-50': $route.path === '/' }"
          >
            Home
          </router-link>
          <router-link 
            to="/about" 
            class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition"
            :class="{ 'text-blue-600 bg-blue-50': $route.path === '/about' }"
          >
            About
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const $route = useRoute()
</script>
