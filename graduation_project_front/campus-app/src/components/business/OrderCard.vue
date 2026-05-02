<template>
  <div class="order-card" @click="handleClick">
    <div class="order-header">
      <span class="order-no">{{ order.orderNo }}</span>
      <el-tag :type="statusType" size="small">{{ order.statusText }}</el-tag>
    </div>
    <div class="order-content">
      <img v-if="order.productImage" :src="formatImageUrl(order.productImage)" class="product-image" />
      <div v-else class="product-image-placeholder">
        <el-icon><Picture /></el-icon>
      </div>
      <div class="product-info">
        <div class="product-title">{{ order.productTitle }}</div>
        <div class="product-price">¥{{ order.productPrice }}</div>
      </div>
    </div>
    <div class="order-footer">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { formatImageUrl } from '@/utils/image'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const statusType = computed(() => {
  switch (props.order.status) {
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

const handleClick = () => {
  emit('click', props.order)
}
</script>

<style scoped>
.order-card {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: all var(--duration-normal);
}

.order-card:hover {
  box-shadow: var(--shadow-card-hover);
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.order-no {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.order-content {
  display: flex;
  gap: var(--spacing-md);
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
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: var(--font-size-h3);
  font-weight: bold;
  color: var(--color-primary);
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-md);
  gap: var(--spacing-sm);
}
</style>
