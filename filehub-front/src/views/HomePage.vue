<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
    <div class="container mx-auto px-4 py-8">
      <div class="text-center mb-8">
        <h1 class="text-4xl font-bold text-gray-800 mb-4">
          Welcome to FileHub, {{ authStore.user?.fullName }}!
        </h1>
        <p class="text-xl text-gray-600">Your personal file management system</p>
      </div>
      
      <!-- User Info Card -->
      <div class="max-w-md mx-auto bg-white rounded-lg shadow-lg p-6 mb-8">
        <h2 class="text-2xl font-semibold text-gray-800 mb-4">Account Information</h2>
        <div class="space-y-3">
          <div class="flex justify-between">
            <span class="text-gray-600">Username:</span>
            <span class="font-medium">{{ authStore.user?.username }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">Email:</span>
            <span class="font-medium">{{ authStore.user?.email }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">Role:</span>
            <span class="px-2 py-1 bg-blue-100 text-blue-800 text-sm rounded-full">
              {{ authStore.user?.role }}
            </span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">Status:</span>
            <span class="px-2 py-1 bg-green-100 text-green-800 text-sm rounded-full">
              {{ authStore.user?.isActive ? 'Active' : 'Inactive' }}
            </span>
          </div>
        </div>
      </div>

      <!-- Counter Example (keeping for demo) -->
      <div class="max-w-md mx-auto bg-white rounded-lg shadow-lg p-6">
        <h2 class="text-2xl font-semibold text-gray-800 mb-4">Counter Example</h2>
        <div class="text-center">
          <p class="text-3xl font-bold text-blue-600 mb-4">{{ counter.count }}</p>
          <div class="space-x-4">
            <button 
              @click="counter.increment"
              class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
            >
              Increment
            </button>
            <button 
              @click="counter.decrement"
              class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition"
            >
              Decrement
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCounterStore } from '@/stores/counter'
import { useAuthStore } from '@/stores/auth'

const counter = useCounterStore()
const authStore = useAuthStore()
</script>
