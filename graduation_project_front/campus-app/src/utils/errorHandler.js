
import { ElMessage, ElMessageBox } from 'element-plus'
import { errorLogger, ErrorType } from '@/utils/errorLogger'
import router from '@/router'
import { useUserStore } from '@/stores/user'

const isDev = import.meta.env.DEV

const userMessages = {
  network: '网络连接失败，请检查网络后重试',
  timeout: '请求超时，请稍后重试',
  unauthorized: '登录已过期，请重新登录',
  forbidden: '无权访问该资源',
  notFound: '请求的资源不存在',
  server: '服务器繁忙，请稍后重试',
  unknown: '发生了未知错误，请稍后重试'
}

export const handleApiError = (error, showMessage = true) => {
  errorLogger.addLog(error, ErrorType.API, {
    url: error.config?.url,
    method: error.config?.method,
    status: error.response?.status
  })

  let message = userMessages.unknown

  if (!error.response) {
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      message = userMessages.timeout
    } else {
      message = userMessages.network
    }
  } else {
    // 优先检查是否有业务错误消息
    if (error.response.data?.message) {
      message = error.response.data.message
    } else {
      switch (error.response.status) {
        case 401:
          message = userMessages.unauthorized
          handleUnauthorized()
          break
        case 403:
          message = userMessages.forbidden
          break
        case 404:
          message = userMessages.notFound
          break
        case 500:
        case 502:
        case 503:
        case 504:
          message = userMessages.server
          break
        default:
          message = error.response.data?.message || userMessages.unknown
      }
    }
  }

  if (showMessage) {
    ElMessage.error(message)
  }

  return Promise.reject(error)
}

const handleUnauthorized = () => {
  const userStore = useUserStore()
  userStore.logout()
  ElMessageBox.alert('登录已过期，请重新登录', '提示', {
    confirmButtonText: '确定',
    callback: () => {
      router.push('/login')
    }
  })
}

export const handleRuntimeError = (error, vm, info) => {
  errorLogger.addLog(error, ErrorType.RUNTIME, { component: vm?.$options?.name, info })
  if (isDev) {
    console.error('Runtime error:', error)
    // 开发模式下只在控制台输出，不弹窗打扰用户
  }
}

export const handlePromiseError = (event) => {
  const error = event.reason || event
  errorLogger.addLog(error, ErrorType.PROMISE, { type: 'unhandledrejection' })
  if (isDev) {
    console.error('Unhandled promise rejection:', error)
    // 开发模式下只在控制台输出，不弹窗打扰用户
  }
  event.preventDefault()
}

export const handleResourceError = (event) => {
  const error = new Error(`Resource load failed: ${event.target?.src || event.target?.href}`)
  errorLogger.addLog(error, ErrorType.RESOURCE, {
    tag: event.target?.tagName,
    url: event.target?.src || event.target?.href
  })
  // 资源加载失败不弹窗，只记录日志
}

export const setupGlobalErrorHandlers = (app) => {
  app.config.errorHandler = handleRuntimeError
  window.addEventListener('unhandledrejection', handlePromiseError)
  window.addEventListener('error', (event) => {
    if (event.target && (event.target.tagName === 'IMG' || event.target.tagName === 'SCRIPT' || event.target.tagName === 'LINK')) {
      handleResourceError(event)
    } else {
      errorLogger.addLog(event.error || event, ErrorType.RUNTIME)
    }
  }, true)
}
