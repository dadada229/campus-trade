<template>
  <AppLayout>
    <div class="my-products-page">
      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <span class="stat-value">{{ onSaleCount }}</span>
          <span class="stat-label">在售</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ soldCount }}</span>
          <span class="stat-label">已售</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ offShelfCount }}</span>
          <span class="stat-label">下架</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-container">
        <template v-if="loading">
          <div class="skeleton-list">
            <div v-for="i in 3" :key="i" class="skeleton-item">
              <el-skeleton :rows="2" animated />
            </div>
          </div>
        </template>

        <template v-else-if="products.length === 0">
          <div class="empty-state">
            <div class="empty-icon">
              <el-icon :size="48"><Box /></el-icon>
            </div>
            <p class="empty-text">还没有发布商品</p>
            <router-link to="/publish" class="publish-btn">
              发布商品
            </router-link>
          </div>
        </template>

        <template v-else>
          <div class="product-list">
            <div 
              v-for="product in products" 
              :key="product.id" 
              class="product-card"
            >
              <div class="product-main" @click="goToDetail(product.id)">
                <div class="product-image">
                  <img 
                    v-if="product.images && product.images.length > 0" 
                    :src="formatImageUrl(product.images[0])" 
                    :alt="product.title"
                  />
                  <el-icon v-else :size="32"><Picture /></el-icon>
                </div>
                <div class="product-info">
                  <h3 class="product-title">{{ product.title }}</h3>
                  <p class="product-price">¥{{ product.price }}</p>
                  <span :class="['product-status', `status-${product.status}`]">
                    {{ statusText(product.status) }}
                  </span>
                </div>
              </div>

              <div class="product-actions" v-if="product.status === 1">
                <button class="action-btn edit" @click.stop="handleEdit(product)">
                  <el-icon><Edit /></el-icon>
                  <span>编辑</span>
                </button>
                <button class="action-btn warning" @click.stop="handleOffShelf(product)">
                  <el-icon><RemoveFilled /></el-icon>
                  <span>下架</span>
                </button>
                <button class="action-btn success" @click.stop="handleMarkSold(product)">
                  <el-icon><Select /></el-icon>
                  <span>已售</span>
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 发布按钮 -->
      <router-link v-if="products.length > 0" to="/publish" class="fab-button">
        <el-icon :size="24"><Plus /></el-icon>
      </router-link>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Box, Edit, RemoveFilled, Select, Plus } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getUserProducts, offShelfProduct, markProductSold } from '@/api/modules/product'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()

const loading = ref(true)
const products = ref([])

const onSaleCount = computed(() => products.value.filter(p => p.status === 1).length)
const soldCount = computed(() => products.value.filter(p => p.status === 2).length)
const offShelfCount = computed(() => products.value.filter(p => p.status === 3).length)

const fetchProducts = async () => {
  try {
    loading.value = true
    const res = await getUserProducts()
    products.value = res.data?.list || res.data || []
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const statusText = (status) => {
  switch (status) {
    case 1: return '在售'
    case 2: return '已售'
    case 3: return '已下架'
    default: return '未知'
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleEdit = (product) => {
  router.push(`/publish/${product.id}`)
}

const handleOffShelf = async (product) => {
  try {
    await ElMessageBox.confirm('确定要下架该商品吗？下架后买家将无法看到此商品', '下架商品', {
      confirmButtonText: '确定下架',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await offShelfProduct(product.id)
    ElMessage.success('下架成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败:', error)
      ElMessage.error('下架失败')
    }
  }
}

const handleMarkSold = async (product) => {
  try {
    await ElMessageBox.confirm('确定要标记该商品为已售吗？', '标记已售', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await markProductSold(product.id)
    ElMessage.success('标记成功')
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败:', error)
      ElMessage.error('标记失败')
    }
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.my-products-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding-bottom: 100px;
}

.stats-row {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-card);
}

.stat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--color-bg);
  border-radius: var(--radius-lg);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-primary);
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-top: 4px;
}

.products-container {
  padding: var(--spacing-lg);
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.skeleton-item {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
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
  margin: 0 0 var(--spacing-lg) 0;
}

.publish-btn {
  padding: var(--spacing-sm) var(--spacing-xl);
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
}

.publish-btn:hover {
  background: var(--color-secondary);
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.product-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.product-main {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: background 0.2s ease;
}

.product-main:hover {
  background: var(--color-bg);
}

.product-image {
  width: 88px;
  height: 88px;
  border-radius: var(--radius-md);
  background: var(--color-bg);
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.product-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
  margin: 0;
}

.product-status {
  display: inline-block;
  width: fit-content;
  font-size: 11px;
  font-weight: 500;
  padding: 3px 8px;
  border-radius: var(--radius-full);
}

.product-status.status-1 {
  background: var(--color-success-light);
  color: var(--color-success);
}

.product-status.status-2 {
  background: var(--color-bg-secondary);
  color: var(--color-text-tertiary);
}

.product-status.status-3 {
  background: var(--color-warning-light);
  color: var(--color-warning);
}

.product-actions {
  display: flex;
  border-top: 1px solid var(--color-border);
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  border: none;
  background: transparent;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:not(:last-child) {
  border-right: 1px solid var(--color-border);
}

.action-btn.edit {
  color: var(--color-primary);
}

.action-btn.edit:hover {
  background: var(--color-primary-light);
}

.action-btn.warning {
  color: var(--color-warning);
}

.action-btn.warning:hover {
  background: var(--color-warning-light);
}

.action-btn.success {
  color: var(--color-success);
}

.action-btn.success:hover {
  background: var(--color-success-light);
}

.fab-button {
  position: fixed;
  bottom: 90px;
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(var(--color-primary-rgb), 0.4);
  text-decoration: none;
  transition: all 0.3s ease;
  z-index: 100;
}

.fab-button:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(var(--color-primary-rgb), 0.5);
}
</style>
