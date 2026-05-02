<template>
  <AppLayout>
    <div class="favorites-page">
      <template v-if="loading">
        <el-skeleton :rows="5" animated />
      </template>
      <template v-else-if="favorites.length === 0">
        <el-empty description="暂无收藏的商品" />
      </template>
      <template v-else>
        <div class="product-grid">
          <ProductCard 
            v-for="item in favorites" 
            :key="item.id" 
            :product="item"
            :is-favorited="true"
            @unfavorite="handleRemoveFavorite(item.id)"
          />
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppLayout from '@/components/common/AppLayout.vue'
import ProductCard from '@/components/common/ProductCard.vue'
import { getFavorites, unfavoriteProduct } from '@/api/modules/product'

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

const handleRemoveFavorite = async (productId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该商品吗？', '提示', {
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
  padding: var(--spacing-lg);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}
</style>
