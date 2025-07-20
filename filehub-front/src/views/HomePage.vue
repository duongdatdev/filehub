<template>
  <div>
    <NavBar />
    <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <div class="container mx-auto px-4 py-8">
        <div class="text-center mb-8">
          <h1 class="text-4xl font-bold text-gray-800 mb-4">Welcome to FileHub</h1>
          <p class="text-xl text-gray-600">Your Vue 3 Frontend Application</p>
        </div>
        
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
  </div>
</template>

<script setup lang="ts">
import { useCounterStore } from '@/stores/counter'
import NavBar from '@/components/NavBar.vue'

const counter = useCounterStore()
</script>
