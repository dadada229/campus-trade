<template>
  <AppLayout>
    <div class="order-detail-page">
      <template v-if="loading">
        <el-skeleton :rows="10" animated />
      </template>
      <template v-else-if="order">
        <div class="order-header">
          <el-tag :type="statusType" size="large">{{ order.statusText }}</el-tag>
          <div class="order-no">{{ order.orderNo }}</div>
        </div>

        <div class="section">
          <div class="section-title">商品信息</div>
          <div class="product-card" @click="goToProduct">
            <img v-if="order.productImage" :src="formatImageUrl(order.productImage)" class="product-image" />
            <div v-else class="product-image-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
            <div class="product-info">
              <div class="product-title">{{ order.productTitle }}</div>
              <div class="product-price">¥{{ order.productPrice }}</div>
            </div>
          </div>
        </div>

        <div class="section">
          <div class="section-title">收货地址</div>
          <div class="address-info">
            <div class="address-name">{{ order.addressSnapshot?.receiverName }}</div>
            <div class="address-phone">{{ order.addressSnapshot?.phone }}</div>
            <div class="address-detail">
              {{ order.addressSnapshot?.province }}{{ order.addressSnapshot?.city }}{{ order.addressSnapshot?.district }}{{ order.addressSnapshot?.detail }}
            </div>
          </div>
        </div>

        <div v-if="order.buyerMessage" class="section">
          <div class="section-title">买家留言</div>
          <div class="message">{{ order.buyerMessage }}</div>
        </div>

        <div class="section">
          <div class="section-title">订单状态</div>
          <el-steps :active="order.status" finish-status="success" simple>
            <el-step title="待确认" description="订单已创建" />
            <el-step title="交易中" description="卖家已确认" />
            <el-step title="已完成" description="交易已完成" />
          </el-steps>
        </div>

        <div class="action-buttons" v-if="order.status === 1 || order.status === 2">
          <el-button v-if="order.status === 1 && order.isSeller" type="primary" @click="handleConfirm">确认订单</el-button>
          <el-button v-if="order.status === 2 && order.isBuyer" type="success" @click="handleComplete">确认完成</el-button>
          <el-button v-if="order.status === 1 && order.isBuyer" type="danger" @click="handleCancel">取消订单</el-button>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getOrderDetail, confirmOrder, completeOrder, cancelOrder } from '@/api/modules/order'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)

const statusType = computed(() => {
  switch (order.value?.status) {
    case 1:
      return 'warning'
    case 2:
      return 'primary'
    case 3:
      return 'success'
    case 4:
      return 'info'
    default:
      return 'info'
  }
})

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
    await ElMessageBox.confirm('确定要确认该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
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
    await ElMessageBox.confirm('确定要完成该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await completeOrder(order.value.id)
    ElMessage.success('完成成功')
    fetchOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('完成失败')
    }
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
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
  padding: var(--spacing-lg);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.order-no {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.section {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-card);
}

.section-title {
  font-size: var(--font-size-h3);
  font-weight: bold;
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.product-card {
  display: flex;
  gap: var(--spacing-md);
  cursor: pointer;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-input);
  object-fit: cover;
}

.product-image-placeholder {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-input);
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
}

.product-image-placeholder .el-icon {
  font-size: 32px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-title {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.product-price {
  font-size: var(--font-size-h3);
  font-weight: bold;
  color: var(--color-primary);
}

.address-info {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
}

.address-name {
  font-weight: bold;
  margin-bottom: var(--spacing-xs);
}

.address-phone {
  margin-bottom: var(--spacing-xs);
}

.address-detail {
  color: var(--color-text-secondary);
}

.message {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  padding: var(--spacing-md);
  background: var(--color-bg);
  border-radius: var(--radius-input);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.action-buttons .el-button {
  flex: 1;
}
</style>
