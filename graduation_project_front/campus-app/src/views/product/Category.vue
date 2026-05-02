<template>
  <AppLayout>
    <div class="category-page">
      <div class="category-header">
        <div class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </div>
        <span class="title">分类</span>
        <div style="width: 24px"></div>
      </div>
      <div class="category-content">
        <div class="category-nav">
          <div
            v-for="category in categories"
            :key="category.id"
            class="nav-item"
            :class="{ active: activeCategoryId === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>
        <div class="category-products">
          <div v-if="activeCategory && activeCategory.children?.length" class="sub-category">
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
          <ProductList
            :products="products"
            :loading="loading"
            :no-more="noMore"
            :favorites="favorites"
            @load-more="loadMore"
            @favorite="(id) => favorites.add(id)"
            @unfavorite="(id) => favorites.delete(id)"
          />
          <el-empty v-if="!loading && !products.length" description="该分类暂无商品" />
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
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
      // 加载对应分类的商品
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

// 监听路由参数变化
watch(() => route.params.id, (newId) => {
  if (newId) {
    const id = parseInt(newId)
    if (categories.value.length) {
      // 分类已经加载，直接选择
      activeCategoryId.value = id
      activeSubCategoryId.value = null
      page.value = 1
      noMore.value = false
      loadProducts(true)
    } else {
      // 分类还未加载，重新加载分类
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

.category-header {
  background: var(--color-card);
  padding: var(--spacing-md);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--color-border);
}

.back-btn {
  cursor: pointer;
  color: var(--color-text-primary);
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.category-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-nav {
  width: 90px;
  background: var(--color-bg);
  overflow-y: auto;
  flex-shrink: 0;
}

.nav-item {
  padding: var(--spacing-md);
  font-size: 14px;
  color: var(--color-text-secondary);
  text-align: center;
  cursor: pointer;
  border-bottom: 1px solid var(--color-border);
}

.nav-item.active {
  background: var(--color-card);
  color: var(--color-primary);
  font-weight: 600;
  border-left: 3px solid var(--color-primary);
}

.category-products {
  flex: 1;
  background: var(--color-card);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sub-category {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
}

.sub-item {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-bg);
  border-radius: var(--radius-button);
  font-size: 13px;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.sub-item.active {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}
</style>