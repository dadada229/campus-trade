<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <!-- 插图 -->
      <div class="illustration">
        <div class="number">4</div>
        <div class="circle">
          <el-icon :size="48"><Search /></el-icon>
        </div>
        <div class="number">4</div>
      </div>

      <!-- 文案 -->
      <h1 class="title">页面未找到</h1>
      <p class="description">抱歉，您访问的页面不存在或已被移除</p>

      <!-- 操作按钮 -->
      <div class="actions">
        <el-button type="primary" size="large" class="primary-btn" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          <span>返回首页</span>
        </el-button>
        <el-button size="large" class="secondary-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回上页</span>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Search, HomeFilled, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, var(--color-bg-secondary) 0%, var(--color-bg) 100%);
  padding: var(--spacing-xl);
}

.not-found-container {
  text-align: center;
  max-width: 400px;
}

.illustration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xxl);
}

.number {
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.circle {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-secondary-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-md) 0;
}

.description {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-xxl) 0;
  line-height: 1.6;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.primary-btn {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.secondary-btn {
  width: 100%;
  height: 48px;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 500;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
}

.secondary-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}
</style>
