// 备用 Mock 数据
const products = [
  {
    id: 1,
    title: 'MacBook Pro 14寸 M3芯片 16+512G',
    description: '2024款，成色99新，无划痕无维修，电池循环15次，平时很爱惜',
    price: 12999,
    originalPrice: 16999,
    condition: 2,
    conditionText: '几乎全新',
    categoryId: 1,
    images: [
      'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop',
      'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400&h=400&fit=crop'
    ],
    sellerId: 1,
    sellerName: '李同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
    sellerSchool: '北京大学',
    tradePlace: '图书馆门口',
    status: 1,
    viewCount: 256,
    createdAt: '2024-04-25T10:00:00Z'
  },
  {
    id: 2,
    title: 'iPad Air 5代 WiFi版 64G 深空灰',
    description: '今年2月购入，用了2个月，考研结束不用了，包装齐全',
    price: 3499,
    originalPrice: 4599,
    condition: 2,
    conditionText: '几乎全新',
    categoryId: 1,
    images: [
      'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop'
    ],
    sellerId: 2,
    sellerName: '王同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    sellerSchool: '清华大学',
    status: 1,
    viewCount: 189,
    createdAt: '2024-04-24T14:00:00Z'
  },
  {
    id: 3,
    title: '全新未拆封 AirPods Pro 2代',
    description: '公司抽奖得的，自己有耳机了，原价出',
    price: 1399,
    originalPrice: 1899,
    condition: 1,
    conditionText: '全新',
    categoryId: 1,
    images: [
      'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop'
    ],
    sellerId: 3,
    sellerName: '张同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    sellerSchool: '中国人民大学',
    status: 1,
    viewCount: 412,
    createdAt: '2024-04-23T09:00:00Z'
  },
  {
    id: 4,
    title: '考研数学 李永乐660题 线性代数讲义',
    description: '几乎全新，只写了几页，25考研必买资料',
    price: 45,
    originalPrice: 89,
    condition: 3,
    conditionText: '轻微使用',
    categoryId: 2,
    images: [
      'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop'
    ],
    sellerId: 1,
    sellerName: '李同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
    sellerSchool: '北京大学',
    status: 2,
    viewCount: 86,
    createdAt: '2024-04-22T16:00:00Z'
  },
  {
    id: 5,
    title: 'Nike Dunk Low 黑白熊猫 42码',
    description: '正品有购买记录，穿了不超过5次，鞋盒都在',
    price: 899,
    originalPrice: 1299,
    condition: 2,
    conditionText: '几乎全新',
    categoryId: 3,
    images: [
      'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop'
    ],
    sellerId: 4,
    sellerName: '赵同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop',
    sellerSchool: '北京师范大学',
    status: 1,
    viewCount: 356,
    createdAt: '2024-04-21T11:00:00Z'
  },
  {
    id: 6,
    title: '小米手环8 NFC版 黑色',
    description: '用了半年，功能完好，原装充电器，表带换了个新的',
    price: 199,
    originalPrice: 299,
    condition: 3,
    conditionText: '轻微使用',
    categoryId: 1,
    images: [
      'https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=400&h=400&fit=crop'
    ],
    sellerId: 2,
    sellerName: '王同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    sellerSchool: '清华大学',
    status: 1,
    viewCount: 167,
    createdAt: '2024-04-20T18:00:00Z'
  },
  {
    id: 7,
    title: '闲置吉他 YAMAHA FG800 41寸',
    description: '学了半年放弃了，音色很好，送琴包、变调夹、拨片',
    price: 1200,
    originalPrice: 1899,
    condition: 3,
    conditionText: '轻微使用',
    categoryId: 4,
    images: [
      'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&h=400&fit=crop'
    ],
    sellerId: 5,
    sellerName: '刘同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1599566150163-29194dcabd36?w=100&h=100&fit=crop',
    sellerSchool: '北京航空航天大学',
    status: 1,
    viewCount: 134,
    createdAt: '2024-04-19T15:00:00Z'
  },
  {
    id: 8,
    title: '经济学原理 曼昆 宏观+微观 第七版',
    description: '上课用的教材，有笔记，但是不影响阅读',
    price: 60,
    originalPrice: 128,
    condition: 4,
    conditionText: '明显痕迹',
    categoryId: 2,
    images: [
      'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400&h=400&fit=crop'
    ],
    sellerId: 3,
    sellerName: '张同学',
    sellerAvatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    sellerSchool: '中国人民大学',
    status: 3,
    viewCount: 78,
    createdAt: '2024-04-18T10:00:00Z'
  }
]

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

let favoriteProductIds = []

const banners = [
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

const categories = [
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

const notices = [
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

const conversations = [
  {
    id: 1,
    user1Id: 1,
    user2Id: 2,
    user2Name: '王同学',
    user2Avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop',
    productId: 2,
    productTitle: 'iPad Air 5代 WiFi版 64G 深空灰',
    productImage: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop',
    lastMessage: '好的，那我们周末见！',
    lastMessageAt: '2024-04-25T16:30:00Z',
    unreadCount: 2
  },
  {
    id: 2,
    user1Id: 1,
    user2Id: 3,
    user2Name: '张同学',
    user2Avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop',
    productId: 3,
    productTitle: '全新未拆封 AirPods Pro 2代',
    productImage: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop',
    lastMessage: '请问什么时候方便交易？',
    lastMessageAt: '2024-04-24T14:20:00Z',
    unreadCount: 0
  },
  {
    id: 3,
    user1Id: 1,
    user2Id: 4,
    user2Name: '赵同学',
    user2Avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&h=100&fit=crop',
    productId: 5,
    productTitle: 'Nike Dunk Low 黑白熊猫 42码',
    productImage: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop',
    lastMessage: '好的，订单已取消。',
    lastMessageAt: '2024-04-23T09:15:00Z',
    unreadCount: 0
  }
]

const messages = {
  1: [
    {
      id: 1,
      senderId: 2,
      content: '你好，我是这台 iPad 的卖家王同学。',
      createdAt: '2024-04-24T14:00:00Z'
    },
    {
      id: 2,
      senderId: 1,
      content: '你好，想问下这台 iPad 是原装的吗？还有包装吗？',
      createdAt: '2024-04-24T14:05:00Z'
    },
    {
      id: 3,
      senderId: 2,
      content: '是的，原装行货，包装齐全，发票都在。',
      createdAt: '2024-04-24T14:10:00Z'
    },
    {
      id: 4,
      senderId: 1,
      content: '好的，那可以小刀吗？3400 可以吗？',
      createdAt: '2024-04-24T14:15:00Z'
    },
    {
      id: 5,
      senderId: 2,
      content: '不好意思，价格已经很低了，不能再少了。',
      createdAt: '2024-04-24T14:20:00Z'
    },
    {
      id: 6,
      senderId: 1,
      content: '好的，那我要了，我们什么时候方便交易？',
      createdAt: '2024-04-25T16:00:00Z'
    },
    {
      id: 7,
      senderId: 2,
      content: '这周六下午 3 点，在图书馆门口可以吗？',
      createdAt: '2024-04-25T16:10:00Z'
    },
    {
      id: 8,
      senderId: 1,
      content: '好的，那我们周末见！',
      createdAt: '2024-04-25T16:30:00Z'
    }
  ],
  2: [
    {
      id: 1,
      senderId: 1,
      content: '你好，我想请问下这个 AirPods Pro 2 代是全新的吗？',
      createdAt: '2024-04-23T10:00:00Z'
    },
    {
      id: 2,
      senderId: 3,
      content: '是的，全新未拆封的，公司抽奖得的，自己有耳机了。',
      createdAt: '2024-04-23T10:05:00Z'
    }
  ],
  3: [
    {
      id: 1,
      senderId: 1,
      content: '你好，我想请问下这双鞋的尺码准确吗？',
      createdAt: '2024-04-22T10:00:00Z'
    },
    {
      id: 2,
      senderId: 4,
      content: '标准的 42 码，放心。',
      createdAt: '2024-04-22T10:05:00Z'
    }
  ]
}

const autoReplies = [
  '好的，我知道了~',
  '让我想想哈',
  '可以的没问题！',
  '明天方便吗？',
  '好嘞~',
  '收到！',
  '嗯嗯',
  '让我确认一下',
  '好的好的',
  '稍等哈'
]

export const mockData = {
  products,
  banners,
  categories,
  notices,
  orders,
  addresses,
  favoriteProductIds,
  conversations,
  messages,
  autoReplies
}
