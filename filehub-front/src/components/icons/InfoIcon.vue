<template>
  <IconComponent v-bind="$attrs" :class="iconClass">
    <path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
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
