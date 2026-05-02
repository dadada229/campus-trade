<template>
  <AppLayout>
    <div class="order-detail-page">
      <template v-if="loading">
        <div class="loading-container">
          <el-skeleton :rows="10" animated />
        </div>
      </template>

      <template v-else-if="order">
        <!-- 状态头部 -->
        <div :class="['status-header', `status-${order.status}`]">
          <div class="status-icon">
            <el-icon :size="32">
              <component :is="statusIcon" />
            </el-icon>
          </div>
          <h2 class="status-text">{{ order.statusText }}</h2>
          <p class="status-desc">{{ statusDescription }}</p>
        </div>

        <!-- 商品信息 -->
        <div class="section">
          <div class="section-header">
            <el-icon><Goods /></el-icon>
            <span>商品信息</span>
          </div>
          <div class="product-card" @click="goToProduct">
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
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="section">
          <div class="section-header">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </div>
          <div class="address-card">
            <div class="address-top">
              <span class="receiver-name">{{ order.addressSnapshot?.receiverName }}</span>
              <span class="receiver-phone">{{ order.addressSnapshot?.phone }}</span>
            </div>
            <p class="address-detail">
              {{ order.addressSnapshot?.province }}{{ order.addressSnapshot?.city }}{{ order.addressSnapshot?.district }}{{ order.addressSnapshot?.detail }}
            </p>
          </div>
        </div>

        <!-- 买家留言 -->
        <div v-if="order.buyerMessage" class="section">
          <div class="section-header">
            <el-icon><ChatDotRound /></el-icon>
            <span>买家留言</span>
          </div>
          <div class="message-card">
            {{ order.buyerMessage }}
          </div>
        </div>

        <!-- 订单进度 -->
        <div class="section">
          <div class="section-header">
            <el-icon><Clock /></el-icon>
            <span>订单进度</span>
          </div>
          <div class="timeline">
            <div 
              v-for="(step, index) in orderSteps" 
              :key="index"
              :class="['timeline-item', { active: index < order.status, current: index === order.status - 1 }]"
            >
              <div class="timeline-dot"></div>
              <div class="timeline-content">
                <span class="step-title">{{ step.title }}</span>
                <span class="step-desc">{{ step.desc }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="section">
          <div class="section-header">
            <el-icon><Document /></el-icon>
            <span>订单信息</span>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">订单编号</span>
              <span class="value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">创建时间</span>
              <span class="value">{{ formatTime(order.createTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar" v-if="showActions">
          <el-button 
            v-if="order.status === 1 && order.isBuyer" 
            type="danger" 
            plain
            size="large"
            @click="handleCancel"
          >
            取消订单
          </el-button>
          <el-button 
            v-if="order.status === 1 && order.isSeller" 
            type="primary" 
            size="large"
            @click="handleConfirm"
          >
            确认订单
          </el-button>
          <el-button 
            v-if="order.status === 2 && order.isBuyer" 
            type="success" 
            size="large"
            @click="handleComplete"
          >
            确认收货
          </el-button>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Picture, ArrowRight, Location, ChatDotRound, Clock, Document, Goods,
  CircleCheck, Loading, SuccessFilled, CircleClose
} from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getOrderDetail, confirmOrder, completeOrder, cancelOrder } from '@/api/modules/order'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)

const orderSteps = [
  { title: '订单创建', desc: '等待卖家确认' },
  { title: '卖家确认', desc: '交易进行中' },
  { title: '交易完成', desc: '订单已完成' }
]

const statusIcon = computed(() => {
  switch (order.value?.status) {
    case 1: return Loading
    case 2: return CircleCheck
    case 3: return SuccessFilled
    case 4: return CircleClose
    default: return Document
  }
})

const statusDescription = computed(() => {
  switch (order.value?.status) {
    case 1: return '等待卖家确认订单'
    case 2: return '交易进行中，请与卖家联系'
    case 3: return '交易已完成，感谢您的信任'
    case 4: return '订单已取消'
    default: return ''
  }
})

const showActions = computed(() => {
  return order.value?.status === 1 || order.value?.status === 2
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchOrder = async () => {
  try {
    loading.value = true
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const goToProduct = () => {
  router.push(`/product/${order.value.productId}`)
}

const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm('确定要确认该订单吗？', '确认订单', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await confirmOrder(order.value.id)
    ElMessage.success('确认成功')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const handleComplete = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'success'
    })
    await completeOrder(order.value.id)
    ElMessage.success('交易完成')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    await cancelOrder(order.value.id)
    ElMessage.success('取消成功')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding-bottom: 100px;
}

.loading-container {
  padding: var(--spacing-lg);
}

.status-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  color: white;
}

.status-header.status-3 {
  background: linear-gradient(135deg, var(--color-success), #059669);
}

.status-header.status-4 {
  background: linear-gradient(135deg, var(--color-text-secondary), var(--color-text-tertiary));
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-full);
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-md);
}

.status-text {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 var(--spacing-xs) 0;
}

.status-desc {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.section {
  background: var(--color-card);
  margin: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
}

.section-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.section-header .el-icon {
  color: var(--color-primary);
}

.product-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  cursor: pointer;
  padding: var(--spacing-sm);
  margin: calc(-1 * var(--spacing-sm));
  border-radius: var(--radius-md);
  transition: background 0.2s ease;
}

.product-card:hover {
  background: var(--color-bg);
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

.arrow {
  color: var(--color-text-tertiary);
}

.address-card {
  background: var(--color-bg);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
}

.address-top {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.receiver-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.receiver-phone {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.address-detail {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
}

.message-card {
  background: var(--color-bg);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.timeline {
  position: relative;
  padding-left: var(--spacing-lg);
}

.timeline-item {
  position: relative;
  padding-bottom: var(--spacing-lg);
  padding-left: var(--spacing-lg);
}

.timeline-item:last-child {
  padding-bottom: 0;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 0;
  width: 2px;
  background: var(--color-border);
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-item.active::before {
  background: var(--color-primary);
}

.timeline-dot {
  position: absolute;
  left: -5px;
  top: 4px;
  width: 12px;
  height: 12px;
  border-radius: var(--radius-full);
  background: var(--color-border);
  border: 2px solid var(--color-card);
}

.timeline-item.active .timeline-dot {
  background: var(--color-primary);
}

.timeline-item.current .timeline-dot {
  box-shadow: 0 0 0 4px var(--color-primary-light);
}

.timeline-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.timeline-item:not(.active) .step-title {
  color: var(--color-text-tertiary);
}

.step-desc {
  font-size: 12px;
  color: var(--color-text-tertiary);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.info-item .value {
  font-size: 14px;
  color: var(--color-text-primary);
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-card);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.action-bar .el-button {
  flex: 1;
  height: 48px;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 600;
}
</style>
