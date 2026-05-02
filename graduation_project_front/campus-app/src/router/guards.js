import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

export const setupGuards = (router) => {
  router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()

    if (!userStore.isInitialized) {
      await userStore.initializeUser()
    }

    document.title = to.meta.title ? `${to.meta.title} - 校园二手交易平台` : '校园二手交易平台'

    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath },
      })
      return
    }

    if (to.meta.adminOnly && !userStore.isAdmin) {
      ElMessage.error('您没有权限访问该页面')
      next('/')
      return
    }

    next()
  })
}
