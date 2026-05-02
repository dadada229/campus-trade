<template>
  <div class="order-card" @click="handleClick">
    <div class="order-header">
      <span class="order-no">订单 {{ order.orderNo }}</span>
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
        <el-icon v-else :size="28"><Picture /></el-icon>
      </div>
      <div class="product-info">
        <h3 class="product-title">{{ order.productTitle }}</h3>
        <p class="product-price">¥{{ order.productPrice }}</p>
      </div>
      <el-icon class="arrow-icon"><ArrowRight /></el-icon>
    </div>

    <div v-if="$slots.actions" class="order-footer">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
import { Picture, ArrowRight } from '@element-plus/icons-vue'
import { formatImageUrl } from '@/utils/image'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.order)
}
</script>

<style scoped>
.order-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all 0.2s ease;
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
  align-items: center;
  gap: var(--spacing-md);
}

.product-image {
  width: 64px;
  height: 64px;
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
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-primary);
  margin: 0;
}

.arrow-icon {
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
  gap: var(--spacing-sm);
}
</style>
