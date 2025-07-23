<template>
  <IconComponent v-bind="$attrs" :class="iconClass">
    <path d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2H5a2 2 0 00-2-2z" />
    <path d="M8 5a2 2 0 012-2h4a2 2 0 012 2v1H8V5z" />
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
