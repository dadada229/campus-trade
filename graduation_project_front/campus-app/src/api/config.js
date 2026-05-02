
export const ApiType = {
  PUBLIC: 'public',
  PRIVATE: 'private',
  ADMIN: 'admin'
}

export const apiConfig = {
  public: [
    '/auth/login',
    '/auth/register',
    '/products',
    '/products/search',
    '/products/\\d+',
    '/banners',
    '/categories',
    '/notices',
    '/notices/\\d+',
    '/upload'
  ],
  private: [
    '/user/.*',
    '/favorites',
    '/orders',
    '/conversations'
  ],
  admin: [
    '/admin/.*'
  ]
}

export const getApiType = (url) => {
  const cleanUrl = url.replace(/^\/api/, '')
  for (const pattern of apiConfig.admin) {
    if (new RegExp(`^${pattern}$`).test(cleanUrl)) {
      return ApiType.ADMIN
    }
  }
  for (const pattern of apiConfig.private) {
    if (new RegExp(`^${pattern}$`).test(cleanUrl)) {
      return ApiType.PRIVATE
    }
  }
  for (const pattern of apiConfig.public) {
    if (new RegExp(`^${pattern}$`).test(cleanUrl)) {
      return ApiType.PUBLIC
    }
  }
  return ApiType.PRIVATE
}

export const isPublicApi = (url) => getApiType(url) === ApiType.PUBLIC
export const isPrivateApi = (url) => getApiType(url) === ApiType.PRIVATE
export const isAdminApi = (url) => getApiType(url) === ApiType.ADMIN
