
import { storage } from '@/utils/storage'

const isDev = import.meta.env.DEV

class ErrorLogger {
  constructor() {
    this.logs = []
    this.maxLogs = 100
  }

  addLog(error, type = 'unknown', meta = {}) {
    const log = {
      timestamp: new Date().toISOString(),
      type,
      message: error.message || String(error),
      stack: error.stack || null,
      userInfo: storage.getUserInfo() ? { id: storage.getUserInfo().id } : null,
      url: window.location.href,
      meta
    }
    this.logs.push(log)
    if (this.logs.length > this.maxLogs) {
      this.logs.shift()
    }
    if (isDev) {
      console.log('[ErrorLog]', log)
    }
  }

  getLogs() {
    return this.logs
  }

  clearLogs() {
    this.logs = []
  }
}

export const errorLogger = new ErrorLogger()

export const ErrorType = {
  API: 'api',
  RUNTIME: 'runtime',
  PROMISE: 'promise',
  RESOURCE: 'resource'
}
