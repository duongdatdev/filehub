<template>
  <IconComponent v-bind="$attrs" :class="iconClass">
    <path d="M4 6h16M4 12h16M4 18h16" />
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
