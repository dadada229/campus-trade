import request from '@/api/request'

export const getCategories = () => {
  return request.get('/admin/categories')
}

export const addCategory = (data) => {
  return request.post('/admin/categories', data)
}

export const updateCategory = (id, data) => {
  return request.put(`/admin/categories/${id}`, data)
}

export const deleteCategory = (id) => {
  return request.delete(`/admin/categories/${id}`)
}

export const sortCategories = (data) => {
  return request.put('/admin/categories/sort', data)
}

export const getBanners = () => {
  return request.get('/admin/banners')
}

export const addBanner = (data) => {
  return request.post('/admin/banners', data)
}

export const updateBanner = (id, data) => {
  return request.put(`/admin/banners/${id}`, data)
}

export const deleteBanner = (id) => {
  return request.delete(`/admin/banners/${id}`)
}

export const toggleBanner = (id) => {
  return request.put(`/admin/banners/${id}/toggle`)
}

export const getNotices = () => {
  return request.get('/admin/notices')
}

export const addNotice = (data) => {
  return request.post('/admin/notices', data)
}

export const updateNotice = (id, data) => {
  return request.put(`/admin/notices/${id}`, data)
}

export const deleteNotice = (id) => {
  return request.delete(`/admin/notices/${id}`)
}

export const toggleNoticePin = (id) => {
  return request.put(`/admin/notices/${id}/pin`)
}

export const getUsers = (params) => {
  return request.get('/admin/users', { params })
}

export const addUser = (data) => {
  return request.post('/admin/users', data)
}

export const updateUser = (id, data) => {
  return request.put(`/admin/users/${id}`, data)
}

export const deleteUser = (id) => {
  return request.delete(`/admin/users/${id}`)
}

export const toggleUser = (id) => {
  return request.put(`/admin/users/${id}/toggle`)
}

export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
