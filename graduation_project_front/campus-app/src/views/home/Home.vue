<template>
  <AppLayout>
    <div class="home-page">
      <!-- 顶部搜索区域 -->
      <div class="search-header">
        <div class="search-bar" @click="goSearch">
          <div class="search-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35"/>
            </svg>
          </div>
          <span class="search-placeholder">搜索你想要的宝贝</span>
        </div>
      </div>

      <!-- 轮播图 -->
      <div v-if="banners.length" class="banner-section">
        <el-carousel class="banner-carousel" height="160px" indicator-position="none" :interval="4000" arrow="never">
          <el-carousel-item v-for="banner in banners" :key="banner.id">
            <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }"></div>
          </el-carousel-item>
        </el-carousel>
        <div class="carousel-indicators">
          <span v-for="(banner, index) in banners" :key="banner.id" class="indicator-dot"></span>
        </div>
      </div>

      <!-- 公告区域 -->
      <div v-if="notices.length" class="notice-section">
        <div class="notice-card">
          <div class="notice-icon">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
            </svg>
          </div>
          <div class="notice-content">
            <span class="notice-label">公告</span>
            <span class="notice-text">{{ notices[0]?.title || notices[0]?.content }}</span>
          </div>
          <div class="notice-arrow">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </div>
        </div>
      </div>

      <!-- 分类导航 -->
      <div class="category-section">
        <div class="category-grid">
          <div 
            class="category-item" 
            v-for="category in topCategories" 
            :key="category.id" 
            @click="goCategory(category.id)"
          >
            <div class="category-icon-wrapper">
              <span class="category-icon">{{ getCategoryIcon(category.id) }}</span>
            </div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
      </div>

      <!-- 商品列表区域 -->
      <div class="products-section">
        <div class="section-header">
          <div class="section-title-wrapper">
            <h2 class="section-title">最新发布</h2>
            <span class="section-subtitle">校园好物 等你来淘</span>
          </div>
        </div>

        <!-- 骨架屏 -->
        <div v-if="initialLoading" class="skeleton-grid">
          <div v-for="i in 4" :key="i" class="skeleton-card">
            <div class="skeleton-image skeleton"></div>
            <div class="skeleton-content">
              <div class="skeleton-title skeleton"></div>
              <div class="skeleton-price skeleton"></div>
              <div class="skeleton-footer skeleton"></div>
            </div>
          </div>
        </div>

        <!-- 商品网格 -->
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

          <!-- 加载更多 -->
          <div v-if="loadingMore" class="loading-more">
            <div class="loading-spinner"></div>
            <span>加载中...</span>
          </div>
          
          <div v-else-if="noMore && products.length" class="no-more">
            <div class="no-more-line"></div>
            <span>已经到底了</span>
            <div class="no-more-line"></div>
          </div>

          <!-- 空状态 -->
          <div v-if="!initialLoading && !products.length" class="empty-state">
            <div class="empty-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
              </svg>
            </div>
            <p class="empty-text">暂无商品</p>
            <p class="empty-hint">快来发布第一件宝贝吧</p>
          </div>
        </template>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
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
  const icons = {
    1: '📱',
    2: '📚',
    3: '👟',
    4: '🎸',
    5: '🏠',
    6: '📦',
    7: '💄',
    8: '🎮'
  }
  return icons[id] || '📦'
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
  if (scrollTop + clientHeight >= scrollHeight - 150) {
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

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll, true)
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: var(--color-bg);
  padding-bottom: calc(var(--tabbar-height) + var(--safe-area-bottom) + var(--spacing-xl));
}

/* 搜索头部 */
.search-header {
  background: var(--color-card);
  padding: var(--spacing-md) var(--padding-page);
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: var(--shadow-xs);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background: var(--color-bg);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-xl);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
}

.search-bar:hover {
  background: var(--color-border-light);
}

.search-bar:active {
  transform: scale(0.99);
}

.search-icon {
  width: 20px;
  height: 20px;
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.search-icon svg {
  width: 100%;
  height: 100%;
}

.search-placeholder {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-body);
}

/* 轮播图 */
.banner-section {
  padding: var(--spacing-md) var(--padding-page);
  position: relative;
}

.banner-carousel {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-color: var(--color-border-light);
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: var(--spacing-sm);
}

.indicator-dot {
  width: 6px;
  height: 6px;
  border-radius: 3px;
  background: var(--color-border);
  transition: all var(--duration-fast);
}

.indicator-dot:first-child {
  width: 16px;
  background: var(--color-primary);
}

/* 公告区域 */
.notice-section {
  padding: 0 var(--padding-page);
  margin-bottom: var(--spacing-sm);
}

.notice-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  background: linear-gradient(135deg, var(--color-primary-bg) 0%, rgba(61, 154, 139, 0.04) 100%);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-card);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.notice-card:hover {
  background: var(--color-primary-soft);
}

.notice-icon {
  width: 20px;
  height: 20px;
  color: var(--color-primary);
  flex-shrink: 0;
}

.notice-icon svg {
  width: 100%;
  height: 100%;
}

.notice-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  min-width: 0;
}

.notice-label {
  font-size: var(--font-size-mini);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
  padding: 2px 6px;
  background: var(--color-primary-bg);
  border-radius: var(--radius-xs);
  flex-shrink: 0;
}

.notice-text {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-arrow {
  width: 16px;
  height: 16px;
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.notice-arrow svg {
  width: 100%;
  height: 100%;
}

/* 分类导航 */
.category-section {
  background: var(--color-card);
  padding: var(--spacing-lg) var(--padding-page);
  margin: var(--spacing-sm) 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg) var(--spacing-sm);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.category-item:active {
  transform: scale(0.95);
}

.category-icon-wrapper {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  border-radius: var(--radius-lg);
  transition: all var(--duration-fast);
}

.category-item:hover .category-icon-wrapper {
  background: var(--color-primary-bg);
  transform: translateY(-2px);
}

.category-icon {
  font-size: 26px;
}

.category-name {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
}

/* 商品区域 */
.products-section {
  padding: var(--spacing-md) var(--padding-page);
}

.section-header {
  margin-bottom: var(--spacing-lg);
}

.section-title-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.section-title {
  font-size: var(--font-size-h3);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
  letter-spacing: -0.02em;
}

.section-subtitle {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
}

/* 骨架屏 */
.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

.skeleton-card {
  background: var(--color-card);
  border-radius: var(--radius-card);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.skeleton-image {
  width: 100%;
  padding-bottom: 100%;
}

.skeleton-content {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.skeleton-title {
  height: 18px;
  width: 80%;
}

.skeleton-price {
  height: 24px;
  width: 50%;
}

.skeleton-footer {
  height: 16px;
  width: 70%;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

@media (min-width: 540px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 加载更多 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  color: var(--color-text-tertiary);
  font-size: var(--font-size-small);
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xl);
  color: var(--color-text-quaternary);
  font-size: var(--font-size-small);
}

.no-more-line {
  width: 40px;
  height: 1px;
  background: var(--color-border);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xxxl) var(--spacing-xl);
}

.empty-icon {
  width: 80px;
  height: 80px;
  color: var(--color-text-quaternary);
  margin-bottom: var(--spacing-lg);
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-xs) 0;
}

.empty-hint {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  margin: 0;
}
</style>
