<template>
  <div class="app-layout">
    <TopNavBar v-if="showTopNav" />
    <main class="main-content">
      <slot />
    </main>
    <BottomTabBar v-if="showBottomNav" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopNavBar from './TopNavBar.vue'
import BottomTabBar from './BottomTabBar.vue'

const route = useRoute()

const showTopNav = computed(() => {
  return !['/login', '/register'].includes(route.path)
})

const showBottomNav = computed(() => {
  const bottomNavPages = ['/', '/home', '/category', '/chat', '/profile']
  return bottomNavPages.includes(route.path)
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: calc(var(--tabbar-height, 56px) + env(safe-area-inset-bottom));
}
</style>
