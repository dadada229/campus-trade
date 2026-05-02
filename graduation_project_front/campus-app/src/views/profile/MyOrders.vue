<template>
  <AppLayout>
    <div class="my-orders-page">
      <!-- 角色切换 -->
      <div class="role-switcher">
        <button 
          :class="['role-btn', { active: roleTab === 'buyer' }]"
          @click="switchRole('buyer')"
        >
          <el-icon><ShoppingBag /></el-icon>
          <span>我买到的</span>
        </button>
        <button 
          :class="['role-btn', { active: roleTab === 'seller' }]"
          @click="switchRole('seller')"
        >
          <el-icon><Sell /></el-icon>
          <span>我卖出的</span>
        </button>
      </div>

      <!-- 状态筛选 -->
      <div class="status-filter">
        <button 
          v-for="tab in statusTabs" 
          :key="tab.value"
          :class="['filter-btn', { active: activeTab === tab.value }]"
          @click="switchStatus(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 订单列表 -->
      <div class="orders-container">
        <template v-if="loading">
          <div class="skeleton-list">
            <div v-for="i in 3" :key="i" class="skeleton-item">
              <el-skeleton :rows="3" animated />
            </div>
          </div>
        </template>

        <template v-else-if="orders.length === 0">
          <div class="empty-state">
            <div class="empty-icon">
              <el-icon :size="48"><Document /></el-icon>
            </div>
            <p class="empty-text">{{ roleTab === 'buyer' ? '暂无购买订单' : '暂无出售订单' }}</p>
            <router-link v-if="roleTab === 'buyer'" to="/" class="browse-btn">
              去逛逛
            </router-link>
          </div>
        </template>

        <template v-else>
          <div class="order-list">
            <div 
              v-for="order in orders" 
              :key="order.id" 
              class="order-card"
              @click="goToDetail(order.id)"
            >
              <div class="order-header">
                <span class="order-no">订单号: {{ order.orderNo }}</span>
                <span :class="['order-status', `status-${order.status}`]">
                  {{ order.statusText }}
                </span>
              </div>
              
              <div class="order-content">
                <div class="product-image">
                  <img 
                    v-if="order.productImage" 
                    :src="formatImageUrl(order.productImage)" 
                    :alt="order.productTitle"
                  />
                  <el-icon v-else :size="32"><Picture /></el-icon>
                </div>
                <div class="product-info">
                  <h3 class="product-title">{{ order.productTitle }}</h3>
                  <p class="product-price">¥{{ order.productPrice }}</p>
                </div>
              </div>

              <div class="order-footer">
                <span class="order-time">{{ formatTime(order.createTime) }}</span>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingBag, Sell, Document, Picture, ArrowRight } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getUserOrders } from '@/api/modules/order'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const orders = ref([])
const activeTab = ref('all')
const roleTab = ref('buyer')

const statusTabs = [
  { label: '全部', value: 'all' },
  { label: '待确认', value: '1' },
  { label: '交易中', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已取消', value: '4' }
]

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const updateURLParams = () => {
  const query = {}
  if (activeTab.value !== 'all') {
    query.status = activeTab.value
  }
  query.role = roleTab.value
  router.replace({ query })
}

const fetchOrders = async () => {
  try {
    loading.value = true
    const params = activeTab.value !== 'all' 
      ? { status: activeTab.value, role: roleTab.value } 
      : { role: roleTab.value }
    const res = await getUserOrders(params)
    if (res && res.data) {
      orders.value = res.data.list || res.data
    } else {
      orders.value = []
    }
  } catch (error) {
    console.error('获取订单列表失败', error)
    ElMessage.error('获取订单列表失败')
    orders.value = []
  } finally {
    loading.value = false
  }
}

const switchRole = (role) => {
  roleTab.value = role
  updateURLParams()
  fetchOrders()
}

const switchStatus = (status) => {
  activeTab.value = status
  updateURLParams()
  fetchOrders()
}

const goToDetail = (id) => {
  router.push(`/order/${id}`)
}

watch(() => route.query.status, (status) => {
  if (status) {
    activeTab.value = status
  }
}, { immediate: true })

watch(() => route.query.role, (role) => {
  if (role === 'seller' || role === 'buyer') {
    roleTab.value = role
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.status) {
    activeTab.value = route.query.status
  }
  if (route.query.role === 'seller') {
    roleTab.value = 'seller'
  }
  fetchOrders()
})
</script>

<style scoped>
.my-orders-page {
  min-height: 100vh;
  background: var(--color-bg);
}

.role-switcher {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-card);
  border-bottom: 1px solid var(--color-border);
}

.role-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  border: 2px solid var(--color-border);
  border-radius: var(--radius-lg);
  background: var(--color-bg);
  color: var(--color-text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.role-btn.active {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.role-btn .el-icon {
  font-size: 18px;
}

.status-filter {
  display: flex;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--color-card);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.status-filter::-webkit-scrollbar {
  display: none;
}

.filter-btn {
  flex-shrink: 0;
  padding: var(--spacing-sm) var(--spacing-lg);
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-bg);
  color: var(--color-text-secondary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-btn.active {
  background: var(--color-primary);
  color: white;
}

.orders-container {
  padding: var(--spacing-lg);
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-item {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
}

.empty-icon {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-full);
  background: var(--color-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
  margin-bottom: var(--spacing-lg);
}

.empty-text {
  font-size: 15px;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-lg) 0;
}

.browse-btn {
  padding: var(--spacing-sm) var(--spacing-xl);
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
}

.browse-btn:hover {
  background: var(--color-secondary);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.order-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
}

.order-no {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.order-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: var(--radius-full);
}

.order-status.status-1 {
  background: var(--color-warning-light);
  color: var(--color-warning);
}

.order-status.status-2 {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.order-status.status-3 {
  background: var(--color-success-light);
  color: var(--color-success);
}

.order-status.status-4 {
  background: var(--color-bg-secondary);
  color: var(--color-text-tertiary);
}

.order-content {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.product-image {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-md);
  background: var(--color-bg);
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-sm) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
  margin: 0;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
}

.order-time {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.arrow-icon {
  color: var(--color-text-tertiary);
}
</style>
