<template>
  <div class="product-detail-page">
    <!-- 顶部导航 -->
    <header class="detail-header">
      <div class="header-btn back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </div>
      <span class="header-title">商品详情</span>
      <div class="header-btn share-btn">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="18" cy="5" r="3"/>
          <circle cx="6" cy="12" r="3"/>
          <circle cx="18" cy="19" r="3"/>
          <path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98"/>
        </svg>
      </div>
    </header>
    
    <div v-loading="loading" class="detail-container">
      <template v-if="product">
        <!-- 图片轮播 -->
        <div class="image-gallery">
          <el-carousel 
            v-if="product.images && product.images.length > 0" 
            height="375px" 
            :indicator-position="product.images.length > 1 ? 'outside' : 'none'"
            :arrow="product.images.length > 1 ? 'hover' : 'never'"
          >
            <el-carousel-item v-for="(img, index) in product.images" :key="index">
              <el-image 
                :src="formatImageUrl(img)" 
                fit="cover" 
                class="gallery-image"
                :preview-src-list="product.images.map(formatImageUrl)" 
                :initial-index="index" 
              />
            </el-carousel-item>
          </el-carousel>
          <div class="image-count">{{ product.images?.length || 0 }} 张图片</div>
        </div>

        <!-- 价格信息 -->
        <div class="price-section">
          <div class="price-main">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ product.price }}</span>
            <span v-if="product.originalPrice" class="price-original">¥{{ product.originalPrice }}</span>
          </div>
          <div v-if="product.status !== 1" class="status-badge" :class="{ sold: product.status === 2 }">
            {{ product.status === 2 ? '已售出' : '已下架' }}
          </div>
        </div>

        <!-- 商品标题 -->
        <div class="title-section">
          <h1 class="product-title">{{ product.title }}</h1>
          <div class="meta-tags">
            <span class="condition-tag" :class="'condition-' + product.condition">
              {{ getConditionText(product.condition) }}
            </span>
            <span class="meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              {{ product.viewCount || 0 }}
            </span>
            <span class="meta-item">{{ formatTimeAgo(product.createdAt) }}</span>
          </div>
        </div>

        <!-- 卖家信息 -->
        <div class="seller-section" @click="goToSellerProducts">
          <div class="seller-avatar">
            <img v-if="product.sellerAvatar" :src="getAvatarUrl(product.sellerAvatar)" alt="seller" />
            <span v-else class="avatar-placeholder">{{ (product.sellerName || '用户').charAt(0) }}</span>
          </div>
          <div class="seller-info">
            <div class="seller-name">{{ product.sellerName || product.sellerNickname || '校园用户' }}</div>
            <div v-if="product.sellerSchool" class="seller-school">
              <svg viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2L1 7l11 5 9-4.09V17h2V7L12 2z"/>
                <path d="M12 13L5.4 9.83v5.82A6.05 6.05 0 0012 21a6.05 6.05 0 006.6-5.35V9.83L12 13z"/>
              </svg>
              {{ product.sellerSchool }}
            </div>
          </div>
          <div class="seller-action">
            <span>查看更多</span>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="description-section">
          <div class="section-title">宝贝描述</div>
          <div class="description-content">{{ product.description }}</div>
          <div v-if="product.tradePlace" class="trade-place">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
            </svg>
            <span>{{ product.tradePlace }}</span>
          </div>
        </div>

        <!-- 推荐商品 -->
        <div v-if="relatedProducts.length > 0" class="related-section">
          <div class="section-header">
            <div class="section-title">猜你喜欢</div>
          </div>
          <div class="related-grid">
            <ProductCard 
              v-for="item in relatedProducts" 
              :key="item.id" 
              :product="item" 
              :show-favorite="false"
            />
          </div>
        </div>
      </template>
    </div>

    <!-- 底部操作栏 -->
    <div v-if="product" class="bottom-action-bar">
      <div class="action-left">
        <div class="action-item" @click="toggleFavorite">
          <div class="action-icon" :class="{ active: isFavorited }">
            <svg v-if="isFavorited" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
          </div>
          <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
        </div>
        <div class="action-item">
          <div class="action-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="18" cy="5" r="3"/>
              <circle cx="6" cy="12" r="3"/>
              <circle cx="18" cy="19" r="3"/>
              <path d="M8.59 13.51l6.83 3.98M15.41 6.51l-6.82 3.98"/>
            </svg>
          </div>
          <span>分享</span>
        </div>
      </div>
      <div class="action-right">
        <button 
          v-if="product && product.status === 1" 
          class="btn-contact" 
          @click="handleContact"
        >
          联系卖家
        </button>
        <button 
          v-if="product && product.status === 1" 
          class="btn-buy" 
          @click="handleBuy"
        >
          立即购买
        </button>
        <button v-else class="btn-disabled" disabled>
          {{ product ? (product.status === 2 ? '已售出' : '已下架') : '暂无' }}
        </button>
      </div>
    </div>

    <!-- 订单确认弹窗 -->
    <el-dialog v-model="orderDialogVisible" title="确认订单" width="90%" class="order-dialog">
      <div class="order-content">
        <!-- 商品信息 -->
        <div class="order-product">
          <el-image :src="formatImageUrl(product?.images?.[0])" fit="cover" class="product-thumb" />
          <div class="product-brief">
            <div class="product-name">{{ product?.title }}</div>
            <div class="product-price">
              <span class="symbol">¥</span>
              <span class="value">{{ product?.price }}</span>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="address-section">
          <div class="address-title">收货地址</div>
          <div v-if="addresses.length > 0" class="address-list">
            <div 
              v-for="addr in addresses" 
              :key="addr.id" 
              class="address-card" 
              :class="{ selected: selectedAddressId === addr.id }"
              @click="selectAddress(addr.id)"
            >
              <div class="address-check">
                <div class="check-circle" :class="{ checked: selectedAddressId === addr.id }"></div>
              </div>
              <div class="address-info">
                <div class="address-header">
                  <span class="receiver">{{ addr.receiverName }}</span>
                  <span class="phone">{{ addr.phone }}</span>
                  <span v-if="addr.isDefault" class="default-tag">默认</span>
                </div>
                <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
              </div>
            </div>
          </div>
          <button class="add-address-btn" @click="goToAddress">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14M5 12h14"/>
            </svg>
            添加收货地址
          </button>
        </div>

        <!-- 留言 -->
        <div class="message-section">
          <div class="message-title">买家留言（选填）</div>
          <el-input 
            v-model="buyerMessage" 
            type="textarea" 
            :rows="3" 
            placeholder="可以填写您想对卖家说的话..." 
            resize="none"
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="orderDialogVisible = false">取消</button>
          <button class="btn-confirm" :disabled="submitting" @click="submitOrder">
            {{ submitting ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
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

const getConditionText = (condition) => {
  const conditionMap = {
    1: '全新',
    2: '几乎全新',
    3: '轻微使用',
    4: '明显痕迹'
  }
  return conditionMap[condition] || '全新'
}

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

const goToSellerProducts = () => {
  ElMessage.info('查看卖家其他商品功能开发中')
}

onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background-color: var(--color-bg);
  padding-bottom: 80px;
}

/* 头部导航 */
.detail-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 100;
  box-shadow: var(--shadow-xs);
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
}

.detail-container {
  padding-top: var(--navbar-height);
}

/* 图片轮播 */
.image-gallery {
  position: relative;
  background: var(--color-card);
}

.gallery-image {
  width: 100%;
  height: 100%;
}

.image-count {
  position: absolute;
  bottom: 40px;
  right: var(--spacing-lg);
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 4px 10px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-mini);
  backdrop-filter: blur(4px);
}

/* 价格区域 */
.price-section {
  background: var(--color-card);
  padding: var(--spacing-lg) var(--padding-page);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price-main {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: var(--font-size-body);
  color: var(--color-price);
  font-weight: var(--font-weight-semibold);
}

.price-value {
  font-size: var(--font-size-price-large);
  font-weight: var(--font-weight-bold);
  color: var(--color-price);
  letter-spacing: -0.02em;
}

.price-original {
  font-size: var(--font-size-small);
  color: var(--color-text-quaternary);
  text-decoration: line-through;
  margin-left: var(--spacing-sm);
}

.status-badge {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--color-info);
  color: #fff;
  font-size: var(--font-size-small);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm);
}

.status-badge.sold {
  background: var(--color-text-tertiary);
}

/* 标题区域 */
.title-section {
  background: var(--color-card);
  padding: 0 var(--padding-page) var(--spacing-lg);
}

.product-title {
  font-size: var(--font-size-h3);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  line-height: 1.5;
  margin: 0 0 var(--spacing-md) 0;
}

.meta-tags {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.condition-tag {
  padding: 4px 10px;
  border-radius: var(--radius-xs);
  font-size: var(--font-size-mini);
  font-weight: var(--font-weight-medium);
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.condition-tag.condition-1 {
  background: rgba(78, 205, 196, 0.12);
  color: var(--color-success);
}

.condition-tag.condition-2 {
  background: var(--color-primary-bg);
  color: var(--color-primary);
}

.condition-tag.condition-3 {
  background: rgba(245, 166, 35, 0.12);
  color: var(--color-warning);
}

.condition-tag.condition-4 {
  background: rgba(139, 157, 195, 0.12);
  color: var(--color-info);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
}

.meta-item svg {
  width: 14px;
  height: 14px;
}

/* 卖家信息 */
.seller-section {
  background: var(--color-card);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-lg) var(--padding-page);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.seller-section:active {
  background: var(--color-bg);
}

.seller-avatar {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-circle);
  overflow: hidden;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.seller-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  color: #fff;
  font-size: 20px;
  font-weight: var(--font-weight-semibold);
}

.seller-info {
  flex: 1;
}

.seller-name {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.seller-school {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
}

.seller-school svg {
  width: 14px;
  height: 14px;
}

.seller-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
}

.seller-action svg {
  width: 16px;
  height: 16px;
}

/* 描述区域 */
.description-section {
  background: var(--color-card);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-lg) var(--padding-page);
}

.section-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.description-content {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  line-height: 1.7;
  white-space: pre-wrap;
}

.trade-place {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-divider);
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.trade-place svg {
  width: 18px;
  height: 18px;
  color: var(--color-primary);
}

/* 推荐商品 */
.related-section {
  background: var(--color-card);
  margin-top: var(--spacing-sm);
  padding: var(--spacing-lg) var(--padding-page);
}

.section-header {
  margin-bottom: var(--spacing-lg);
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);
}

/* 底部操作栏 */
.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--padding-page);
  box-shadow: var(--shadow-top);
  z-index: 100;
}

.action-left {
  display: flex;
  gap: var(--spacing-xl);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  cursor: pointer;
  font-size: var(--font-size-mini);
  color: var(--color-text-secondary);
}

.action-icon {
  width: 24px;
  height: 24px;
  color: var(--color-text-tertiary);
  transition: all var(--duration-fast);
}

.action-icon svg {
  width: 100%;
  height: 100%;
}

.action-icon.active {
  color: var(--color-danger);
}

.action-right {
  display: flex;
  gap: var(--spacing-md);
}

.btn-contact,
.btn-buy,
.btn-disabled {
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--radius-xl);
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--duration-fast);
  border: none;
}

.btn-contact {
  background: var(--color-card);
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
}

.btn-contact:active {
  background: var(--color-primary-bg);
}

.btn-buy {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(61, 154, 139, 0.3);
}

.btn-buy:active {
  transform: scale(0.98);
}

.btn-disabled {
  background: var(--color-border);
  color: var(--color-text-tertiary);
  cursor: not-allowed;
}

/* 订单弹窗 */
.order-content {
  padding: 0;
}

.order-product {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-bg);
  border-radius: var(--radius-card);
  margin-bottom: var(--spacing-xl);
}

.product-thumb {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

.product-brief {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.product-price .symbol {
  font-size: var(--font-size-small);
  color: var(--color-price);
}

.product-price .value {
  font-size: var(--font-size-h3);
  font-weight: var(--font-weight-bold);
  color: var(--color-price);
}

.address-section,
.message-section {
  margin-bottom: var(--spacing-xl);
}

.address-title,
.message-title {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.address-card {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-card);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.address-card.selected {
  border-color: var(--color-primary);
  background: var(--color-primary-bg);
}

.address-check {
  flex-shrink: 0;
  padding-top: 2px;
}

.check-circle {
  width: 20px;
  height: 20px;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-circle);
  transition: all var(--duration-fast);
}

.check-circle.checked {
  border-color: var(--color-primary);
  background: var(--color-primary);
  position: relative;
}

.check-circle.checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  background: #fff;
  border-radius: var(--radius-circle);
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xs);
}

.receiver {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.phone {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
}

.default-tag {
  padding: 2px 6px;
  background: var(--color-primary-bg);
  color: var(--color-primary);
  font-size: var(--font-size-mini);
  border-radius: var(--radius-xs);
}

.address-detail {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.add-address-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  width: 100%;
  padding: var(--spacing-md);
  background: none;
  border: 1.5px dashed var(--color-border);
  border-radius: var(--radius-card);
  color: var(--color-text-secondary);
  font-size: var(--font-size-body);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.add-address-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.add-address-btn svg {
  width: 18px;
  height: 18px;
}

.dialog-footer {
  display: flex;
  gap: var(--spacing-md);
}

.btn-cancel,
.btn-confirm {
  flex: 1;
  padding: var(--spacing-md);
  border-radius: var(--radius-button);
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--duration-fast);
  border: none;
}

.btn-cancel {
  background: var(--color-bg);
  color: var(--color-text-secondary);
}

.btn-confirm {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: #fff;
}

.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
