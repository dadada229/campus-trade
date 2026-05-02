<template>
  <footer class="bottom-tab-bar">
    <div class="tab-item" :class="{ active: currentPath === '/home' }" @click="goHome">
      <div class="tab-icon">
        <svg v-if="currentPath === '/home'" viewBox="0 0 24 24" fill="currentColor">
          <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
        </svg>
      </div>
      <span class="tab-text">首页</span>
    </div>

    <div class="tab-item" :class="{ active: currentPath === '/category' }" @click="goCategory">
      <div class="tab-icon">
        <svg v-if="currentPath === '/category'" viewBox="0 0 24 24" fill="currentColor">
          <path d="M4 8h4V4H4v4zm6 12h4v-4h-4v4zm-6 0h4v-4H4v4zm0-6h4v-4H4v4zm6 0h4v-4h-4v4zm6-10v4h4V4h-4zm-6 4h4V4h-4v4zm6 6h4v-4h-4v4zm0 6h4v-4h-4v4z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <rect x="3" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="3" y="14" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/>
        </svg>
      </div>
      <span class="tab-text">分类</span>
    </div>

    <div class="tab-item publish-item" @click="goPublish">
      <div class="publish-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
          <path d="M12 5v14M5 12h14"/>
        </svg>
      </div>
      <span class="tab-text publish-text">发布</span>
    </div>

    <div class="tab-item" :class="{ active: currentPath === '/chat' }" @click="goChat">
      <div class="tab-icon">
        <svg v-if="currentPath === '/chat'" viewBox="0 0 24 24" fill="currentColor">
          <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
        </svg>
      </div>
      <span class="tab-text">消息</span>
      <div v-if="unreadCount > 0" class="badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</div>
    </div>

    <div class="tab-item" :class="{ active: currentPath === '/profile' }" @click="goProfile">
      <div class="tab-icon">
        <svg v-if="currentPath === '/profile'" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
          <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
        </svg>
      </div>
      <span class="tab-text">我的</span>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const currentPath = computed(() => route.path)
const unreadCount = computed(() => {
  return chatStore.conversations?.reduce((sum, c) => sum + (c.unreadCount || 0), 0) || 0
})

const goHome = () => router.push('/home')
const goCategory = () => router.push('/category')
const goChat = () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
  } else {
    router.push('/chat')
  }
}
const goProfile = () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
  } else {
    router.push('/profile')
  }
}
const goPublish = () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
  } else {
    router.push('/publish')
  }
}
</script>

<style scoped>
.bottom-tab-bar {
  height: calc(var(--tabbar-height) + env(safe-area-inset-bottom));
  background-color: var(--color-card);
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: var(--shadow-top);
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.92);
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 8px;
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
  position: relative;
}

.tab-item:active {
  transform: scale(0.92);
}

.tab-icon {
  width: 24px;
  height: 24px;
  color: var(--color-text-tertiary);
  margin-bottom: 2px;
  transition: all var(--duration-fast) var(--ease-standard);
}

.tab-icon svg {
  width: 100%;
  height: 100%;
}

.tab-text {
  font-size: 11px;
  color: var(--color-text-tertiary);
  font-weight: var(--font-weight-medium);
  transition: all var(--duration-fast) var(--ease-standard);
}

.tab-item.active .tab-icon {
  color: var(--color-primary);
}

.tab-item.active .tab-text {
  color: var(--color-primary);
}

/* 发布按钮 */
.publish-item {
  flex: 0.8;
}

.publish-btn {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -12px;
  box-shadow: 0 6px 16px rgba(61, 154, 139, 0.35);
  transition: all var(--duration-fast) var(--ease-standard);
}

.publish-btn:active {
  transform: scale(0.95);
  box-shadow: 0 4px 12px rgba(61, 154, 139, 0.25);
}

.publish-btn svg {
  width: 24px;
  height: 24px;
  color: #fff;
}

.publish-text {
  margin-top: 4px;
  color: var(--color-primary);
}

/* 未读消息徽章 */
.badge {
  position: absolute;
  top: 2px;
  right: 50%;
  transform: translateX(16px);
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: var(--color-danger);
  color: #fff;
  font-size: 10px;
  font-weight: var(--font-weight-semibold);
  line-height: 16px;
  text-align: center;
}
</style>
