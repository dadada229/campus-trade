<template>
  <AppLayout>
    <div class="profile-page">
      <div class="user-card">
        <img :src="getAvatarUrl(userInfo?.avatar)" class="avatar" />
        <div class="user-info">
          <div class="nickname">{{ userInfo?.nickname || '用户' }}</div>
          <div class="school">{{ userInfo?.school || '未设置学校' }}</div>
        </div>
        <el-button type="primary" size="small" link @click="goToEdit">编辑</el-button>
      </div>

      <div class="order-section">
        <div class="section-header">
          <span class="section-title">我的订单</span>
        </div>
        <div class="order-tabs">
          <div class="order-tab" @click="goToOrders(1)">
            <el-icon><Document /></el-icon>
            <span>待确认</span>
          </div>
          <div class="order-tab" @click="goToOrders(2)">
            <el-icon><Van /></el-icon>
            <span>交易中</span>
          </div>
          <div class="order-tab" @click="goToOrders(3)">
            <el-icon><CircleCheck /></el-icon>
            <span>已完成</span>
          </div>
          <div class="order-tab" @click="goToOrders(4)">
            <el-icon><Close /></el-icon>
            <span>已取消</span>
          </div>
        </div>
        <div class="order-entrance">
          <div class="order-entrance-item" @click="goToOrders(undefined, 'buyer')">
            <span class="entrance-label">我买到的</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
          <div class="order-entrance-item" @click="goToOrders(undefined, 'seller')">
            <span class="entrance-label">我卖出的</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item" @click="$router.push('/my-products')">
          <el-icon><Goods /></el-icon>
          <span>我的发布</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/favorites')">
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/address')">
          <el-icon><Location /></el-icon>
          <span>收货地址</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item" v-if="userStore.isAdmin" @click="$router.push('/admin/categories')">
          <el-icon><Setting /></el-icon>
          <span>后台管理</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import AppLayout from '@/components/common/AppLayout.vue'
import { Document, Van, CircleCheck, Close, Goods, Star, Location, SwitchButton, ArrowRight, Setting } from '@element-plus/icons-vue'
import { getAvatarUrl } from '@/utils/image'

const router = useRouter()
const userStore = useUserStore()

const userInfo = userStore.userInfo

const goToEdit = () => {
  router.push('/profile/edit')
}

const goToOrders = (status, role) => {
  const query = {}
  if (status) {
    query.status = status
  }
  if (role) {
    query.role = role
  }
  router.push({ path: '/my-orders', query })
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
  } catch {
  }
}
</script>

<style scoped>
.profile-page {
  padding: var(--spacing-lg);
}

.user-card {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.nickname {
  font-size: var(--font-size-h2);
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.school {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.order-section {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  cursor: pointer;
}

.section-title {
  font-size: var(--font-size-h3);
  font-weight: bold;
  color: var(--color-text-primary);
}

.section-more {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.order-tabs {
  display: flex;
  justify-content: space-around;
}

.order-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: var(--font-size-small);
}

.order-tab:hover {
  color: var(--color-primary);
}

.order-entrance {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border);
}

.order-entrance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) 0;
  cursor: pointer;
  transition: background var(--duration-normal);
}

.order-entrance-item:first-child {
  padding-top: 0;
}

.order-entrance-item:hover {
  background: var(--color-bg);
  margin: 0 calc(-1 * var(--spacing-lg));
  padding-left: var(--spacing-lg);
  padding-right: var(--spacing-lg);
}

.entrance-label {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.menu-section {
  background: var(--color-card);
  border-radius: var(--radius-card);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: background var(--duration-normal);
}

.menu-item:not(:last-child) {
  border-bottom: 1px solid var(--color-border);
}

.menu-item:hover {
  background: var(--color-bg);
}

.menu-item span {
  flex: 1;
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.arrow {
  color: var(--color-text-tertiary);
}
</style>
