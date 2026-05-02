import Mock from 'mockjs'

let categories = [
  {
    id: 1,
    name: '数码产品',
    parentId: 0,
    sort: 1,
    children: [
      { id: 11, name: '手机', parentId: 1, sort: 1 },
      { id: 12, name: '电脑', parentId: 1, sort: 2 },
      { id: 13, name: '平板', parentId: 1, sort: 3 },
      { id: 14, name: '耳机/音箱', parentId: 1, sort: 4 }
    ]
  },
  {
    id: 2,
    name: '图书教材',
    parentId: 0,
    sort: 2,
    children: [
      { id: 21, name: '考研资料', parentId: 2, sort: 1 },
      { id: 22, name: '教材课本', parentId: 2, sort: 2 },
      { id: 23, name: '课外读物', parentId: 2, sort: 3 }
    ]
  },
  {
    id: 3,
    name: '服饰鞋包',
    parentId: 0,
    sort: 3,
    children: [
      { id: 31, name: '男装', parentId: 3, sort: 1 },
      { id: 32, name: '女装', parentId: 3, sort: 2 },
      { id: 33, name: '鞋靴', parentId: 3, sort: 3 },
      { id: 34, name: '箱包', parentId: 3, sort: 4 }
    ]
  },
  {
    id: 4,
    name: '运动乐器',
    parentId: 0,
    sort: 4,
    children: [
      { id: 41, name: '运动器材', parentId: 4, sort: 1 },
      { id: 42, name: '乐器', parentId: 4, sort: 2 }
    ]
  },
  {
    id: 5,
    name: '生活用品',
    parentId: 0,
    sort: 5,
    children: [
      { id: 51, name: '宿舍用品', parentId: 5, sort: 1 },
      { id: 52, name: '家具', parentId: 5, sort: 2 }
    ]
  },
  {
    id: 6,
    name: '其他',
    parentId: 0,
    sort: 6,
    children: []
  }
]

let banners = [
  {
    id: 1,
    image: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=1200&h=400&fit=crop',
    link: '',
    sort: 1,
    enabled: true
  },
  {
    id: 2,
    image: 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=1200&h=400&fit=crop',
    link: '',
    sort: 2,
    enabled: true
  },
  {
    id: 3,
    image: 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=1200&h=400&fit=crop',
    link: '',
    sort: 3,
    enabled: true
  }
]

let notices = [
  {
    id: 1,
    title: '🎉 校园二手交易平台正式上线！',
    content: '欢迎同学们来发布闲置物品，开启您的校园二手之旅！',
    isPinned: true,
    createdAt: '2024-04-20T09:00:00Z'
  },
  {
    id: 2,
    title: '⚠️ 交易安全提醒',
    content: '请尽量选择当面交易，仔细检查物品，确认无误后再完成交易哦！',
    isPinned: true,
    createdAt: '2024-04-21T10:00:00Z'
  }
]

let users = [
  {
    id: 1,
    username: 'admin',
    nickname: '管理员',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
    phone: 'admin',
    email: 'admin@example.com',
    role: 'admin',
    status: true,
    createdAt: '2024-04-01T00:00:00Z',
    updatedAt: '2024-04-01T00:00:00Z'
  },
  {
    id: 2,
    username: '13800138001',
    nickname: '张三',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    phone: '13800138001',
    email: 'zhangsan@example.com',
    role: 'user',
    status: true,
    createdAt: '2024-04-10T09:00:00Z',
    updatedAt: '2024-04-10T09:00:00Z'
  },
  {
    id: 3,
    username: '13800138002',
    nickname: '李四',
    avatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=100&h=100&fit=crop',
    phone: '13800138002',
    email: 'lisi@example.com',
    role: 'user',
    status: true,
    createdAt: '2024-04-12T10:00:00Z',
    updatedAt: '2024-04-12T10:00:00Z'
  },
  {
    id: 4,
    username: '13800138003',
    nickname: '王五',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    phone: '13800138003',
    email: 'wangwu@example.com',
    role: 'user',
    status: false,
    createdAt: '2024-04-15T11:00:00Z',
    updatedAt: '2024-04-20T14:30:00Z'
  }
]

const flattenCategories = (cats) => {
  let result = []
  cats.forEach(cat => {
    result.push({ ...cat, children: undefined })
    if (cat.children && cat.children.length > 0) {
      result = result.concat(flattenCategories(cat.children))
    }
  })
  return result
}

const findCategoryById = (id, cats) => {
  for (let cat of cats) {
    if (cat.id === id) return cat
    if (cat.children) {
      const found = findCategoryById(id, cat.children)
      if (found) return found
    }
  }
  return null
}

const getNextId = () => {
  const all = flattenCategories(categories)
  return Math.max(...all.map(c => c.id), 0) + 1
}

export default [
  {
    url: '/api/notices',
    method: 'get',
    response: ({ query }) => {
      let result = [...notices]
      if (query.pinned === '1') {
        result = result.filter(n => n.isPinned)
      }
      return {
        code: 200,
        data: result,
        message: 'success'
      }
    }
  },
  {
    url: '/api/notices/:id',
    method: 'get',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const notice = notices.find(n => n.id === id)
      if (!notice) {
        return {
          code: 404,
          data: null,
          message: '公告不存在'
        }
      }
      return {
        code: 200,
        data: notice,
        message: 'success'
      }
    }
  },
  {
    url: '/api/admin/categories',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: categories,
        message: 'success'
      }
    }
  },
  {
    url: '/api/admin/categories',
    method: 'post',
    response: ({ body }) => {
      const newCat = {
        id: getNextId(),
        name: body.name,
        parentId: body.parentId || 0,
        sort: body.sort || 1,
        children: []
      }
      if (newCat.parentId === 0) {
        categories.push(newCat)
      } else {
        const parent = findCategoryById(newCat.parentId, categories)
        if (parent) {
          if (!parent.children) parent.children = []
          parent.children.push(newCat)
        }
      }
      return {
        code: 200,
        data: newCat,
        message: '添加成功'
      }
    }
  },
  {
    url: '/api/admin/categories/:id',
    method: 'put',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const cat = findCategoryById(id, categories)
      if (!cat) {
        return {
          code: 404,
          data: null,
          message: '分类不存在'
        }
      }
      cat.name = body.name
      cat.sort = body.sort
      return {
        code: 200,
        data: cat,
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/admin/categories/:id',
    method: 'delete',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const deleteFromList = (list) => {
        const index = list.findIndex(c => c.id === id)
        if (index !== -1) {
          const cat = list[index]
          if (cat.children && cat.children.length > 0) {
            return { canDelete: false, message: '该分类下有子分类，无法删除' }
          }
          list.splice(index, 1)
          return { canDelete: true }
        }
        for (let cat of list) {
          if (cat.children) {
            const result = deleteFromList(cat.children)
            if (result) return result
          }
        }
        return null
      }
      const result = deleteFromList(categories)
      if (!result) {
        return {
          code: 404,
          data: null,
          message: '分类不存在'
        }
      }
      if (!result.canDelete) {
        return {
          code: 400,
          data: null,
          message: result.message
        }
      }
      return {
        code: 200,
        data: true,
        message: '删除成功'
      }
    }
  },
  {
    url: '/api/admin/categories/sort',
    method: 'put',
    response: ({ body }) => {
      return {
        code: 200,
        data: true,
        message: '排序成功'
      }
    }
  },
  {
    url: '/api/admin/banners',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: banners,
        message: 'success'
      }
    }
  },
  {
    url: '/api/admin/banners',
    method: 'post',
    response: ({ body }) => {
      const newBanner = {
        id: banners.length > 0 ? Math.max(...banners.map(b => b.id)) + 1 : 1,
        image: body.image,
        link: body.link || '',
        sort: body.sort || banners.length + 1,
        enabled: body.enabled !== false
      }
      banners.push(newBanner)
      return {
        code: 200,
        data: newBanner,
        message: '添加成功'
      }
    }
  },
  {
    url: '/api/admin/banners/:id',
    method: 'put',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const index = banners.findIndex(b => b.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '轮播图不存在'
        }
      }
      banners[index] = {
        ...banners[index],
        image: body.image,
        link: body.link,
        sort: body.sort,
        enabled: body.enabled
      }
      return {
        code: 200,
        data: banners[index],
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/admin/banners/:id',
    method: 'delete',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = banners.findIndex(b => b.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '轮播图不存在'
        }
      }
      banners.splice(index, 1)
      return {
        code: 200,
        data: true,
        message: '删除成功'
      }
    }
  },
  {
    url: '/api/admin/banners/:id/toggle',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const banner = banners.find(b => b.id === id)
      if (!banner) {
        return {
          code: 404,
          data: null,
          message: '轮播图不存在'
        }
      }
      banner.enabled = !banner.enabled
      return {
        code: 200,
        data: banner,
        message: '切换成功'
      }
    }
  },
  {
    url: '/api/admin/notices',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: notices,
        message: 'success'
      }
    }
  },
  {
    url: '/api/admin/notices',
    method: 'post',
    response: ({ body }) => {
      const newNotice = {
        id: notices.length > 0 ? Math.max(...notices.map(n => n.id)) + 1 : 1,
        title: body.title,
        content: body.content,
        isPinned: body.isPinned || false,
        createdAt: new Date().toISOString()
      }
      notices.unshift(newNotice)
      return {
        code: 200,
        data: newNotice,
        message: '添加成功'
      }
    }
  },
  {
    url: '/api/admin/notices/:id',
    method: 'put',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const index = notices.findIndex(n => n.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '公告不存在'
        }
      }
      notices[index] = {
        ...notices[index],
        title: body.title,
        content: body.content,
        isPinned: body.isPinned,
        updatedAt: new Date().toISOString()
      }
      return {
        code: 200,
        data: notices[index],
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/admin/notices/:id',
    method: 'delete',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const index = notices.findIndex(n => n.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '公告不存在'
        }
      }
      notices.splice(index, 1)
      return {
        code: 200,
        data: true,
        message: '删除成功'
      }
    }
  },
  {
    url: '/api/admin/notices/:id/pin',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const notice = notices.find(n => n.id === id)
      if (!notice) {
        return {
          code: 404,
          data: null,
          message: '公告不存在'
        }
      }
      notice.isPinned = !notice.isPinned
      return {
        code: 200,
        data: notice,
        message: '切换成功'
      }
    }
  },
  {
    url: '/api/admin/users',
    method: 'get',
    response: ({ query }) => {
      let result = [...users]
      if (query.keyword) {
        const keyword = query.keyword.toLowerCase()
        result = result.filter(u => 
          u.username.toLowerCase().includes(keyword) ||
          u.nickname.toLowerCase().includes(keyword) ||
          u.phone.includes(keyword) ||
          u.email.toLowerCase().includes(keyword)
        )
      }
      if (query.role) {
        result = result.filter(u => u.role === query.role)
      }
      if (query.status !== undefined && query.status !== '') {
        const status = query.status === 'true' || query.status === true
        result = result.filter(u => u.status === status)
      }
      return {
        code: 200,
        data: result,
        message: 'success'
      }
    }
  },
  {
    url: '/api/admin/users',
    method: 'post',
    response: ({ body }) => {
      const newUser = {
        id: users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1,
        username: body.username,
        nickname: body.nickname || body.username,
        avatar: body.avatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop',
        phone: body.phone || body.username,
        email: body.email || '',
        role: body.role || 'user',
        status: body.status !== false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      users.push(newUser)
      return {
        code: 200,
        data: newUser,
        message: '添加成功'
      }
    }
  },
  {
    url: '/api/admin/users/:id',
    method: 'put',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const index = users.findIndex(u => u.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '用户不存在'
        }
      }
      users[index] = {
        ...users[index],
        username: body.username,
        nickname: body.nickname,
        avatar: body.avatar,
        phone: body.phone,
        email: body.email,
        role: body.role,
        status: body.status,
        updatedAt: new Date().toISOString()
      }
      return {
        code: 200,
        data: users[index],
        message: '更新成功'
      }
    }
  },
  {
    url: '/api/admin/users/:id',
    method: 'delete',
    response: ({ params }) => {
      const id = parseInt(params.id)
      if (id === 1) {
        return {
          code: 400,
          data: null,
          message: '管理员账户无法删除'
        }
      }
      const index = users.findIndex(u => u.id === id)
      if (index === -1) {
        return {
          code: 404,
          data: null,
          message: '用户不存在'
        }
      }
      users.splice(index, 1)
      return {
        code: 200,
        data: true,
        message: '删除成功'
      }
    }
  },
  {
    url: '/api/admin/users/:id/toggle',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const user = users.find(u => u.id === id)
      if (!user) {
        return {
          code: 404,
          data: null,
          message: '用户不存在'
        }
      }
      if (id === 1) {
        return {
          code: 400,
          data: null,
          message: '管理员账户无法禁用'
        }
      }
      user.status = !user.status
      return {
        code: 200,
        data: user,
        message: '切换成功'
      }
    }
  }
]
