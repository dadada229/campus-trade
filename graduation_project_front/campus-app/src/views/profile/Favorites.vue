<template>
  <AppLayout>
    <div class="favorites-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <h2 class="page-title">我的收藏</h2>
        <span class="count">{{ favorites.length }} 件商品</span>
      </div>

      <!-- 收藏列表 -->
      <div class="favorites-container">
        <template v-if="loading">
          <div class="skeleton-grid">
            <div v-for="i in 4" :key="i" class="skeleton-item">
              <el-skeleton :rows="3" animated />
            </div>
          </div>
        </template>

        <template v-else-if="favorites.length === 0">
          <div class="empty-state">
            <div class="empty-icon">
              <el-icon :size="48"><Star /></el-icon>
            </div>
            <p class="empty-text">还没有收藏商品</p>
            <p class="empty-hint">去发现更多好物吧</p>
            <router-link to="/" class="browse-btn">
              去逛逛
            </router-link>
          </div>
        </template>

        <template v-else>
          <div class="product-grid">
            <div 
              v-for="item in favorites" 
              :key="item.id" 
              class="product-card"
              @click="goToDetail(item.id)"
            >
              <div class="product-image">
                <img 
                  v-if="item.images && item.images.length > 0" 
                  :src="formatImageUrl(item.images[0])" 
                  :alt="item.title"
                />
                <el-icon v-else :size="32"><Picture /></el-icon>
                <button 
                  class="unfavorite-btn"
                  @click.stop="handleRemoveFavorite(item.id)"
                >
                  <el-icon><StarFilled /></el-icon>
                </button>
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ item.title }}</h3>
                <div class="product-footer">
                  <span class="product-price">¥{{ item.price }}</span>
                  <span v-if="item.originalPrice" class="original-price">
                    ¥{{ item.originalPrice }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, Picture } from '@element-plus/icons-vue'
import AppLayout from '@/components/common/AppLayout.vue'
import { getFavorites, unfavoriteProduct } from '@/api/modules/product'
import { formatImageUrl } from '@/utils/image'

const router = useRouter()

const loading = ref(true)
const favorites = ref([])

const fetchFavorites = async () => {
  try {
    loading.value = true
    const res = await getFavorites()
    favorites.value = res.data?.list || res.data || []
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleRemoveFavorite = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该商品吗？', '取消收藏', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unfavoriteProduct(productId)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
  min-height: 100vh;
  background: var(--color-bg);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background: var(--color-card);
  border-bottom: 1px solid var(--color-border);
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
}

.count {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.favorites-container {
  padding: var(--spacing-lg);
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

.skeleton-item {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
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
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-secondary-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary);
  margin-bottom: var(--spacing-lg);
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-xs) 0;
}

.empty-hint {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-lg) 0;
}

.browse-btn {
  padding: var(--spacing-sm) var(--spacing-xl);
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s ease;
}

.browse-btn:hover {
  background: var(--color-secondary);
  transform: translateY(-2px);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

.product-card {
  background: var(--color-card);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  background: var(--color-bg);
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

.unfavorite-btn {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow-sm);
}

.unfavorite-btn:hover {
  background: var(--color-primary);
  color: white;
  transform: scale(1.1);
}

.product-info {
  padding: var(--spacing-md);
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
  margin: 0 0 var(--spacing-sm) 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-footer {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
}

.original-price {
  font-size: 12px;
  color: var(--color-text-tertiary);
  text-decoration: line-through;
}
</style>
