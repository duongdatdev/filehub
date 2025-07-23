<template>
  <IconComponent v-bind="$attrs" :class="iconClass">
    <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
  </IconComponent>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import IconComponent from './IconComponent.vue'

interface Props {
  class?: string
}

const props = defineProps<Props>()

const iconClass = computed(() => props.class || 'w-5 h-5')
</script>
