<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

onMounted(async () => {
  const userStore = useUserStore()
  await userStore.initializeUser()
})
</script>

<template>
  <router-view v-slot="{ Component, route }">
    <transition name="slide-fade" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease-out;
}

.fade-enter-from {
  opacity: 0;
}

.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease-out;
}

.slide-fade-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}
</style>

<style scoped>
</style>
