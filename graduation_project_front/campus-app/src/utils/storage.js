const TOKEN_KEY = 'campus_app_token'
const USER_INFO_KEY = 'campus_app_user_info'

export const storage = {
  setToken(token) {
    if (token === null || token === undefined) {
      token = ''
    }
    localStorage.setItem(TOKEN_KEY, token)
  },
  getToken() {
    return localStorage.getItem(TOKEN_KEY) || ''
  },
  removeToken() {
    localStorage.removeItem(TOKEN_KEY)
  },
  setUserInfo(info) {
    if (info === null || info === undefined) {
      localStorage.removeItem(USER_INFO_KEY)
    } else {
      localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
    }
  },
  getUserInfo() {
    const info = localStorage.getItem(USER_INFO_KEY)
    if (!info) return null
    try {
      return JSON.parse(info)
    } catch (e) {
      console.error('解析用户信息失败', e)
      return null
    }
  },
  removeUserInfo() {
    localStorage.removeItem(USER_INFO_KEY)
  },
  clear() {
    this.removeToken()
    this.removeUserInfo()
  },
}
