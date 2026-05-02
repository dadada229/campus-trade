import Mock from 'mockjs'

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

export default [
  {
    url: '/api/conversations',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: conversations,
        message: 'success'
      }
    }
  },
  {
    url: '/api/conversations/:id/messages',
    method: 'get',
    response: ({ params }) => {
      const id = parseInt(params.id)
      return {
        code: 200,
        data: messages[id] || [],
        message: 'success'
      }
    }
  },
  {
    url: '/api/conversations/:id/messages',
    method: 'post',
    response: ({ params, body }) => {
      const id = parseInt(params.id)
      const newId = Date.now()
      const newMsg = {
        id: newId,
        senderId: 1,
        content: body.content,
        createdAt: new Date().toISOString()
      }
      if (!messages[id]) {
        messages[id] = []
      }
      messages[id].push(newMsg)
      
      const conv = conversations.find(c => c.id === id)
      if (conv) {
        conv.lastMessage = body.content
        conv.lastMessageAt = new Date().toISOString()
      }
      
      setTimeout(() => {
        const replyId = Date.now() + 1
        const replyContent = autoReplies[Math.floor(Math.random() * autoReplies.length)]
        const replyMsg = {
          id: replyId,
          senderId: conv ? conv.user2Id : 2,
          content: replyContent,
          createdAt: new Date().toISOString()
        }
        if (!messages[id]) {
          messages[id] = []
        }
        messages[id].push(replyMsg)
        if (conv) {
          conv.lastMessage = replyContent
          conv.lastMessageAt = new Date().toISOString()
          conv.unreadCount++
        }
      }, 1000 + Math.random() * 1000)
      
      return {
        code: 200,
        data: newMsg,
        message: 'success'
      }
    }
  },
  {
    url: '/api/conversations',
    method: 'post',
    response: ({ body }) => {
      const productId = body.productId
      const sellerId = body.sellerId
      let conv = conversations.find(c => c.productId === productId && c.user2Id === sellerId)
      if (!conv) {
        const newId = conversations.length > 0 ? Math.max(...conversations.map(c => c.id)) + 1 : 1
        conv = {
          id: newId,
          user1Id: 1,
          user2Id: sellerId,
          user2Name: '卖家',
          user2Avatar: '',
          productId: productId,
          productTitle: '',
          productImage: '',
          lastMessage: '',
          lastMessageAt: new Date().toISOString(),
          unreadCount: 0
        }
        conversations.unshift(conv)
        messages[newId] = []
      }
      return {
        code: 200,
        data: conv,
        message: 'success'
      }
    }
  },
  {
    url: '/api/conversations/:id/read',
    method: 'put',
    response: ({ params }) => {
      const id = parseInt(params.id)
      const conv = conversations.find(c => c.id === id)
      if (conv) {
        conv.unreadCount = 0
      }
      return {
        code: 200,
        data: true,
        message: 'success'
      }
    }
  }
]
