import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@/utils/storage'
import { login as loginApi, register as registerApi, getUserInfo } from '@/api/modules/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(storage.getToken())
  const userInfo = ref(storage.getUserInfo())
  const isInitialized = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 1)

  const setToken = (newToken) => {
    token.value = newToken
    storage.setToken(newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    storage.setUserInfo(info)
  }

  const login = async (phone, password) => {
    const res = await loginApi({ phone, password })
    if (!res || !res.data) {
      throw new Error('登录失败，响应数据无效')
    }
    const { token, userInfo } = res.data
    if (!token) {
      throw new Error('登录失败，未获取到Token')
    }
    setToken(token)
    setUserInfo(userInfo || null)
    return res
  }

  const register = async (data) => {
    const res = await registerApi(data)
    return res
  }

  const fetchUser = async () => {
    try {
      const res = await getUserInfo()
      setUserInfo(res.data)
      return res
    } catch (error) {
      console.error('获取用户信息失败', error)
      if (error.response && error.response.status === 401) {
        logout()
      }
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    storage.clear()
  }

  const initializeUser = async () => {
    if (isInitialized.value) return
    isInitialized.value = true
    
    if (token.value) {
      try {
        await fetchUser()
      } catch (error) {
        console.error('初始化用户信息失败', error)
      }
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isInitialized,
    setToken,
    setUserInfo,
    login,
    register,
    fetchUser,
    logout,
    initializeUser,
  }
})
