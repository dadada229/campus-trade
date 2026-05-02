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

export const createOrder = (data) => {
  const { message, ...rest } = data
  return request.post('/orders', { ...rest, buyerMessage: message })
}

export const getOrderDetail = (id) => {
  return request.get(`/orders/${id}`)
}

export const getUserOrders = (params) => {
  return request.get('/user/orders', { params }).then(res => {
    if (res.code === 200 && res.data) {
      return { ...res, data: adaptPageData(res.data) }
    }
    return res
  })
}

export const confirmOrder = (id) => {
  return request.put(`/orders/${id}/confirm`)
}

export const completeOrder = (id) => {
  return request.put(`/orders/${id}/complete`)
}

export const cancelOrder = (id) => {
  return request.put(`/orders/${id}/cancel`)
}

export const getUserAddresses = () => {
  return request.get('/user/addresses')
}

export const createAddress = (data) => {
  return request.post('/user/addresses', data)
}

export const updateAddress = (id, data) => {
  return request.put(`/user/addresses/${id}`, data)
}

export const deleteAddress = (id) => {
  return request.delete(`/user/addresses/${id}`)
}

export const setDefaultAddress = (id) => {
  return request.put(`/user/addresses/${id}/default`)
}
