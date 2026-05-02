import request from '@/api/request'

const adaptPageData = (data) => {
  if (data && data.records !== undefined) {
    return {
      list: data.records,
      total: data.total,
      page: data.current,
      size: data.size,
      pages: data.pages
    }
  }
  return data
}

export const getProducts = (params) => {
  return request.get('/products', { params }).then(res => {
    if (res.code === 200 && res.data) {
      return { ...res, data: adaptPageData(res.data) }
    }
    return res
  })
}

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData).then(res => {
    if (res.code === 200 && typeof res.data === 'string') {
      return { ...res, data: { url: res.data } }
    }
    return res
  })
}

export const getProductDetail = (id) => {
  return request.get(`/products/${id}`)
}

export const getBanners = () => {
  return request.get('/banners').then(res => {
    if (res.code === 200 && res.data) {
      const adaptedBanners = res.data.map(banner => ({
        ...banner,
        image: banner.imageUrl || banner.image
      }))
      return { ...res, data: adaptedBanners }
    }
    return res
  })
}

export const getCategories = (params) => {
  return request.get('/categories', { params })
}

export const getNotices = (params) => {
  return request.get('/notices', { params }).then(res => {
    if (res.code === 200 && res.data) {
      // 首页只需要通知数组，从分页数据中提取 records
      if (res.data.records !== undefined) {
        return { ...res, data: res.data.records }
      }
    }
    return res
  })
}

export const favoriteProduct = (id) => {
  return request.post(`/products/${id}/favorite`)
}

export const unfavoriteProduct = (id) => {
  return request.delete(`/products/${id}/favorite`)
}

export const createProduct = (data) => {
  const { tradeLocation, ...rest } = data
  return request.post('/products', { ...rest, tradePlace: tradeLocation })
}

export const updateProduct = (id, data) => {
  const { tradeLocation, ...rest } = data
  return request.put(`/products/${id}`, { ...rest, tradePlace: tradeLocation })
}

export const getUserProducts = (params) => {
  return request.get('/products/user/my', { params }).then(res => {
    if (res.code === 200 && res.data) {
      return { ...res, data: adaptPageData(res.data) }
    }
    return res
  })
}

export const offShelfProduct = (id) => {
  return request.put(`/products/${id}/off-shelf`)
}

export const markProductSold = (id) => {
  return request.put(`/products/${id}/mark-sold`)
}

export const getFavorites = (params) => {
  return request.get('/favorites', { params }).then(res => {
    if (res.code === 200 && res.data) {
      return { ...res, data: adaptPageData(res.data) }
    }
    return res
  })
}

export const deleteFavorite = (id) => {
  return request.delete(`/favorites/${id}`)
}
