<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-4">UI Components Test Page</h1>
        <p class="text-gray-600">Testing Tailwind CSS and custom components</p>
      </div>

      <!-- Button Tests -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Buttons</h2>
        <div class="space-x-4 mb-6">
          <button class="btn btn-primary">Primary Button</button>
          <button class="btn btn-secondary">Secondary Button</button>
          <button class="btn btn-danger">Danger Button</button>
          <button class="btn btn-ghost">Ghost Button</button>
        </div>
        
        <!-- Tailwind Buttons -->
        <div class="space-x-4">
          <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            Tailwind Primary
          </button>
          <button class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">
            Tailwind Secondary
          </button>
          <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
            Tailwind Danger
          </button>
        </div>
      </section>

      <!-- Icon Tests -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Icons</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow">
            <FolderIcon class="w-8 h-8 text-blue-600 mb-2" />
            <span class="text-sm text-gray-600">Folder Icon</span>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow">
            <InfoIcon class="w-8 h-8 text-green-600 mb-2" />
            <span class="text-sm text-gray-600">Info Icon</span>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow">
            <UserIcon class="w-8 h-8 text-purple-600 mb-2" />
            <span class="text-sm text-gray-600">User Icon</span>
          </div>
          
          <div class="flex flex-col items-center p-4 bg-white rounded-lg shadow">
            <MenuIcon class="w-8 h-8 text-gray-600 mb-2" />
            <span class="text-sm text-gray-600">Menu Icon</span>
          </div>
        </div>
      </section>

      <!-- Form Tests -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Forms</h2>
        <div class="bg-white rounded-lg shadow p-6 max-w-md">
          <div class="mb-4">
            <label class="form-label">Name</label>
            <input type="text" class="form-input" placeholder="Enter your name" />
          </div>
          
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">Email (Tailwind)</label>
            <input 
              type="email" 
              class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500" 
              placeholder="Enter your email" 
            />
          </div>
          
          <div class="mb-6">
            <label class="form-label">Message</label>
            <textarea class="form-input h-20" placeholder="Enter your message"></textarea>
          </div>
          
          <button class="btn btn-primary w-full">Submit Form</button>
        </div>
      </section>

      <!-- Card Tests -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Cards</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="card p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Custom Card</h3>
            <p class="text-gray-600 mb-4">This is a custom card using our CSS classes.</p>
            <button class="btn btn-primary">Action</button>
          </div>
          
          <div class="bg-white rounded-lg shadow-lg p-6">
            <h3 class="text-lg font-semibold text-gray-900 mb-2">Tailwind Card</h3>
            <p class="text-gray-600 mb-4">This is a card using pure Tailwind classes.</p>
            <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              Action
            </button>
          </div>
          
          <div class="bg-gradient-to-r from-purple-400 via-pink-500 to-red-500 rounded-lg shadow-lg p-6 text-white">
            <h3 class="text-lg font-semibold mb-2">Gradient Card</h3>
            <p class="mb-4">A beautiful gradient card using Tailwind.</p>
            <button class="bg-white text-gray-800 font-bold py-2 px-4 rounded hover:bg-gray-100">
              Action
            </button>
          </div>
        </div>
      </section>

      <!-- Loading and Animations -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Loading & Animations</h2>
        <div class="space-x-6 flex items-center">
          <div class="spinner"></div>
          <div class="animate-pulse bg-gray-300 h-4 w-32 rounded"></div>
          <div class="animate-bounce bg-blue-500 h-6 w-6 rounded-full"></div>
          <div class="animate-spin h-6 w-6 border-2 border-blue-500 border-t-transparent rounded-full"></div>
        </div>
      </section>

      <!-- Responsive Grid -->
      <section class="mb-12">
        <h2 class="text-2xl font-semibold text-gray-800 mb-6">Responsive Grid</h2>
        <div class="grid-responsive">
          <div class="bg-blue-100 p-4 rounded-lg text-center">
            <h4 class="font-semibold text-blue-800">Grid Item 1</h4>
            <p class="text-blue-600">Responsive grid using custom class</p>
          </div>
          <div class="bg-green-100 p-4 rounded-lg text-center">
            <h4 class="font-semibold text-green-800">Grid Item 2</h4>
            <p class="text-green-600">Adjusts based on screen size</p>
          </div>
          <div class="bg-purple-100 p-4 rounded-lg text-center">
            <h4 class="font-semibold text-purple-800">Grid Item 3</h4>
            <p class="text-purple-600">Mobile: 1 col, Tablet: 2 cols, Desktop: 3 cols</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import FolderIcon from '@/components/icons/FolderIcon.vue'
import InfoIcon from '@/components/icons/InfoIcon.vue'
import UserIcon from '@/components/icons/UserIcon.vue'
import MenuIcon from '@/components/icons/MenuIcon.vue'
</script>
