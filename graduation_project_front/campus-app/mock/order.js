import Mock from 'mockjs'

const orders = [
  {
    id: 1,
    orderNo: 'OD202404250001',
    productId: 1,
    productTitle: 'MacBook Pro 14寸 M3芯片 16+512G',
    productImage: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop',
    productPrice: 12999,
    buyerId: 1,
    buyerName: '李同学',
    sellerId: 2,
    sellerName: '王同学',
    addressSnapshot: {
      receiverName: '李同学',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '海淀区',
      detail: '某某大学图书馆'
    },
    buyerMessage: '请周末在图书馆门口交易',
    status: 1,
    statusText: '待确认',
    createdAt: '2024-04-25T10:30:00Z'
  },
  {
    id: 2,
    orderNo: 'OD202404240002',
    productId: 2,
    productTitle: 'iPad Air 5代 WiFi版 64G 深空灰',
    productImage: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop',
    productPrice: 3499,
    buyerId: 1,
    buyerName: '李同学',
    sellerId: 2,
    sellerName: '王同学',
    addressSnapshot: {
      receiverName: '李同学',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '海淀区',
      detail: '某某大学图书馆'
    },
    buyerMessage: '',
    status: 2,
    statusText: '交易中',
    createdAt: '2024-04-24T14:00:00Z'
  },
  {
    id: 3,
    orderNo: 'OD202404230003',
    productId: 3,
    productTitle: '全新未拆封 AirPods Pro 2代',
    productImage: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop',
    productPrice: 1399,
    buyerId: 1,
    buyerName: '李同学',
    sellerId: 3,
    sellerName: '张同学',
    addressSnapshot: {
      receiverName: '李同学',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '海淀区',
      detail: '某某大学图书馆'
    },
    buyerMessage: '',
    status: 3,
    statusText: '已完成',
    createdAt: '2024-04-23T09:00:00Z'
  },
  {
    id: 4,
    orderNo: 'OD202404220004',
    productId: 5,
    productTitle: 'Nike Dunk Low 黑白熊猫 42码',
    productImage: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
    productPrice: 899,
    buyerId: 1,
    buyerName: '李同学',
    sellerId: 4,
    sellerName: '赵同学',
    addressSnapshot: {
      receiverName: '李同学',
      phone: '13800138001',
      province: '北京市',
      city: '北京市',
      district: '海淀区',
      detail: '某某大学图书馆'
    },
    buyerMessage: '尺码不合适想取消',
    status: 4,
    statusText: '已取消',
    createdAt: '2024-04-22T16:00:00Z'
  }
]

const addresses = [
  {
    id: 1,
    userId: 1,
    receiverName: '李同学',
    phone: '13800138001',
    province: '北京市',
    city: '北京市',
    district: '海淀区',
    detail: '某某大学图书馆',
    isDefault: true
  },
  {
    id: 2,
    userId: 1,
    receiverName: '李同学',
    phone: '13800138002',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail: '某某街道某某号',
    isDefault: false
  }
]

export default [
  {
    url: '/api/orders',
    method: 'post',
    response: ({ body }) => {
      const orderNo = 'OD' + Date.now()
      const newOrder = {
        id: orders.length > 0 ? Math.max(...orders.map(o => o.id)) + 1 : 1,
        orderNo: orderNo,
        productId: body.productId,
        productTitle: '',
        productImage: '',
        productPrice: 0,
        buyerId: 1,
        buyerName: '李同学',
        sellerId: 2,
        sellerName: '卖家',
        addressSnapshot: body.addressSnapshot,
        buyerMessage: body.buyerMessage || '',
        status: 1,
        statusText: '待确认',
        createdAt: new Date().toISOString()
      }
      orders.unshift(newOrder)
      return {
        code: 200,
        data: newOrder,
        message: '下单成功'
      }
    }
  },
  {
    url: '/api/orders/:id',
    method: 'get',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const order = orders.find(o => o.id === id)
      if (!order) {
        return {
          code: 404,
          data: null,
          message: '订单不存在'
        }
      }
      return {
        code: 200,
        data: order,
        message: 'success'
      }
    }
  },
  {
    url: '/api/user/orders',
    method: 'get',
    response: ({ query }) => {
      let list = [...orders]
      if (query.status) {
        list = list.filter(o => o.status === parseInt(query.status))
      }
      return {
        code: 200,
        data: list,
        message: 'success'
      }
    }
  },
  {
    url: '/api/orders/:id/confirm',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = orders.findIndex(o => o.id === id)
      if (index !== -1) {
        orders[index].status = 2
        orders[index].statusText = '交易中'
      }
      return {
        code: 200,
        data: true,
        message: '确认成功'
      }
    }
  },
  {
    url: '/api/orders/:id/complete',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = orders.findIndex(o => o.id === id)
      if (index !== -1) {
        orders[index].status = 3
        orders[index].statusText = '已完成'
      }
      return {
        code: 200,
        data: true,
        message: '完成成功'
      }
    }
  },
  {
    url: '/api/orders/:id/cancel',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = orders.findIndex(o => o.id === id)
      if (index !== -1) {
        orders[index].status = 4
        orders[index].statusText = '已取消'
      }
      return {
        code: 200,
        data: true,
        message: '取消成功'
      }
    }
  },
  {
    url: '/api/user/addresses',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: addresses,
        message: 'success'
      }
    }
  },
  {
    url: '/api/user/addresses',
    method: 'post',
    response: ({ body }) => {
      const newAddress = {
        id: addresses.length > 0 ? Math.max(...addresses.map(a => a.id)) + 1 : 1,
        userId: 1,
        receiverName: body.receiverName,
        phone: body.phone,
        province: body.province || '北京市',
        city: body.city || '北京市',
        district: body.district || '海淀区',
        detail: body.detail,
        isDefault: body.isDefault || false
      }
      if (body.isDefault) {
        addresses.forEach(a => a.isDefault = false)
      }
      addresses.push(newAddress)
      return {
        code: 200,
        data: newAddress,
        message: '添加成功'
      }
    }
  },
  {
    url: '/api/user/addresses/:id',
    method: 'put',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const index = addresses.findIndex(a => a.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '地址不存在'
        }
      }
      if (body.isDefault) {
        addresses.forEach(a => a.isDefault = false)
      }
      addresses[index] = {
        ...addresses[index],
        receiverName: body.receiverName,
        phone: body.phone,
        province: body.province || addresses[index].province,
        city: body.city || addresses[index].city,
        district: body.district || addresses[index].district,
        detail: body.detail,
        isDefault: body.isDefault
      }
      return {
        code: 200,
        data: true,
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/user/addresses/:id',
    method: 'delete',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = addresses.findIndex(a => a.id === id)
      if (index !== -1) {
        addresses.splice(index, 1)
      }
      return {
        code: 200,
        data: true,
        message: '删除成功'
      }
    }
  },
  {
    url: '/api/user/addresses/:id/default',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      addresses.forEach(a => a.isDefault = a.id === id)
      return {
        code: 200,
        data: true,
        message: '设置成功'
      }
    }
  }
]
