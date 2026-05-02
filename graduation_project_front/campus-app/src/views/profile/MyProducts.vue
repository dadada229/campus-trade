<template>
  <AppLayout>
    <div class="my-products-page">
      <template v-if="loading">
        <el-skeleton :rows="5" animated />
      </template>
      <template v-else-if="products.length === 0">
        <el-empty description="暂无发布的商品" />
      </template>
      <template v-else>
        <div class="product-list">
          <div v-for="product in products" :key="product.id" class="product-item">
            <div class="product-card" @click="goToDetail(product.id)">
              <img v-if="product.images && product.images.length > 0" 
                :src="formatImageUrl(product.images[0])" class="product-image" />
              <div v-else class="product-image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="product-info">
                <div class="product-title">{{ product.title }}</div>
                <div class="product-price">¥{{ product.price }}</div>
                <el-tag :type="statusType(product.status)" size="small">{{ statusText(product.status) }}</el-tag>
              </div>
            </div>
            <div class="product-actions">
              <el-button 
                v-if="product.status === 1" 
                type="primary" 
                size="small" 
                link
                @click.stop="handleEdit(product)"
              >
                编辑
              </el-button>
              <el-button 
                v-if="product.status === 1" 
                type="warning" 
                size="small" 
                link
                @click.stop="handleOffShelf(product)"
              >
                下架
              </el-button>
              <el-button 
                v-if="product.status === 1" 
                type="success" 
                size="small" 
                link
                @click.stop="handleMarkSold(product)"
              >
                标记已售
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getUserProducts, offShelfProduct, markProductSold } from '@/api/modules/product'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()

const loading = ref(true)
const products = ref([])

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

const statusType = (status) => {
  switch (status) {
    case 1:
      return 'success'
    case 2:
      return 'info'
    case 3:
      return 'warning'
    default:
      return 'info'
  }
}

const statusText = (status) => {
  switch (status) {
    case 1:
      return '在售'
    case 2:
      return '已售'
    case 3:
      return '已下架'
    default:
      return '未知'
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
    await ElMessageBox.confirm('确定要下架该商品吗？', '提示', {
      confirmButtonText: '确定',
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
    await ElMessageBox.confirm('确定要标记该商品为已售吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
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
  padding: var(--spacing-lg);
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.product-item {
  background: var(--color-card);
  border-radius: var(--radius-card);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-card);
}

.product-card {
  display: flex;
  gap: var(--spacing-md);
  cursor: pointer;
  margin-bottom: var(--spacing-md);
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-input);
  object-fit: cover;
}

.product-image-placeholder {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-input);
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary);
}

.product-image-placeholder .el-icon {
  font-size: 32px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-title {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  font-size: var(--font-size-h3);
  font-weight: bold;
  color: var(--color-primary);
}

.product-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--color-border);
}
</style>
