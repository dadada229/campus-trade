<template>
  <AppLayout>
    <div class="chat-list-page">
      <template v-if="loading">
        <el-skeleton :rows="5" animated />
      </template>
      <template v-else-if="conversations.length === 0">
        <el-empty description="暂无消息" />
      </template>
      <template v-else>
        <div class="conversation-list">
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conversation-item"
            @click="goToDetail(conv)"
          >
            <div class="avatar-wrapper">
              <el-avatar :size="52" :src="conv.user2Avatar" />
              <div v-if="conv.unreadCount > 0" class="badge">
                {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
              </div>
            </div>
            <div class="info">
              <div class="top">
                <span class="name">{{ conv.user2Name }}</span>
                <span class="time">{{ formatTime(conv.lastMessageAt) }}</span>
              </div>
              <div class="bottom">
                <span class="last-message">{{ conv.lastMessage || '暂无消息' }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/common/AppLayout.vue'
import { useChatStore } from '@/stores/chat'
import { getConversations } from '@/api/modules/chat'
import { wsManager } from '@/utils/websocket'

const router = useRouter()
const chatStore = useChatStore()

const loading = ref(true)
const conversations = ref([])

const handleNewMessage = (msg) => {
  const conv = conversations.value.find(c => c.id === msg.conversationId)
  if (conv) {
    conv.lastMessage = msg.content
    conv.lastMessageAt = msg.createdAt
    if (!chatStore.currentConversation || chatStore.currentConversation.id !== msg.conversationId) {
      conv.unreadCount = (conv.unreadCount || 0) + 1
    }
  }
}

const loadConversations = async () => {
  loading.value = true
  try {
    const res = await getConversations()
    if (res.code === 200) {
      conversations.value = res.data
      chatStore.setConversations(res.data)
    }
  } finally {
    loading.value = false
  }
}

const goToDetail = (conv) => {
  chatStore.setCurrentConversation(conv)
  router.push(`/chat/${conv.id}`)
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const oneDay = 24 * 60 * 60 * 1000
  const oneWeek = oneDay * 7

  if (diff < oneDay && date.getDate() === now.getDate()) {
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  } else if (diff < oneDay * 2) {
    return '昨天'
  } else if (diff < oneWeek) {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return weekdays[date.getDay()]
  } else {
    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${month}/${day}`
  }
}

onMounted(() => {
  if (!wsManager.ws || wsManager.ws.readyState !== WebSocket.OPEN) {
    wsManager.connect()
  }
  wsManager.on('new_message', handleNewMessage)
  loadConversations()
})

onUnmounted(() => {
  wsManager.off('new_message', handleNewMessage)
})
</script>

<style scoped>
.chat-list-page {
  padding: var(--spacing-lg);
}
.conversation-list {
  margin-top: var(--spacing-md);
}
.conversation-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--color-border);
  cursor: pointer;
  transition: background var(--duration-fast);
}
.conversation-item:hover {
  background: rgba(0,0,0,0.02);
}
.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}
.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--color-danger);
  color: #fff;
  font-size: 11px;
  line-height: 18px;
  text-align: center;
}
.info {
  flex: 1;
  margin-left: var(--spacing-md);
  overflow: hidden;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.name {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-primary);
}
.time {
  font-size: 12px;
  color: var(--color-text-tertiary);
}
.bottom {
  overflow: hidden;
}
.last-message {
  display: block;
  font-size: 14px;
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
