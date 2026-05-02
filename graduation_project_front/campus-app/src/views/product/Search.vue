<template>
  <AppLayout>
    <div class="search-page">
      <!-- 搜索头部 -->
      <div class="search-header">
        <div class="search-input-wrapper">
          <div class="search-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35"/>
            </svg>
          </div>
          <el-input
            v-model="keyword"
            placeholder="搜索你想要的宝贝"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>
        <span class="cancel-btn" @click="goBack">取消</span>
      </div>

      <!-- 搜索前内容 -->
      <div v-if="!hasSearched" class="search-before">
        <!-- 搜索历史 -->
        <div v-if="historyList.length" class="history-section">
          <div class="section-header">
            <span class="section-title">搜索历史</span>
            <button class="clear-btn" @click="clearHistory">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
              </svg>
            </button>
          </div>
          <div class="tag-list">
            <div
              v-for="(item, index) in historyList"
              :key="index"
              class="tag-item"
              @click="searchHistory(item)"
            >
              {{ item }}
            </div>
          </div>
        </div>

        <!-- 热门搜索 -->
        <div v-if="hotList.length" class="hot-section">
          <div class="section-header">
            <span class="section-title">热门搜索</span>
          </div>
          <div class="tag-list">
            <div
              v-for="(item, index) in hotList"
              :key="index"
              class="tag-item hot"
              @click="searchHistory(item)"
            >
              <span v-if="index < 3" class="hot-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
              {{ item }}
            </div>
          </div>
        </div>
      </div>

      <!-- 搜索结果 -->
      <div v-else class="search-result">
        <!-- 排序栏 -->
        <div v-if="products.length || loading" class="sort-bar">
          <div
            class="sort-item"
            :class="{ active: sortType === 'default' }"
            @click="setSortType('default')"
          >
            <span>综合</span>
          </div>
          <div
            class="sort-item"
            :class="{ active: sortType === 'time' }"
            @click="setSortType('time')"
          >
            <span>最新</span>
          </div>
          <div
            class="sort-item price"
            :class="{ active: sortType.startsWith('price') }"
            @click="togglePriceSort"
          >
            <span>价格</span>
            <div class="price-arrows">
              <svg viewBox="0 0 24 24" fill="currentColor" :class="{ active: sortType === 'price_asc' }">
                <path d="M7 14l5-5 5 5z"/>
              </svg>
              <svg viewBox="0 0 24 24" fill="currentColor" :class="{ active: sortType === 'price_desc' }">
                <path d="M7 10l5 5 5-5z"/>
              </svg>
            </div>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-area">
          <ProductList
            :products="products"
            :loading="loading"
            :no-more="noMore"
            :favorites="favorites"
            @load-more="loadMore"
            @favorite="(id) => favorites.add(id)"
            @unfavorite="(id) => favorites.delete(id)"
          />
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && !products.length" class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="11" cy="11" r="8"/>
              <path d="M21 21l-4.35-4.35"/>
            </svg>
          </div>
          <p class="empty-text">未找到相关商品</p>
          <p class="empty-hint">换个关键词试试吧</p>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/common/AppLayout.vue'
import ProductList from '@/components/business/ProductList.vue'
import { getProducts } from '@/api/modules/product'

const router = useRouter()

const keyword = ref('')
const historyList = ref([])
const hotList = ref(['笔记本', '手机', '耳机', '考研资料', '球鞋', '自行车'])
const products = ref([])
const loading = ref(false)
const hasSearched = ref(false)
const sortType = ref('default')
const page = ref(1)
const noMore = ref(false)
const favorites = ref(new Set())
const pageSize = 10

const goBack = () => {
  router.back()
}

const loadHistory = () => {
  const stored = localStorage.getItem('searchHistory')
  if (stored) {
    historyList.value = JSON.parse(stored)
  }
}

const saveHistory = (kw) => {
  const index = historyList.value.indexOf(kw)
  if (index > -1) {
    historyList.value.splice(index, 1)
  }
  historyList.value.unshift(kw)
  historyList.value = historyList.value.slice(0, 10)
  localStorage.setItem('searchHistory', JSON.stringify(historyList.value))
}

const clearHistory = () => {
  historyList.value = []
  localStorage.removeItem('searchHistory')
}

const searchHistory = (kw) => {
  keyword.value = kw
  handleSearch()
}

const handleSearch = async () => {
  if (!keyword.value.trim()) return
  saveHistory(keyword.value.trim())
  hasSearched.value = true
  page.value = 1
  noMore.value = false
  await loadProducts(true)
}

const setSortType = (type) => {
  sortType.value = type
  page.value = 1
  noMore.value = false
  loadProducts(true)
}

const togglePriceSort = () => {
  if (sortType.value === 'price_asc') {
    sortType.value = 'price_desc'
  } else {
    sortType.value = 'price_asc'
  }
  page.value = 1
  noMore.value = false
  loadProducts(true)
}

const loadProducts = async (reset = false) => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: pageSize,
      status: 1,
      keyword: keyword.value
    }
    if (sortType.value !== 'default') {
      params.sort = sortType.value
    }
    const res = await getProducts(params)
    const newProducts = res.data?.list || []
    if (reset) {
      products.value = newProducts
    } else {
      products.value = [...products.value, ...newProducts]
    }
    noMore.value = newProducts.length < pageSize
  } catch (e) {
    console.error('搜索失败', e)
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  if (loading.value || noMore.value) return
  page.value += 1
  await loadProducts(false)
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: var(--color-bg);
}

/* 搜索头部 */
.search-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--padding-page);
  background: var(--color-card);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-xs);
}

.search-input-wrapper {
  flex: 1;
  position: relative;
}

.search-icon {
  position: absolute;
  left: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--color-text-tertiary);
  z-index: 1;
  pointer-events: none;
}

.search-icon svg {
  width: 100%;
  height: 100%;
}

.search-input-wrapper :deep(.el-input__wrapper) {
  padding-left: 40px !important;
  border-radius: var(--radius-xl) !important;
  background: var(--color-bg) !important;
}

.cancel-btn {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
  flex-shrink: 0;
}

.cancel-btn:hover {
  color: var(--color-primary);
}

/* 搜索前内容 */
.search-before {
  padding: var(--padding-page);
}

.history-section,
.hot-section {
  margin-bottom: var(--spacing-xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.section-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.clear-btn {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--color-text-tertiary);
  transition: all var(--duration-fast);
}

.clear-btn:hover {
  color: var(--color-danger);
}

.clear-btn svg {
  width: 18px;
  height: 18px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.tag-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--color-card);
  border-radius: var(--radius-xl);
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
  box-shadow: var(--shadow-xs);
}

.tag-item:hover {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.tag-item:active {
  transform: scale(0.97);
}

.hot-rank {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-xs);
  font-size: 10px;
  font-weight: var(--font-weight-semibold);
  color: #fff;
  background: var(--color-text-tertiary);
}

.hot-rank.rank-1 {
  background: var(--color-price);
}

.hot-rank.rank-2 {
  background: var(--color-warning);
}

.hot-rank.rank-3 {
  background: var(--color-primary);
}

/* 搜索结果 */
.search-result {
  display: flex;
  flex-direction: column;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) var(--padding-page);
  background: var(--color-card);
  gap: var(--spacing-xl);
  border-bottom: 1px solid var(--color-divider);
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.sort-item.active {
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.sort-item.price {
  display: flex;
  align-items: center;
}

.price-arrows {
  display: flex;
  flex-direction: column;
  margin-left: 2px;
}

.price-arrows svg {
  width: 12px;
  height: 8px;
  color: var(--color-text-tertiary);
}

.price-arrows svg.active {
  color: var(--color-primary);
}

/* 商品区域 */
.product-area {
  padding: var(--spacing-md) var(--padding-page);
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
