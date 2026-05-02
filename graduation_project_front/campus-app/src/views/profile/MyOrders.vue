<template>
  <AppLayout>
    <div class="my-orders-page">
      <el-tabs v-model="roleTab" @tab-change="handleRoleTabChange" class="role-tabs">
        <el-tab-pane label="我买到的" name="buyer" />
        <el-tab-pane label="我卖出的" name="seller" />
      </el-tabs>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待确认" name="1" />
        <el-tab-pane label="交易中" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>

      <template v-if="loading">
        <el-skeleton :rows="5" animated />
      </template>
      <template v-else-if="orders.length === 0">
        <el-empty :description="roleTab === 'buyer' ? '暂无购买订单' : '暂无出售订单'" />
      </template>
      <template v-else>
        <OrderCard
          v-for="order in orders"
          :key="order.id"
          :order="order"
          @click="goToDetail(order.id)"
        />
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppLayout from '@/components/common/AppLayout.vue'
import OrderCard from '@/components/business/OrderCard.vue'
import { getUserOrders } from '@/api/modules/order'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const orders = ref([])
const activeTab = ref('all')
const roleTab = ref('buyer')

const updateURLParams = () => {
  const query = {}
  if (activeTab.value !== 'all') {
    query.status = activeTab.value
  }
  query.role = roleTab.value
  router.replace({ query })
}

const fetchOrders = async () => {
  try {
    loading.value = true
    const params = activeTab.value !== 'all' ? { status: activeTab.value, role: roleTab.value } : { role: roleTab.value }
    const res = await getUserOrders(params)
    if (res && res.data) {
      orders.value = res.data.list || res.data
    } else {
      orders.value = []
    }
  } catch (error) {
    console.error('获取订单列表失败', error)
    ElMessage.error('获取订单列表失败')
    orders.value = []
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  updateURLParams()
  fetchOrders()
}

const handleRoleTabChange = () => {
  updateURLParams()
  fetchOrders()
}

const goToDetail = (id) => {
  router.push(`/order/${id}`)
}

watch(() => route.query.status, (status) => {
  if (status) {
    activeTab.value = status
  }
}, { immediate: true })

watch(() => route.query.role, (role) => {
  if (role === 'seller' || role === 'buyer') {
    roleTab.value = role
  }
}, { immediate: true })

onMounted(() => {
  if (route.query.status) {
    activeTab.value = route.query.status
  }
  if (route.query.role === 'seller') {
    roleTab.value = 'seller'
  }
  fetchOrders()
})
</script>

<style scoped>
.my-orders-page {
  padding: var(--spacing-lg);
}

.role-tabs {
  margin-bottom: var(--spacing-lg);
}

:deep(.role-tabs .el-tabs__header) {
  margin: 0 0 var(--spacing-md) 0;
}

:deep(.el-tabs__header) {
  margin: 0 0 var(--spacing-lg) 0;
}

/* 平滑过渡动画 */
:deep(.el-tabs__item) {
  transition: all 0.2s ease-in-out;
}

/* 骨架屏加载优化 */
:deep(.el-skeleton__paragraph) {
  --el-skeleton-color: var(--color-border);
}

/* 空状态优化 */
:deep(.el-empty__image) {
  width: 100px;
  height: 100px;
}

:deep(.el-empty__description) {
  color: var(--color-text-secondary);
}
</style>
