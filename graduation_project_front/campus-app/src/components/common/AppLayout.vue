<template>
  <div class="app-layout" :class="{ 'has-tabbar': showBottomNav }">
    <!-- 顶部导航 -->
    <header v-if="showTopNav" class="layout-header">
      <TopNavBar />
    </header>

    <!-- 主内容区域 -->
    <main class="layout-main">
      <slot />
    </main>

    <!-- 底部标签栏 -->
    <footer v-if="showBottomNav" class="layout-footer">
      <BottomTabBar />
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopNavBar from './TopNavBar.vue'
import BottomTabBar from './BottomTabBar.vue'

const route = useRoute()

// 不显示顶部导航的页面
const hideTopNavPages = ['/login', '/register']

// 显示底部导航的页面
const bottomNavPages = ['/', '/home', '/category', '/chat', '/profile']

const showTopNav = computed(() => {
  return !hideTopNavPages.includes(route.path)
})

const showBottomNav = computed(() => {
  return bottomNavPages.includes(route.path)
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.layout-header {
  position: sticky;
  top: 0;
  z-index: 100;
  flex-shrink: 0;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.app-layout.has-tabbar .layout-main {
  padding-bottom: calc(60px + env(safe-area-inset-bottom, 0px));
}

.layout-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
</style>
