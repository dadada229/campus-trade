<template>
  <AppLayout>
    <div class="category-page">
      <!-- 顶部标题 -->
      <header class="category-header">
        <div class="header-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </div>
        <h1 class="header-title">商品分类</h1>
        <div class="header-placeholder"></div>
      </header>

      <div class="category-content">
        <!-- 左侧分类导航 -->
        <div class="category-sidebar">
          <div
            v-for="category in categories"
            :key="category.id"
            class="nav-item"
            :class="{ active: activeCategoryId === category.id }"
            @click="selectCategory(category.id)"
          >
            <span class="nav-text">{{ category.name }}</span>
            <div class="nav-indicator"></div>
          </div>
        </div>

        <!-- 右侧内容区 -->
        <div class="category-main">
          <!-- 子分类 -->
          <div v-if="activeCategory && activeCategory.children?.length" class="sub-category-section">
            <div class="sub-category-grid">
              <div
                v-for="sub in activeCategory.children"
                :key="sub.id"
                class="sub-item"
                :class="{ active: activeSubCategoryId === sub.id }"
                @click="selectSubCategory(sub.id)"
              >
                {{ sub.name }}
              </div>
            </div>
          </div>

          <!-- 商品列表 -->
          <div class="products-area">
            <ProductList
              :products="products"
              :loading="loading"
              :no-more="noMore"
              :favorites="favorites"
              @load-more="loadMore"
              @favorite="(id) => favorites.add(id)"
              @unfavorite="(id) => favorites.delete(id)"
            />
            
            <!-- 空状态 -->
            <div v-if="!loading && !products.length" class="empty-state">
              <div class="empty-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                </svg>
              </div>
              <p class="empty-text">该分类暂无商品</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppLayout from '@/components/common/AppLayout.vue'
import ProductList from '@/components/business/ProductList.vue'
import { getProducts, getCategories } from '@/api/modules/product'

const router = useRouter()
const route = useRoute()

const categories = ref([])
const activeCategoryId = ref(null)
const activeSubCategoryId = ref(null)
const products = ref([])
const loading = ref(false)
const page = ref(1)
const noMore = ref(false)
const favorites = ref(new Set())
const pageSize = 10

const activeCategory = computed(() => {
  return categories.value.find(c => c.id === activeCategoryId.value)
})

const goBack = () => {
  router.back()
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
    if (categories.value.length) {
      const initialId = route.params.id ? parseInt(route.params.id) : categories.value[0].id
      activeCategoryId.value = initialId
      loadProducts(true)
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

const selectCategory = (id) => {
  activeCategoryId.value = id
  activeSubCategoryId.value = null
  page.value = 1
  noMore.value = false
  loadProducts(true)
}

const selectSubCategory = (id) => {
  activeSubCategoryId.value = id
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
      status: 1
    }
    if (activeSubCategoryId.value) {
      params.categoryId = activeSubCategoryId.value
    } else if (activeCategoryId.value) {
      params.categoryId = activeCategoryId.value
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
    console.error('加载商品失败', e)
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
  loadCategories()
})

watch(() => route.params.id, (newId) => {
  if (newId) {
    const id = parseInt(newId)
    if (categories.value.length) {
      activeCategoryId.value = id
      activeSubCategoryId.value = null
      page.value = 1
      noMore.value = false
      loadProducts(true)
    } else {
      loadCategories()
    }
  }
})
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background-color: var(--color-bg);
  display: flex;
  flex-direction: column;
}

/* 顶部标题 */
.category-header {
  height: var(--navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: var(--shadow-xs);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-circle);
  cursor: pointer;
  transition: all var(--duration-fast);
  margin-left: -8px;
}

.header-btn:hover {
  background: var(--color-bg);
}

.header-btn:active {
  transform: scale(0.92);
}

.header-btn svg {
  width: 22px;
  height: 22px;
  color: var(--color-text-primary);
}

.header-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}

.header-placeholder {
  width: 36px;
}

/* 内容区域 */
.category-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧导航 */
.category-sidebar {
  width: 88px;
  background: var(--color-card);
  overflow-y: auto;
  flex-shrink: 0;
  border-right: 1px solid var(--color-divider);
}

.nav-item {
  position: relative;
  padding: var(--spacing-lg) var(--spacing-sm);
  text-align: center;
  cursor: pointer;
  transition: all var(--duration-fast);
}

.nav-item:active {
  background: var(--color-border-light);
}

.nav-text {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  transition: all var(--duration-fast);
}

.nav-item.active {
  background: var(--color-bg);
}

.nav-item.active .nav-text {
  color: var(--color-primary);
  font-weight: var(--font-weight-semibold);
}

.nav-indicator {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: var(--color-primary);
  border-radius: 0 2px 2px 0;
  transition: all var(--duration-fast);
}

.nav-item.active .nav-indicator {
  height: 20px;
}

/* 右侧主内容 */
.category-main {
  flex: 1;
  background: var(--color-bg);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

/* 子分类 */
.sub-category-section {
  background: var(--color-card);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
}

.sub-category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.sub-item {
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--color-bg);
  border-radius: var(--radius-xl);
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.sub-item:active {
  transform: scale(0.97);
}

.sub-item.active {
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

/* 商品区域 */
.products-area {
  flex: 1;
  padding: var(--spacing-md);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xxxl) var(--spacing-xl);
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: var(--color-text-quaternary);
  margin-bottom: var(--spacing-md);
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: var(--font-size-body);
  color: var(--color-text-tertiary);
  margin: 0;
}
</style>
