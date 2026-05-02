<template>
  <AppLayout>
    <div class="profile-page">
      <!-- 用户卡片 -->
      <div class="user-card">
        <div class="user-bg"></div>
        <div class="user-content">
          <div class="avatar-wrapper">
            <img :src="getAvatarUrl(userInfo?.avatar)" class="avatar" />
            <div class="avatar-edit" @click="goToEdit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </div>
          </div>
          <div class="user-info">
            <div class="nickname">{{ userInfo?.nickname || '校园用户' }}</div>
            <div class="school">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2L1 7l11 5 9-4.09V17h2V7L12 2z"/>
                <path d="M12 13L5.4 9.83v5.82A6.05 6.05 0 0012 21a6.05 6.05 0 006.6-5.35V9.83L12 13z"/>
              </svg>
              {{ userInfo?.school || '未设置学校' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 订单快捷入口 -->
      <div class="section-card">
        <div class="section-header">
          <span class="section-title">我的订单</span>
          <div class="section-action" @click="goToOrders()">
            <span>全部订单</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </div>
        </div>
        <div class="order-grid">
          <div class="order-item" @click="goToOrders(1)">
            <div class="order-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
              </svg>
            </div>
            <span>待确认</span>
          </div>
          <div class="order-item" @click="goToOrders(2)">
            <div class="order-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <rect x="1" y="3" width="15" height="13" rx="2"/>
                <path d="M16 8h4l3 3v5a2 2 0 01-2 2h-5"/>
                <circle cx="5.5" cy="18.5" r="2.5"/>
                <circle cx="18.5" cy="18.5" r="2.5"/>
              </svg>
            </div>
            <span>交易中</span>
          </div>
          <div class="order-item" @click="goToOrders(3)">
            <div class="order-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
                <path d="M22 4L12 14.01l-3-3"/>
              </svg>
            </div>
            <span>已完成</span>
          </div>
          <div class="order-item" @click="goToOrders(4)">
            <div class="order-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                <circle cx="12" cy="12" r="10"/>
                <path d="M15 9l-6 6M9 9l6 6"/>
              </svg>
            </div>
            <span>已取消</span>
          </div>
        </div>
        <div class="order-links">
          <div class="order-link" @click="goToOrders(undefined, 'buyer')">
            <span>我买到的</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </div>
          <div class="order-link" @click="goToOrders(undefined, 'seller')">
            <span>我卖出的</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="section-card">
        <div class="menu-item" @click="$router.push('/my-products')">
          <div class="menu-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
            </svg>
          </div>
          <span class="menu-text">我的发布</span>
          <svg class="menu-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
        <div class="menu-item" @click="$router.push('/favorites')">
          <div class="menu-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/>
            </svg>
          </div>
          <span class="menu-text">我的收藏</span>
          <svg class="menu-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
        <div class="menu-item" @click="$router.push('/address')">
          <div class="menu-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
          </div>
          <span class="menu-text">收货地址</span>
          <svg class="menu-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
      </div>

      <!-- 其他功能 -->
      <div class="section-card">
        <div class="menu-item" v-if="userStore.isAdmin" @click="$router.push('/admin/categories')">
          <div class="menu-icon admin">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
            </svg>
          </div>
          <span class="menu-text">后台管理</span>
          <svg class="menu-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
        <div class="menu-item logout" @click="handleLogout">
          <div class="menu-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
              <polyline points="16,17 21,12 16,7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
          </div>
          <span class="menu-text">退出登录</span>
          <svg class="menu-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import AppLayout from '@/components/common/AppLayout.vue'
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
  min-height: 100vh;
  background-color: var(--color-bg);
  padding: var(--padding-page);
  padding-bottom: calc(var(--tabbar-height) + var(--safe-area-bottom) + var(--spacing-xl));
}

/* 用户卡片 */
.user-card {
  position: relative;
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-card);
}

.user-bg {
  height: 80px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
}

.user-content {
  display: flex;
  align-items: flex-end;
  gap: var(--spacing-lg);
  padding: 0 var(--spacing-xl) var(--spacing-xl);
  margin-top: -40px;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-circle);
  object-fit: cover;
  border: 4px solid var(--color-card);
  box-shadow: var(--shadow-card);
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: var(--color-card);
  border-radius: var(--radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-fast);
}

.avatar-edit:hover {
  background: var(--color-primary-bg);
}

.avatar-edit svg {
  width: 14px;
  height: 14px;
  color: var(--color-primary);
}

.user-info {
  flex: 1;
  padding-bottom: var(--spacing-xs);
}

.nickname {
  font-size: var(--font-size-h2);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-xs);
}

.school {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.school svg {
  width: 16px;
  height: 16px;
  color: var(--color-primary);
}

/* 区块卡片 */
.section-card {
  background: var(--color-card);
  border-radius: var(--radius-card);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--color-divider);
}

.section-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.section-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.section-action:hover {
  color: var(--color-primary);
}

.section-action svg {
  width: 16px;
  height: 16px;
}

/* 订单网格 */
.order-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  padding: var(--spacing-lg);
}

.order-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.order-item:active {
  transform: scale(0.95);
}

.order-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  border-radius: var(--radius-card);
  transition: all var(--duration-fast);
}

.order-item:hover .order-icon {
  background: var(--color-primary-bg);
}

.order-icon svg {
  width: 24px;
  height: 24px;
  color: var(--color-text-secondary);
}

.order-item:hover .order-icon svg {
  color: var(--color-primary);
}

.order-item span {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

/* 订单链接 */
.order-links {
  border-top: 1px solid var(--color-divider);
}

.order-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.order-link:not(:last-child) {
  border-bottom: 1px solid var(--color-divider);
}

.order-link:hover {
  background: var(--color-bg);
}

.order-link span {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.order-link svg {
  width: 18px;
  height: 18px;
  color: var(--color-text-tertiary);
}

/* 菜单项 */
.menu-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.menu-item:not(:last-child) {
  border-bottom: 1px solid var(--color-divider);
}

.menu-item:hover {
  background: var(--color-bg);
}

.menu-item:active {
  background: var(--color-border-light);
}

.menu-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary-bg);
  border-radius: var(--radius-sm);
}

.menu-icon svg {
  width: 22px;
  height: 22px;
  color: var(--color-primary);
}

.menu-icon.admin {
  background: rgba(245, 166, 35, 0.12);
}

.menu-icon.admin svg {
  color: var(--color-warning);
}

.menu-item.logout .menu-icon {
  background: rgba(239, 107, 107, 0.12);
}

.menu-item.logout .menu-icon svg {
  color: var(--color-danger);
}

.menu-text {
  flex: 1;
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.menu-arrow {
  width: 18px;
  height: 18px;
  color: var(--color-text-quaternary);
}
</style>
