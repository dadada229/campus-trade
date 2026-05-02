<template>
  <footer class="bottom-tab-bar">
    <div class="tab-item" :class="{ active: currentPath === '/home' }" @click="goHome">
      <el-icon class="tab-icon"><HomeFilled /></el-icon>
      <span class="tab-text">首页</span>
    </div>
    <div class="tab-item" :class="{ active: currentPath === '/category' }" @click="goCategory">
      <el-icon class="tab-icon"><Menu /></el-icon>
      <span class="tab-text">分类</span>
    </div>
    <div class="tab-item publish-item" @click="goPublish">
      <div class="publish-btn">
        <el-icon class="publish-icon"><Plus /></el-icon>
      </div>
    </div>
    <div class="tab-item" :class="{ active: currentPath === '/chat' }" @click="goChat">
      <el-icon class="tab-icon"><ChatDotRound /></el-icon>
      <span class="tab-text">消息</span>
    </div>
    <div class="tab-item" :class="{ active: currentPath === '/profile' }" @click="goProfile">
      <el-icon class="tab-icon"><User /></el-icon>
      <span class="tab-text">我的</span>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, Menu, Plus, ChatDotRound, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentPath = computed(() => route.path)

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
  height: calc(var(--tabbar-height, 56px) + env(safe-area-inset-bottom));
  background-color: var(--color-card);
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-icon {
  font-size: 22px;
  color: var(--color-text-tertiary);
  margin-bottom: 2px;
}

.tab-text {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.tab-item.active .tab-icon,
.tab-item.active .tab-text {
  color: var(--color-primary);
}

.publish-item {
  position: relative;
  flex: 0.8;
}

.publish-btn {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -8px;
  box-shadow: 0 4px 12px rgba(255, 107, 61, 0.3);
}

.publish-icon {
  font-size: 26px;
  color: #fff;
}
</style>
