<template>
  <div class="product-detail-page">
    <TopNavBar :show-back="true" :show-center="true" center-text="商品详情" />
    
    <div v-loading="loading" class="detail-container">
      <template v-if="product">
        <el-carousel v-if="product.images && product.images.length > 0" height="375px" indicator-position="outside">
          <el-carousel-item v-for="(img, index) in product.images" :key="index">
            <el-image :src="formatImageUrl(img)" fit="cover" style="width: 100%; height: 100%;" :preview-src-list="product.images.map(formatImageUrl)" :initial-index="index" />
          </el-carousel-item>
        </el-carousel>

        <div class="product-info">
          <div class="price-row">
            <span class="price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            <el-tag v-if="product.status === 2" type="info" size="small">已售出</el-tag>
            <el-tag v-else-if="product.status === 3" type="info" size="small">已下架</el-tag>
          </div>
          
          <h1 class="title">{{ product.title }}</h1>
          
          <div class="meta-row">
            <el-tag :type="getConditionType(product.condition)" size="small">{{ product.conditionText || '全新' }}</el-tag>
            <span class="view-count">{{ product.viewCount || 0 }}次浏览</span>
            <span class="publish-time">{{ formatTimeAgo(product.createdAt) }}</span>
          </div>
        </div>

        <div class="seller-card" @click="goToSellerProducts">
          <el-avatar :src="getAvatarUrl(product.sellerAvatar)" :size="48">
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="seller-info">
            <div class="seller-name">{{ product.sellerName || product.sellerNickname || '用户' }}</div>
            <div v-if="product.sellerSchool" class="seller-school">{{ product.sellerSchool }}</div>
          </div>
          <el-icon class="arrow-right"><ArrowRight /></el-icon>
        </div>

        <div class="description-card">
          <div class="card-title">宝贝描述</div>
          <div class="description-text">{{ product.description }}</div>
          <div v-if="product.tradePlace" class="trade-place">
            <el-icon><Location /></el-icon>
            <span>交易地点：{{ product.tradePlace }}</span>
          </div>
        </div>

        <div v-if="relatedProducts.length > 0" class="related-section">
          <div class="section-title">猜你喜欢</div>
          <div class="related-list">
            <ProductCard v-for="item in relatedProducts" :key="item.id" :product="item" @click="goToDetail(item.id)" />
          </div>
        </div>
      </template>
    </div>

    <div v-if="product" class="bottom-bar">
      <div class="bar-left">
        <div class="bar-icon" @click="toggleFavorite">
          <el-icon :class="{ favorited: isFavorited }">
            <StarFilled v-if="isFavorited" />
            <Star v-else />
          </el-icon>
          <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
        </div>
        <div class="bar-icon">
          <el-icon><Share /></el-icon>
          <span>分享</span>
        </div>
      </div>
      <div class="bar-right">
        <el-button v-if="product && product.status === 1" class="contact-btn" @click="handleContact">联系卖家</el-button>
        <el-button v-if="product && product.status === 1" type="primary" class="buy-btn" @click="handleBuy">立即购买</el-button>
        <el-button v-else disabled type="info" class="buy-btn">{{ product ? (product.status === 2 ? '已售出' : '已下架') : '暂无' }}</el-button>
      </div>
    </div>

    <el-dialog v-model="orderDialogVisible" title="确认订单" width="90%">
      <div class="order-dialog">
        <div class="order-product">
          <el-image :src="formatImageUrl(product?.images?.[0])" fit="cover" style="width: 80px; height: 80px;" />
          <div class="product-brief">
            <div class="product-title">{{ product?.title }}</div>
            <div class="product-price">¥{{ product?.price }}</div>
          </div>
        </div>

        <div class="address-section">
          <div class="section-title">收货地址</div>
          <div v-if="addresses.length > 0" class="address-list">
            <div v-for="addr in addresses" :key="addr.id" 
                 class="address-item" 
                 :class="{ selected: selectedAddressId === addr.id }"
                 @click="selectAddress(addr.id)">
              <div class="address-info">
                <span class="receiver">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.phone }}</span>
              </div>
              <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
              <el-tag v-if="addr.isDefault" type="primary" size="small" style="margin-top: 8px;">默认</el-tag>
            </div>
          </div>
          <el-button type="text" @click="goToAddress">+ 添加收货地址</el-button>
        </div>

        <div class="message-section">
          <div class="section-title">买家留言（选填）</div>
          <el-input v-model="buyerMessage" type="textarea" :rows="3" placeholder="可以填写您想对卖家说的话..." />
        </div>
      </div>
      <template #footer>
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, Share, ArrowRight, Location, User } from '@element-plus/icons-vue'
import TopNavBar from '@/components/common/TopNavBar.vue'
import ProductCard from '@/components/common/ProductCard.vue'
import { getProductDetail, getProducts, favoriteProduct, unfavoriteProduct } from '@/api/modules/product'
import { getUserAddresses, createOrder } from '@/api/modules/order'
import { createOrGetConversation } from '@/api/modules/chat'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'
import { formatTimeAgo } from '@/utils/format'
import { formatImageUrl, getAvatarUrl } from '@/utils/image'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const loading = ref(false)
const product = ref(null)
const isFavorited = ref(false)
const relatedProducts = ref([])
const orderDialogVisible = ref(false)
const addresses = ref([])
const selectedAddressId = ref(null)
const buyerMessage = ref('')
const submitting = ref(false)

const loadProductDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    if (res && res.code === 200) {
      product.value = res.data
      isFavorited.value = res.data.isFavorited === true
      if (res.data.categoryId) {
        loadRelatedProducts(res.data.categoryId, res.data.id)
      }
    } else {
      ElMessage.error(res?.message || '加载商品详情失败')
      router.push('/home')
    }
  } catch (error) {
    console.error('加载商品详情失败', error)
    ElMessage.error('加载商品详情失败')
    router.push('/home')
  } finally {
    loading.value = false
  }
}

const loadRelatedProducts = async (categoryId, excludeId) => {
  try {
    const res = await getProducts({ categoryId, excludeId, size: 4, status: 1 })
    if (res && res.code === 200 && res.data) {
      relatedProducts.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载推荐商品失败', error)
  }
}

const loadAddresses = async () => {
  try {
    const res = await getUserAddresses()
    if (res && res.code === 200) {
      addresses.value = res.data || []
      const defaultAddr = addresses.value.find(a => a.isDefault)
      if (defaultAddr) {
        selectedAddressId.value = defaultAddr.id
      } else if (addresses.value.length > 0) {
        selectedAddressId.value = addresses.value[0].id
      }
    }
  } catch (error) {
    console.error('加载地址失败', error)
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('您需要登录后才能收藏商品，是否立即登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      router.push('/login')
    }).catch(() => {})
    return
  }

  try {
    if (isFavorited.value) {
      await unfavoriteProduct(product.value.id)
      isFavorited.value = false
      ElMessage.success('取消收藏成功')
    } else {
      await favoriteProduct(product.value.id)
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleContact = async () => {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('您需要登录后才能联系卖家，是否立即登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      router.push('/login')
    }).catch(() => {})
    return
  }
  try {
    const res = await createOrGetConversation(product.value.id, product.value.sellerId)
    if (res && res.code === 200) {
      chatStore.setCurrentConversation(res.data)
      router.push(`/chat/${res.data.id}`)
    }
  } catch (error) {
    console.error('联系卖家失败', error)
    ElMessage.error('联系卖家失败')
  }
}

const handleBuy = async () => {
  if (!userStore.isLoggedIn) {
    ElMessageBox.confirm('您需要登录后才能购买商品，是否立即登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      router.push('/login')
    }).catch(() => {})
    return
  }
  await loadAddresses()
  orderDialogVisible.value = true
}

const selectAddress = (id) => {
  selectedAddressId.value = id
}

const goToAddress = () => {
  orderDialogVisible.value = false
  router.push('/address')
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder({
      productId: product.value.id,
      addressId: selectedAddressId.value,
      message: buyerMessage.value
    })
    if (res && res.code === 200) {
      ElMessage.success('下单成功')
      orderDialogVisible.value = false
      product.value.status = 2
      setTimeout(() => {
        router.push('/my-orders')
      }, 500)
    } else {
      const errorMsg = res?.message || '下单失败'
      console.error('下单失败:', errorMsg, res)
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('下单异常:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '下单失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    submitting.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const goToSellerProducts = () => {
  ElMessage.info('查看卖家其他商品功能开发中')
}

const getConditionType = (condition) => {
  const types = { 1: 'success', 2: 'primary', 3: 'warning', 4: 'info' }
  return types[condition] || 'info'
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background-color: var(--color-bg);
  padding-bottom: 70px;
}

.detail-container {
  padding-bottom: 20px;
}

.product-info {
  background: var(--color-card);
  padding: 16px;
  margin-bottom: 12px;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.price {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-primary);
}

.original-price {
  font-size: 14px;
  color: var(--color-text-tertiary);
  text-decoration: line-through;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-secondary);
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.view-count,
.publish-time {
  font-size: 13px;
  color: var(--color-text-tertiary);
}

.seller-card {
  background: var(--color-card);
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.seller-info {
  flex: 1;
}

.seller-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-secondary);
}

.seller-school {
  font-size: 13px;
  color: var(--color-text-tertiary);
  margin-top: 4px;
}

.arrow-right {
  color: var(--color-text-tertiary);
}

.description-card {
  background: var(--color-card);
  padding: 16px;
  margin-bottom: 12px;
}

.card-title,
.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-secondary);
  margin-bottom: 12px;
}

.description-text {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.trade-place {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
  font-size: 13px;
  color: var(--color-text-secondary);
}

.related-section {
  background: var(--color-card);
  padding: 16px;
}

.related-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: var(--color-card);
  display: flex;
  align-items: center;
  padding: 0 16px;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.bar-left {
  display: flex;
  gap: 24px;
}

.bar-icon {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: 12px;
}

.bar-icon .el-icon {
  font-size: 22px;
}

.bar-icon .favorited {
  color: var(--color-primary);
}

.bar-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.contact-btn {
  border: 1px solid var(--color-primary);
  color: var(--color-primary);
}

.buy-btn {
  background: var(--color-primary);
}

.order-dialog {
  padding: 8px 0;
}

.order-product {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--color-bg);
  border-radius: 8px;
  margin-bottom: 20px;
}

.product-brief {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-brief .product-title {
  font-size: 14px;
  color: var(--color-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-brief .product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
}

.address-section {
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.address-item.selected {
  border-color: var(--color-primary);
  background: rgba(255, 107, 61, 0.05);
}

.address-item:hover {
  border-color: var(--color-primary-light);
}

.address-info {
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
}

.receiver,
.phone {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-secondary);
}

.address-detail {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.message-section {
  margin-bottom: 10px;
}
</style>
