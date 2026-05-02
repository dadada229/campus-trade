<template>
  <header class="top-nav-bar">
    <div class="nav-left" @click="goBack">
      <el-icon v-if="showBack" class="back-icon"><ArrowLeft /></el-icon>
      <span v-else class="logo">校园二手</span>
    </div>
    <div class="nav-title">{{ title }}</div>
    <div class="nav-right">
      <el-icon class="action-icon" @click="goSearch"><Search /></el-icon>
      <el-icon class="action-icon" @click="goChat"><ChatDotRound /></el-icon>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Search, ChatDotRound } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const title = computed(() => route.meta?.title || '校园二手')
const showBack = computed(() => !['/', '/home'].includes(route.path))

const goBack = () => {
  if (showBack.value) {
    router.back()
  }
}

const goSearch = () => {
  router.push('/search')
}

const goChat = () => {
  router.push('/chat')
}
</script>

<style scoped>
.top-nav-bar {
  height: var(--navbar-height, 44px);
  background-color: var(--color-card);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-md);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  width: 60px;
  cursor: pointer;
}

.back-icon {
  font-size: 20px;
  color: var(--color-text-primary);
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  width: 60px;
  justify-content: flex-end;
}

.action-icon {
  font-size: 20px;
  color: var(--color-text-primary);
  cursor: pointer;
}
</style>
