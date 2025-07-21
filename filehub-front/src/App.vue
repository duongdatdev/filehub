<template>
  <div id="app">
    <NavBar v-if="!isAuthPage" />
    <router-view />
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
/* Global styles can be added here */
</style>
