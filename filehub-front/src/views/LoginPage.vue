<template>
  <!-- Background with animated gradient -->
  <div class="min-h-screen relative overflow-hidden bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900">
    <!-- Animated background elements -->
    <div class="absolute inset-0">
      <div class="absolute top-1/4 left-1/4 w-96 h-96 bg-blue-500/20 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
      <div class="absolute top-1/3 right-1/4 w-96 h-96 bg-purple-500/20 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000"></div>
      <div class="absolute bottom-1/4 left-1/3 w-96 h-96 bg-pink-500/20 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-4000"></div>
    </div>
    
    <!-- Main content -->
    <div class="relative flex items-center justify-center min-h-screen py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 animate-fade-in-up">
        <!-- Header with enhanced design -->
        <div class="text-center animate-slide-in-right">
          <!-- Logo with enhanced styling -->
          <div class="relative inline-flex items-center justify-center w-20 h-20 mb-8 mx-auto group animate-float">
            <div class="absolute inset-0 bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 rounded-2xl blur-sm group-hover:blur-md transition-all duration-300"></div>
            <div class="relative flex items-center justify-center w-full h-full bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 rounded-2xl shadow-xl transform group-hover:scale-105 transition-all duration-300">
              <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
          </div>
          
          <!-- Title with enhanced typography -->
          <h1 class="text-4xl md:text-5xl font-black text-white mb-3 tracking-tight">
            <span class="bg-gradient-to-r from-white via-blue-100 to-purple-100 bg-clip-text text-transparent">
              Welcome back
            </span>
          </h1>
          <p class="text-lg md:text-xl text-gray-300 mb-4 font-medium">
            Sign in to your FileHub account
          </p>
          <p class="text-sm text-gray-400">
            Don't have an account?
            <router-link to="/register" class="font-semibold text-blue-400 hover:text-blue-300 transition-colors duration-200 hover:underline ml-1">
              Create one here
            </router-link>
          </p>
        </div>
        
        <!-- Login Form with Glass Effect -->
        <div class="relative group">
          <!-- Glass morphism background with enhanced effects -->
          <div class="absolute inset-0 bg-white/10 backdrop-blur-xl rounded-3xl border border-white/20 shadow-2xl group-hover:shadow-3xl transition-all duration-500"></div>
          <div class="absolute inset-0 bg-gradient-to-r from-blue-500/5 via-purple-500/5 to-pink-500/5 rounded-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
          
          <div class="relative p-8 space-y-6">
            <form class="space-y-6" @submit.prevent="handleLogin">
              <!-- Error message with enhanced styling -->
              <div v-if="authStore.error" class="bg-red-500/10 backdrop-blur-sm border border-red-500/20 text-red-300 px-4 py-3 rounded-xl flex items-center space-x-3 animate-shake">
                <div class="flex-shrink-0">
                  <svg class="w-5 h-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <span class="font-medium">{{ authStore.error }}</span>
              </div>

              <div class="space-y-5">
                <!-- Username Field -->
                <div class="group">
                  <label for="username" class="block text-sm font-semibold text-gray-200 mb-2 group-focus-within:text-blue-300 transition-colors">
                    Username
                  </label>
                  <div class="relative">
                    <input
                      id="username"
                      v-model="form.username"
                      name="username"
                      type="text"
                      required
                      class="w-full px-4 py-3 pl-12 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500/50 transition-all duration-300 hover:bg-white/15"
                      placeholder="Enter your username"
                    />
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <svg class="w-5 h-5 text-gray-400 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </div>
                  </div>
                </div>
                
                <!-- Password Field -->
                <div class="group">
                  <label for="password" class="block text-sm font-semibold text-gray-200 mb-2 group-focus-within:text-blue-300 transition-colors">
                    Password
                  </label>
                  <div class="relative">
                    <input
                      id="password"
                      v-model="form.password"
                      name="password"
                      :type="showPassword ? 'text' : 'password'"
                      required
                      class="w-full px-4 py-3 pl-12 pr-12 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500/50 transition-all duration-300 hover:bg-white/15"
                      placeholder="Enter your password"
                    />
                    <div class="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                      <svg class="w-5 h-5 text-gray-400 group-focus-within:text-blue-400 transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2a6 6 0 01-6-6c0-3.313 2.687-6 6-6s6 2.687 6 6a6 6 0 01-6 6z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 19H6.5A2.5 2.5 0 014 16.5v-9A2.5 2.5 0 016.5 5h10A2.5 2.5 0 0119 7.5v9a2.5 2.5 0 01-2.5 2.5H16" />
                      </svg>
                    </div>
                    <button
                      type="button"
                      @click="showPassword = !showPassword"
                      class="absolute inset-y-0 right-0 pr-4 flex items-center hover:bg-white/10 rounded-r-xl transition-colors"
                    >
                      <svg v-if="showPassword" class="w-5 h-5 text-gray-400 hover:text-white transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                      </svg>
                      <svg v-else class="w-5 h-5 text-gray-400 hover:text-white transition-colors" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>

              <!-- Remember me and Forgot password -->
              <div class="flex items-center justify-between">
                <label class="flex items-center group cursor-pointer">
                  <input 
                    v-model="form.rememberMe" 
                    type="checkbox" 
                    class="w-4 h-4 text-blue-500 bg-white/10 border-white/20 rounded focus:ring-blue-500/50 focus:ring-2 transition-all"
                  />
                  <span class="ml-3 text-sm text-gray-300 group-hover:text-white transition-colors">Remember me</span>
                </label>
                <a href="#" class="text-sm text-blue-400 hover:text-blue-300 transition-colors duration-200 hover:underline font-medium">
                  Forgot password?
                </a>
              </div>

              <!-- Enhanced Sign In Button -->
              <button
                type="submit"
                :disabled="authStore.loading"
                class="group relative w-full flex justify-center py-4 px-4 border border-transparent text-base font-bold rounded-xl text-white bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 hover:from-blue-700 hover:via-purple-700 hover:to-pink-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-300 transform hover:scale-[1.02] hover:shadow-2xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none shadow-xl btn-gradient-hover overflow-hidden"
              >
                <!-- Loading/Success animation overlay -->
                <div v-if="authStore.loading" class="absolute inset-0 bg-gradient-to-r from-blue-600/80 via-purple-600/80 to-pink-600/80 flex items-center justify-center">
                  <svg class="h-6 w-6 text-white animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                </div>
                
                <span class="flex items-center">
                  <svg v-if="!authStore.loading" class="h-5 w-5 mr-3 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  {{ authStore.loading ? 'Signing In...' : 'Sign In' }}
                </span>
              </button>
            </form>

            <!-- Enhanced Social Login -->
            <div class="mt-8">
              <div class="relative">
                <div class="absolute inset-0 flex items-center">
                  <div class="w-full border-t border-white/20" />
                </div>
                <div class="relative flex justify-center text-sm">
                  <span class="px-4 bg-transparent text-gray-300 font-medium">Or continue with</span>
                </div>
              </div>

              <div class="mt-6 grid grid-cols-2 gap-4">
                <button
                  type="button"
                  class="group w-full flex justify-center items-center py-3 px-4 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl text-sm font-semibold text-gray-300 hover:text-white hover:bg-white/20 transition-all duration-300 transform hover:scale-105 hover:shadow-lg"
                >
                  <svg class="w-5 h-5 mr-2 group-hover:scale-110 transition-transform" viewBox="0 0 24 24">
                    <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                    <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                    <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                    <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                  </svg>
                  Google
                </button>

                <button
                  type="button"
                  class="group w-full flex justify-center items-center py-3 px-4 bg-white/10 backdrop-blur-sm border border-white/20 rounded-xl text-sm font-semibold text-gray-300 hover:text-white hover:bg-white/20 transition-all duration-300 transform hover:scale-105 hover:shadow-lg"
                >
                  <svg class="w-5 h-5 mr-2 group-hover:scale-110 transition-transform" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12.017 0C5.396 0 .029 5.367.029 11.987c0 5.079 3.158 9.417 7.618 11.174-.105-.949-.199-2.403.041-3.439.219-.937 1.219-5.068 1.219-5.068s-.31-.653-.31-1.613c0-1.511.875-2.637 1.963-2.637.925 0 1.372.694 1.372 1.526 0 .93-.593 2.321-.898 3.608-.255 1.077.538 1.955 1.595 1.955 1.915 0 3.386-2.021 3.386-4.936 0-2.584-1.843-4.389-4.476-4.389-3.064 0-4.871 2.284-4.871 4.648 0 .919.35 1.903.789 2.441.086.105.098.198.072.307-.079.331-.256 1.03-.291 1.175-.045.186-.147.226-.339.136-1.249-.581-2.03-2.407-2.03-3.874 0-3.154 2.292-6.052 6.608-6.052 3.469 0 6.165 2.473 6.165 5.776 0 3.447-2.173 6.22-5.19 6.22-1.013 0-1.966-.527-2.29-1.226l-.623 2.378c-.226.869-.835 1.958-1.244 2.621.937.29 1.931.446 2.962.446 6.624 0 11.99-5.367 11.99-11.987C24.007 5.367 18.641.001 12.017.001z"/>
                  </svg>
                  GitHub
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const showPassword = ref(false)

const form = ref({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  const success = await authStore.login({
    username: form.value.username,
    password: form.value.password
  })
  
  if (success) {
    router.push('/')
  }
}
</script>
