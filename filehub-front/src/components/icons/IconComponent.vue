<template>
  <svg 
    :class="iconClasses"
    :width="size"
    :height="size"
    :fill="fill"
    :stroke="stroke"
    :viewBox="viewBox"
    :stroke-width="strokeWidth"
    stroke-linecap="round"
    stroke-linejoin="round"
  >
    <slot />
  </svg>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  name?: string
  size?: string | number
  fill?: string
  stroke?: string
  strokeWidth?: string | number
  viewBox?: string
  class?: string
}

const props = withDefaults(defineProps<Props>(), {
  size: 24,
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 2,
  viewBox: '0 0 24 24',
  class: ''
})

const iconClasses = computed(() => {
  return `inline-block ${props.class}`
})
</script>

<style scoped>
svg {
  vertical-align: middle;
}
</style>
