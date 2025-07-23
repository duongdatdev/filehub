<template>
  <IconComponent v-bind="$attrs" :class="iconClass">
    <path d="M6 18L18 6M6 6l12 12" />
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
