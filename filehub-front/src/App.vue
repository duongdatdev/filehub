<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- Navigation Bar -->
    <NavBar v-if="!isAuthPage" />
    
    <!-- Main Content -->
    <main :class="isAuthPage ? 'min-h-screen' : 'pt-16'">
      <router-view />
    </main>

    <!-- Footer -->
    <footer v-if="!isAuthPage" class="bg-white border-t border-gray-200 mt-auto">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 class="text-lg font-semibold text-gray-900 mb-4">FileHub</h3>
            <p class="text-gray-600">Your secure file management platform for all your storage needs.</p>
          </div>
          <div>
            <h4 class="text-md font-medium text-gray-900 mb-4">Quick Links</h4>
            <ul class="space-y-2">
              <li><router-link to="/" class="text-gray-600 hover:text-blue-600 transition">Home</router-link></li>
              <li><router-link to="/about" class="text-gray-600 hover:text-blue-600 transition">About</router-link></li>
            </ul>
          </div>
          <div>
            <h4 class="text-md font-medium text-gray-900 mb-4">Support</h4>
            <ul class="space-y-2">
              <li><a href="#" class="text-gray-600 hover:text-blue-600 transition">Help Center</a></li>
              <li><a href="#" class="text-gray-600 hover:text-blue-600 transition">Contact Us</a></li>
            </ul>
          </div>
        </div>
        <div class="border-t border-gray-200 mt-8 pt-6 text-center">
          <p class="text-gray-500 text-sm">&copy; 2024 FileHub. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const authStore = useAuthStore()

const isAuthPage = computed(() => {
  return route.path === '/login' || route.path === '/register'
})

onMounted(() => {
  authStore.initializeAuth()
})
</script>

<style>
/* Global styles */
#app {
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}
</style>
