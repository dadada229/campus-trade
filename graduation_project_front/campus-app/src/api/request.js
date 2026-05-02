import axios from 'axios'
import { ElMessageBox } from 'element-plus'
import { storage } from '@/utils/storage'
import { useUserStore } from '@/stores/user'
import router from '@/router'
import { getApiType, ApiType } from '@/api/config'
import { handleApiError } from '@/utils/errorHandler'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(
  (config) => {
    const apiType = getApiType(config.url)
    const userStore = useUserStore()

    if (apiType === ApiType.PRIVATE || apiType === ApiType.ADMIN) {
      if (!userStore.isLoggedIn) {
        ElMessageBox.confirm('请先登录后再继续操作', '提示', {
          confirmButtonText: '去登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          router.push({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath }
          })
        }).catch(() => {})
        return Promise.reject(new Error('未登录'))
      }

      if (apiType === ApiType.ADMIN && !userStore.isAdmin) {
        return Promise.reject(new Error('无权访问管理员接口'))
      }
    }

    const token = storage.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return handleApiError(error, false)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    if (res.code !== 200) {
      const error = new Error(res.message || '请求失败')
      error.response = { status: res.code, data: res }
      error.config = response.config
      return handleApiError(error)
    }
    return res
  },
  (error) => {
    return handleApiError(error)
  }
)

export default request