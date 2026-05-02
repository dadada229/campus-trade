<template>
  <div class="product-card" @click="goDetail">
    <div class="card-image-wrapper">
      <LazyImage
        :src="formatImageUrl(product.images?.[0])"
        :alt="product.title"
        aspectRatio="1"
      />
      <div v-if="product.status !== 1" class="status-overlay">
        <span class="status-text">{{ statusText }}</span>
      </div>
      <div v-if="showFavorite" class="favorite-btn" @click.stop="toggleFavorite">
        <div class="favorite-icon" :class="{ favorited: isFavorited, 'bounce-in': isAnimating }">
          <svg v-if="isFavorited" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
        </div>
      </div>
      <div v-if="product.condition" class="condition-badge">
        {{ getConditionText(product.condition) }}
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ product.title }}</h3>
      <div class="card-price-row">
        <div class="price-wrapper">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ formatPrice(product.price) }}</span>
        </div>
        <span v-if="product.originalPrice" class="original-price">¥{{ formatPrice(product.originalPrice) }}</span>
      </div>
      <div class="card-footer">
        <div class="seller-info">
          <div class="seller-avatar">
            <span>{{ sellerAvatarChar }}</span>
          </div>
          <span class="seller-name">{{ product.sellerName || '校园用户' }}</span>
        </div>
        <div class="meta-info">
          <span v-if="product.tradePlace" class="location">{{ product.tradePlace }}</span>
          <span v-else-if="product.viewCount" class="view-count">{{ product.viewCount }}浏览</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import LazyImage from './LazyImage.vue'
import { formatImageUrl } from '@/utils/image'

const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  showFavorite: {
    type: Boolean,
    default: true
  },
  isFavorited: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['favorite', 'unfavorite'])

const router = useRouter()
const isAnimating = ref(false)

const statusText = computed(() => {
  const statusMap = {
    1: '',
    2: '已售出',
    3: '已下架'
  }
  return statusMap[props.product.status] || ''
})

const sellerAvatarChar = computed(() => {
  const name = props.product.sellerName || 'U'
  return name.charAt(0).toUpperCase()
})

const formatPrice = (value) => {
  const num = Number(value)
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num % 1 === 0 ? num : num.toFixed(2)
}

const getConditionText = (condition) => {
  const conditionMap = {
    1: '全新',
    2: '几乎全新',
    3: '轻微使用',
    4: '明显痕迹'
  }
  return conditionMap[condition] || ''
}

const goDetail = () => {
  router.push(`/product/${props.product.id}`)
}

const toggleFavorite = () => {
  isAnimating.value = true
  nextTick(() => {
    if (props.isFavorited) {
      emit('unfavorite', props.product.id)
    } else {
      emit('favorite', props.product.id)
    }
    setTimeout(() => {
      isAnimating.value = false
    }, 300)
  })
}
</script>

<style scoped>
.product-card {
  background-color: var(--color-card);
  border-radius: var(--radius-card);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-smooth);
  display: flex;
  flex-direction: column;
  height: 100%;
  box-shadow: var(--shadow-card);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.product-card:active {
  transform: translateY(-2px) scale(0.99);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  background: var(--color-bg);
}

.status-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}

.status-text {
  color: #fff;
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-medium);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: rgba(0, 0, 0, 0.3);
  border-radius: var(--radius-sm);
}

.condition-badge {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  color: var(--color-text-secondary);
  padding: 3px 8px;
  border-radius: var(--radius-xs);
  font-size: var(--font-size-mini);
  font-weight: var(--font-weight-medium);
  box-shadow: var(--shadow-xs);
}

.favorite-btn {
  position: absolute;
  bottom: var(--spacing-sm);
  right: var(--spacing-sm);
  width: 34px;
  height: 34px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-radius: var(--radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: all var(--duration-fast) var(--ease-standard);
}

.favorite-btn:hover {
  transform: scale(1.08);
}

.favorite-btn:active {
  transform: scale(0.95);
}

.favorite-icon {
  width: 18px;
  height: 18px;
  color: var(--color-text-tertiary);
  transition: all var(--duration-fast) var(--ease-standard);
}

.favorite-icon.favorited {
  color: var(--color-danger);
}

.favorite-icon svg {
  width: 100%;
  height: 100%;
}

.bounce-in {
  animation: bounce-in 0.3s var(--ease-bounce);
}

@keyframes bounce-in {
  0% { transform: scale(0.5); }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.card-content {
  padding: var(--spacing-md);
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.card-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-normal);
  color: var(--color-text-primary);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.45;
  letter-spacing: -0.01em;
}

.card-price-row {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
  margin-top: auto;
}

.price-wrapper {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: var(--font-size-small);
  color: var(--color-price);
  font-weight: var(--font-weight-semibold);
}

.price-value {
  font-size: var(--font-size-price);
  font-weight: var(--font-weight-bold);
  color: var(--color-price);
  letter-spacing: -0.02em;
}

.original-price {
  font-size: var(--font-size-mini);
  color: var(--color-text-quaternary);
  text-decoration: line-through;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-sm);
  border-top: 1px solid var(--color-divider);
}

.seller-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  flex: 1;
  min-width: 0;
}

.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: var(--radius-circle);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.seller-avatar span {
  color: #fff;
  font-size: 10px;
  font-weight: var(--font-weight-semibold);
}

.seller-name {
  font-size: var(--font-size-mini);
  color: var(--color-text-tertiary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta-info {
  flex-shrink: 0;
}

.location,
.view-count {
  font-size: var(--font-size-mini);
  color: var(--color-text-quaternary);
}
</style>
