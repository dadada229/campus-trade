<template>
  <div class="product-card" @click="goDetail">
    <div class="card-image-wrapper">
      <LazyImage
        :src="formatImageUrl(product.images?.[0])"
        :alt="product.title"
        aspectRatio="1"
      />
      <div v-if="product.status !== 1" class="status-tag">
        {{ statusText }}
      </div>
      <div v-if="showFavorite" class="favorite-btn" @click.stop="toggleFavorite">
        <el-icon :class="{ favorited: isFavorited, 'bounce-in': isAnimating }">
          <StarFilled v-if="isFavorited" />
          <Star v-else />
        </el-icon>
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ product.title }}</h3>
      <PriceTag
        :price="product.price"
        :original-price="product.originalPrice"
      />
      <div class="card-footer">
        <div class="seller-info">
          <div class="seller-avatar">{{ sellerAvatarChar }}</div>
          <span class="seller-name">{{ product.sellerName || '用户' }}</span>
        </div>
        <span class="view-count">{{ product.viewCount || 0 }} 浏览</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Star, StarFilled } from '@element-plus/icons-vue'
import LazyImage from './LazyImage.vue'
import PriceTag from './PriceTag.vue'
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
    2: '已售罄',
    3: '已下架'
  }
  return statusMap[props.product.status] || ''
})

const sellerAvatarChar = computed(() => {
  const name = props.product.sellerName || 'U'
  return name.charAt(0).toUpperCase()
})

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
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: all var(--duration-normal);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
}

.status-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.favorite-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.favorite-btn el-icon {
  font-size: 18px;
  color: var(--color-text-tertiary);
}

.favorite-btn .favorited {
  color: var(--color-danger);
}

.bounce-in {
  animation: bounce-in 0.3s var(--ease-bounce);
}

@keyframes bounce-in {
  0% { transform: scale(0); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.card-content {
  padding: var(--spacing-md);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 14px;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-sm) 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  flex: 0 0 auto;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-sm);
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

.seller-name {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.view-count {
  font-size: 11px;
  color: var(--color-text-tertiary);
}
</style>
