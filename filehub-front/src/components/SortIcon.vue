<template>
  <span class="ml-2 flex-none rounded text-gray-400 group-hover:visible group-focus:visible">
    <svg
      v-if="direction === 'asc'"
      class="h-5 w-5 text-gray-900"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 20 20"
      fill="currentColor"
      aria-hidden="true"
    >
      <path
        fill-rule="evenodd"
        d="M10 3a1 1 0 01.707.293l3 3a1 1 0 01-1.414 1.414L11 6.414V16a1 1 0 11-2 0V6.414L7.707 7.707a1 1 0 01-1.414-1.414l3-3A1 1 0 0110 3z"
        clip-rule="evenodd"
      />
    </svg>
    <svg
      v-else-if="direction === 'desc'"
      class="h-5 w-5 text-gray-900"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 20 20"
      fill="currentColor"
      aria-hidden="true"
    >
      <path
        fill-rule="evenodd"
        d="M10 17a1 1 0 01-.707-.293l-3-3a1 1 0 011.414-1.414L9 13.586V4a1 1 0 112 0v9.586l1.293-1.293a1 1 0 011.414 1.414l-3 3A1 1 0 0110 17z"
        clip-rule="evenodd"
      />
    </svg>
    <svg
      v-else
      class="invisible h-5 w-5 group-hover:visible group-focus:visible"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 20 20"
      fill="currentColor"
      aria-hidden="true"
    >
      <path
        fill-rule="evenodd"
        d="M10 3a1 1 0 01.707.293l3 3a1 1 0 01-1.414 1.414L11 6.414V16a1 1 0 11-2 0V6.414L7.707 7.707a1 1 0 01-1.414-1.414l3-3A1 1 0 0110 3z"
        clip-rule="evenodd"
      />
    </svg>
  </span>
</template>

<script setup lang="ts">
interface Props {
  direction?: 'asc' | 'desc' | null
}

withDefaults(defineProps<Props>(), {
  direction: null
})
</script>