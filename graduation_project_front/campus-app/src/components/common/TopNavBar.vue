<template>
  <header class="top-nav-bar">
    <div class="nav-left" @click="goBack">
      <div v-if="showBack" class="back-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </div>
      <div v-else class="logo">
        <span class="logo-icon">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M5 13.18v4L12 21l7-3.82v-4L12 17l-7-3.82zM12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>
          </svg>
        </span>
        <span class="logo-text">校园好物</span>
      </div>
    </div>
    
    <div class="nav-title">
      <span v-if="showBack">{{ title }}</span>
    </div>
    
    <div class="nav-right">
      <div v-if="!showBack" class="action-icons">
        <div class="action-btn" @click="goSearch">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="M21 21l-4.35-4.35"/>
          </svg>
        </div>
        <div class="action-btn" @click="goChat">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
          </svg>
          <span v-if="unreadCount > 0" class="nav-badge"></span>
        </div>
      </div>
      <div v-else class="placeholder"></div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()

const title = computed(() => route.meta?.title || '')
const showBack = computed(() => !['/', '/home'].includes(route.path))
const unreadCount = computed(() => {
  return chatStore.conversations?.reduce((sum, c) => sum + (c.unreadCount || 0), 0) || 0
})

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
  height: var(--navbar-height);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  box-shadow: var(--shadow-xs);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  min-width: 80px;
}

.back-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: -8px;
  border-radius: var(--radius-circle);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
}

.back-btn:hover {
  background: var(--color-bg);
}

.back-btn:active {
  transform: scale(0.92);
}

.back-btn svg {
  width: 22px;
  height: 22px;
  color: var(--color-text-primary);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.logo-icon {
  width: 26px;
  height: 26px;
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 100%;
  height: 100%;
}

.logo-text {
  font-size: 18px;
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 var(--spacing-md);
}

.nav-right {
  display: flex;
  align-items: center;
  min-width: 80px;
  justify-content: flex-end;
}

.action-icons {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.action-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-circle);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
  position: relative;
}

.action-btn:hover {
  background: var(--color-bg);
}

.action-btn:active {
  transform: scale(0.92);
}

.action-btn svg {
  width: 22px;
  height: 22px;
  color: var(--color-text-primary);
}

.nav-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  width: 8px;
  height: 8px;
  background: var(--color-danger);
  border-radius: var(--radius-circle);
  border: 2px solid var(--color-card);
}

.placeholder {
  width: 36px;
}
</style>
