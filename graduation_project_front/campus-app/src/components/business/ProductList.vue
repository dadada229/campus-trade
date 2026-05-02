<template>
  <div class="product-list" ref="containerRef">
    <div class="products-grid">
      <template v-if="!loading || products.length">
        <ProductCard
          v-for="product in products"
          :key="product.id"
          :product="product"
          :is-favorited="favorites.has(product.id)"
          @favorite="handleFavorite"
          @unfavorite="handleUnfavorite"
        />
      </template>
      <template v-else>
        <div v-for="i in 4" :key="i" class="skeleton-card">
          <div class="skeleton-image"></div>
          <div class="skeleton-content">
            <div class="skeleton-line long"></div>
            <div class="skeleton-line short"></div>
          </div>
        </div>
      </template>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading && !noMore && products.length" class="loading-more">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <!-- 无更多数据 -->
    <div v-else-if="noMore && products.length" class="no-more">
      <div class="divider"></div>
      <span>已经到底了</span>
      <div class="divider"></div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !products.length" class="empty-state">
      <div class="empty-icon">
        <el-icon :size="48"><Box /></el-icon>
      </div>
      <p class="empty-text">暂无商品</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Box } from '@element-plus/icons-vue'
import ProductCard from '@/components/common/ProductCard.vue'
import { favoriteProduct, unfavoriteProduct } from '@/api/modules/product'

const props = defineProps({
  products: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  noMore: {
    type: Boolean,
    default: false
  },
  favorites: {
    type: Set,
    default: () => new Set()
  }
})

const emit = defineEmits(['loadMore', 'favorite', 'unfavorite'])

const containerRef = ref(null)

const handleScroll = () => {
  if (!containerRef.value || props.loading || props.noMore) return
  const { scrollTop, scrollHeight, clientHeight } = containerRef.value
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    emit('loadMore')
  }
}

const handleFavorite = async (id) => {
  try {
    await favoriteProduct(id)
    emit('favorite', id)
    ElMessage.success('收藏成功')
  } catch (e) {
    ElMessage.error('收藏失败')
  }
}

const handleUnfavorite = async (id) => {
  try {
    await unfavoriteProduct(id)
    emit('unfavorite', id)
    ElMessage.success('取消收藏成功')
  } catch (e) {
    ElMessage.error('取消收藏失败')
  }
}

onMounted(() => {
  if (containerRef.value) {
    containerRef.value.addEventListener('scroll', handleScroll)
  }
})

onUnmounted(() => {
  if (containerRef.value) {
    containerRef.value.removeEventListener('scroll', handleScroll)
  }
})
</script>

<style scoped>
.product-list {
  height: 100%;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
  padding: var(--spacing-md);
}

@media (min-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.skeleton-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.skeleton-image {
  aspect-ratio: 1;
  background: linear-gradient(90deg, var(--color-bg) 25%, var(--color-bg-secondary) 50%, var(--color-bg) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  padding: var(--spacing-md);
}

.skeleton-line {
  height: 14px;
  border-radius: var(--radius-sm);
  background: linear-gradient(90deg, var(--color-bg) 25%, var(--color-bg-secondary) 50%, var(--color-bg) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  margin-bottom: var(--spacing-sm);
}

.skeleton-line.long {
  width: 100%;
}

.skeleton-line.short {
  width: 60%;
  margin-bottom: 0;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  color: var(--color-text-secondary);
  font-size: 14px;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-full);
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.divider {
  width: 40px;
  height: 1px;
  background: var(--color-border);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl);
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
  margin: 0;
}
</style>
