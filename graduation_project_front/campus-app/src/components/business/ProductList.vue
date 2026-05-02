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
          <el-skeleton :rows="4" animated />
        </div>
      </template>
    </div>
    <div v-if="loading && !noMore" class="loading-more">
      <el-icon class="loading-icon is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div v-else-if="noMore && products.length" class="no-more">
      <span>没有更多了</span>
    </div>
    <el-empty v-if="!loading && !products.length" description="暂无商品" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
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
  border-radius: var(--radius-card);
  padding: var(--spacing-sm);
}

.loading-more,
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: var(--spacing-lg);
  color: var(--color-text-secondary);
  font-size: 14px;
}

.loading-icon {
  font-size: 16px;
}
</style>