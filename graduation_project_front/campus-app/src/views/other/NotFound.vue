<template>
  <AppLayout>
    <div class="not-found-page">
      <div class="not-found-container">
        <div class="not-found-code">404</div>
        <div class="not-found-text">抱歉，您访问的页面不存在</div>
        <div class="not-found-desc">可能是链接错误或页面已被移除</div>
        <div class="not-found-actions">
          <el-button type="primary" size="large" @click="goHome">
            返回首页
          </el-button>
          <el-button size="large" @click="goBack">
            返回上一页
          </el-button>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AppLayout from '@/components/common/AppLayout.vue'

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
  background: linear-gradient(135deg, var(--color-bg) 0%, var(--color-card) 100%);
}

.not-found-container {
  text-align: center;
  padding: var(--spacing-xxl);
}

.not-found-code {
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  color: var(--color-primary);
  margin-bottom: var(--spacing-lg);
  text-shadow: 4px 4px 0 rgba(255, 107, 61, 0.2);
}

.not-found-text {
  font-size: var(--font-size-h2);
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-sm);
}

.not-found-desc {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xxl);
}

.not-found-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
}
</style>
