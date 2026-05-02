<template>
  <AppLayout>
    <div class="search-page">
      <div class="search-header">
        <div class="search-input-wrapper">
          <el-input
            v-model="keyword"
            placeholder="搜索商品"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <span class="cancel-btn" @click="goBack">取消</span>
      </div>

      <div v-if="!hasSearched" class="history-section">
        <div v-if="historyList.length" class="history-header">
          <span class="history-title">搜索历史</span>
          <el-icon class="clear-icon" @click="clearHistory"><Delete /></el-icon>
        </div>
        <div v-if="historyList.length" class="history-list">
          <div
            v-for="(item, index) in historyList"
            :key="index"
            class="history-item"
            @click="searchHistory(item)"
          >
            {{ item }}
          </div>
        </div>
        <div class="hot-section" v-if="hotList.length">
          <div class="hot-header">
            <span class="hot-title">热门搜索</span>
          </div>
          <div class="hot-list">
            <div
              v-for="(item, index) in hotList"
              :key="index"
              class="hot-item"
              @click="searchHistory(item)"
            >
              {{ item }}
            </div>
          </div>
        </div>
      </div>

      <div v-else class="search-result">
        <div v-if="products.length" class="sort-bar">
          <span
            class="sort-item"
            :class="{ active: sortType === 'default' }"
            @click="setSortType('default')"
          >综合</span>
          <span
            class="sort-item"
            :class="{ active: sortType === 'price_asc' }"
            @click="setSortType('price_asc')"
          >价格↑</span>
          <span
            class="sort-item"
            :class="{ active: sortType === 'price_desc' }"
            @click="setSortType('price_desc')"
          >价格↓</span>
        </div>
        <ProductList
          :products="products"
          :loading="loading"
          :no-more="noMore"
          :favorites="favorites"
          @load-more="loadMore"
          @favorite="(id) => favorites.add(id)"
          @unfavorite="(id) => favorites.delete(id)"
        />
        <el-empty v-if="!loading && !products.length" description="未找到相关商品" />
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Delete } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import ProductList from '@/components/business/ProductList.vue'
import { getProducts } from '@/api/modules/product'

const router = useRouter()

const keyword = ref('')
const historyList = ref([])
const hotList = ref(['笔记本', '手机', '耳机', '考研资料', '球鞋'])
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

.search-header {
  background: var(--color-card);
  padding: var(--spacing-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: sticky;
  top: 0;
  z-index: 10;
}

.search-input-wrapper {
  flex: 1;
}

.cancel-btn {
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.history-section {
  padding: var(--spacing-md);
}

.history-header,
.hot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.history-title,
.hot-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.clear-icon {
  color: var(--color-text-tertiary);
  cursor: pointer;
}

.history-list,
.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.history-item,
.hot-item {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-card);
  border-radius: var(--radius-button);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.hot-section {
  margin-top: var(--spacing-lg);
}

.search-result {
  flex: 1;
}

.sort-bar {
  background: var(--color-card);
  padding: var(--spacing-sm) var(--spacing-md);
  display: flex;
  gap: var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
}

.sort-item {
  font-size: 14px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-xs) 0;
}

.sort-item.active {
  color: var(--color-primary);
  font-weight: 600;
}
</style>