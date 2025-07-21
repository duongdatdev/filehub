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
          <!-- Navigation links (only show if authenticated) -->
          <template v-if="authStore.isAuthenticated">
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

            <!-- User dropdown -->
            <div class="relative">
              <button
                @click="showUserMenu = !showUserMenu"
                class="flex items-center space-x-2 text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition"
              >
                <div class="w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center">
                  {{ userInitials }}
                </div>
                <span>{{ authStore.user?.fullName }}</span>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                </svg>
              </button>

              <!-- Dropdown menu -->
              <div
                v-if="showUserMenu"
                v-click-outside="closeUserMenu"
                class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10"
              >
                <div class="px-4 py-2 text-sm text-gray-700 border-b">
                  <div class="font-medium">{{ authStore.user?.fullName }}</div>
                  <div class="text-gray-500">{{ authStore.user?.email }}</div>
                </div>
                <button
                  @click="handleLogout"
                  class="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                >
                  Sign out
                </button>
              </div>
            </div>
          </template>

          <!-- Login/Register links (only show if not authenticated) -->
          <template v-else>
            <router-link 
              to="/login" 
              class="text-gray-700 hover:text-blue-600 px-3 py-2 rounded-md text-sm font-medium transition"
            >
              Sign In
            </router-link>
            <router-link 
              to="/register" 
              class="bg-blue-600 text-white hover:bg-blue-700 px-4 py-2 rounded-md text-sm font-medium transition"
            >
              Sign Up
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const $route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const showUserMenu = ref(false)

const userInitials = computed(() => {
  if (!authStore.user?.fullName) return 'U'
  return authStore.user.fullName
    .split(' ')
    .map(name => name[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
})

const handleLogout = async () => {
  await authStore.logout()
  showUserMenu.value = false
  router.push('/login')
}

const closeUserMenu = () => {
  showUserMenu.value = false
}

// Click outside directive (you can implement this as a custom directive)
const vClickOutside = {
  mounted(el: any, binding: any) {
    el.clickOutsideEvent = (event: Event) => {
      if (!(el == event.target || el.contains(event.target))) {
        binding.value()
      }
    }
    document.addEventListener("click", el.clickOutsideEvent)
  },
  unmounted(el: any) {
    document.removeEventListener("click", el.clickOutsideEvent)
  }
}
</script>
