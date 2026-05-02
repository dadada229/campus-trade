<template>
  <AppLayout>
    <div class="home-page">
      <div class="search-bar" @click="goSearch">
        <el-icon class="search-icon"><Search /></el-icon>
        <span class="search-placeholder">搜索商品</span>
      </div>

      <el-carousel v-if="banners.length" class="banner-carousel" height="180px" indicator-position="none" autoplay>
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }"></div>
        </el-carousel-item>
      </el-carousel>

      <div v-if="notices.length" class="notice-bar">
        <div class="notice-content">
          <el-icon class="notice-icon"><Bell /></el-icon>
          <div class="notice-text">
            <div class="notice-title">{{ notices[0]?.title }}</div>
            <div v-if="notices[0]?.content" class="notice-content-preview">{{ notices[0].content }}</div>
          </div>
        </div>
      </div>

      <div class="category-nav">
        <div class="category-list">
          <div class="category-item" v-for="category in topCategories" :key="category.id" @click="goCategory(category.id)">
            <div class="category-icon">{{ getCategoryIcon(category.id) }}</div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
      </div>

      <div class="products-section">
        <div class="section-header">
          <h2 class="section-title">最新发布</h2>
        </div>
        <div v-if="initialLoading" class="skeleton-grid">
          <div v-for="i in 4" :key="i" class="skeleton-card">
            <el-skeleton :rows="4" animated />
          </div>
        </div>
        <template v-else>
          <div class="products-grid">
            <ProductCard
              v-for="product in products"
              :key="product.id"
              :product="product"
              :is-favorited="favorites.has(product.id)"
              @favorite="handleFavorite"
              @unfavorite="handleUnfavorite"
            />
          </div>
          <div v-if="loadingMore" class="loading-more">
            <el-icon class="loading-icon is-loading"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          <div v-else-if="noMore && products.length" class="no-more">
            <span>没有更多了</span>
          </div>
          <el-empty v-if="!initialLoading && !products.length" description="暂无商品" />
        </template>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Search, Loading } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import ProductCard from '@/components/common/ProductCard.vue'
import { getProducts, getBanners, getCategories, getNotices, favoriteProduct, unfavoriteProduct } from '@/api/modules/product'

const router = useRouter()

const banners = ref([])
const notices = ref([])
const categories = ref([])
const products = ref([])
const initialLoading = ref(true)
const loadingMore = ref(false)
const favorites = ref(new Set())
const page = ref(1)
const noMore = ref(false)
const pageSize = 8

const topCategories = computed(() => {
  return categories.value.slice(0, 8)
})

const getCategoryIcon = (id) => {
  const icons = ['📱', '📚', '👟', '🎸', '🏠', '📦']
  return icons[id - 1] || '📦'
}

const loadInitialData = async () => {
  initialLoading.value = true
  try {
    const [bannerRes, noticeRes, categoryRes, productRes] = await Promise.all([
      getBanners(),
      getNotices({ pinned: 1 }),
      getCategories(),
      getProducts({ page: 1, size: pageSize, status: 1 })
    ])

    banners.value = bannerRes.data || []
    notices.value = noticeRes.data || []
    categories.value = categoryRes.data || []
    products.value = productRes.data?.list || []
    page.value = 1
    noMore.value = products.value.length < pageSize
  } catch (e) {
    console.error('加载失败', e)
  } finally {
    initialLoading.value = false
  }
}

const loadMoreData = async () => {
  if (loadingMore.value || noMore.value) return
  loadingMore.value = true
  try {
    const res = await getProducts({ page: page.value + 1, size: pageSize, status: 1 })
    const newProducts = res.data?.list || []
    products.value = [...products.value, ...newProducts]
    page.value += 1
    noMore.value = newProducts.length < pageSize
  } catch (e) {
    console.error('加载更多失败', e)
  } finally {
    loadingMore.value = false
  }
}

const handleScroll = (e) => {
  const { scrollTop, scrollHeight, clientHeight } = e.target
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    loadMoreData()
  }
}

const goSearch = () => {
  router.push('/search')
}

const goCategory = (id) => {
  router.push(`/category/${id}`)
}



const handleFavorite = async (id) => {
  try {
    await favoriteProduct(id)
    favorites.value.add(id)
  } catch (e) {
    console.error('收藏失败', e)
  }
}

const handleUnfavorite = async (id) => {
  try {
    await unfavoriteProduct(id)
    favorites.value.delete(id)
  } catch (e) {
    console.error('取消收藏失败', e)
  }
}

onMounted(() => {
  loadInitialData()
  window.addEventListener('scroll', handleScroll, true)
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: var(--color-bg);
  padding-bottom: 20px;
}

.search-bar {
  background: var(--color-card);
  padding: var(--spacing-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.search-icon {
  color: var(--color-text-secondary);
  font-size: 18px;
}

.search-placeholder {
  color: var(--color-text-tertiary);
  font-size: 14px;
}

.banner-carousel {
  width: 100%;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.notice-bar {
  background: var(--color-card);
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
}

.notice-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  overflow: hidden;
}

.notice-icon {
  color: var(--color-primary);
  flex-shrink: 0;
}

.notice-text {
  flex: 1;
  overflow: hidden;
}

.notice-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
}

.notice-content-preview {
  font-size: 12px;
  color: var(--color-text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-nav {
  background: var(--color-card);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.category-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
}

.category-icon {
  font-size: 28px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary-bg);
  border-radius: 14px;
}

.category-name {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.products-section {
  padding: var(--spacing-md);
}

.section-header {
  margin-bottom: var(--spacing-md);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

.skeleton-card {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-sm);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
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
