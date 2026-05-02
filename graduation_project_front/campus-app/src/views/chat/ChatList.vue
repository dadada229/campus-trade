<template>
  <AppLayout>
    <div class="chat-list-page">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">消息</h1>
      </div>

      <!-- 加载状态 -->
      <template v-if="loading">
        <div class="skeleton-list">
          <div v-for="i in 5" :key="i" class="skeleton-item">
            <div class="skeleton-avatar skeleton"></div>
            <div class="skeleton-content">
              <div class="skeleton-name skeleton"></div>
              <div class="skeleton-message skeleton"></div>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <template v-else-if="conversations.length === 0">
        <div class="empty-state">
          <div class="empty-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
          </div>
          <p class="empty-text">暂无消息</p>
          <p class="empty-hint">去逛逛感兴趣的宝贝吧</p>
        </div>
      </template>

      <!-- 会话列表 -->
      <template v-else>
        <div class="conversation-list">
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conversation-item"
            @click="goToDetail(conv)"
          >
            <div class="avatar-wrapper">
              <img 
                v-if="conv.user2Avatar" 
                :src="conv.user2Avatar" 
                class="avatar-img" 
                alt=""
              />
              <div v-else class="avatar-placeholder">
                {{ (conv.user2Name || '用户').charAt(0) }}
              </div>
              <div v-if="conv.unreadCount > 0" class="unread-badge">
                {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
              </div>
            </div>
            <div class="conv-content">
              <div class="conv-header">
                <span class="conv-name">{{ conv.user2Name || '校园用户' }}</span>
                <span class="conv-time">{{ formatTime(conv.lastMessageAt) }}</span>
              </div>
              <div class="conv-message">
                <span class="message-text">{{ conv.lastMessage || '暂无消息' }}</span>
              </div>
            </div>
            <div class="conv-arrow">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 18l6-6-6-6"/>
              </svg>
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
  min-height: 100vh;
  background-color: var(--color-bg);
  padding-bottom: calc(var(--tabbar-height) + var(--safe-area-bottom) + var(--spacing-xl));
}

/* 页面头部 */
.page-header {
  background: var(--color-card);
  padding: var(--spacing-lg) var(--padding-page);
  box-shadow: var(--shadow-xs);
}

.page-title {
  font-size: var(--font-size-h2);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0;
}

/* 骨架屏 */
.skeleton-list {
  background: var(--color-card);
  margin-top: var(--spacing-sm);
}

.skeleton-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) var(--padding-page);
}

.skeleton-avatar {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-circle);
  flex-shrink: 0;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.skeleton-name {
  width: 80px;
  height: 18px;
}

.skeleton-message {
  width: 60%;
  height: 16px;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xxxl) var(--spacing-xl);
}

.empty-icon {
  width: 80px;
  height: 80px;
  color: var(--color-text-quaternary);
  margin-bottom: var(--spacing-lg);
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-text {
  font-size: var(--font-size-body);
  color: var(--color-text-secondary);
  margin: 0 0 var(--spacing-xs) 0;
}

.empty-hint {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  margin: 0;
}

/* 会话列表 */
.conversation-list {
  background: var(--color-card);
  margin-top: var(--spacing-sm);
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg) var(--padding-page);
  cursor: pointer;
  transition: all var(--duration-fast);
  border-bottom: 1px solid var(--color-divider);
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: var(--color-bg);
}

.conversation-item:active {
  background: var(--color-border-light);
}

/* 头像 */
.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-img {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-circle);
  object-fit: cover;
}

.avatar-placeholder {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-circle);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: var(--font-weight-semibold);
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  border-radius: 10px;
  background: var(--color-danger);
  color: #fff;
  font-size: 11px;
  font-weight: var(--font-weight-semibold);
  line-height: 20px;
  text-align: center;
  border: 2px solid var(--color-card);
}

/* 内容区域 */
.conv-content {
  flex: 1;
  min-width: 0;
}

.conv-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xs);
}

.conv-name {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.conv-time {
  font-size: var(--font-size-mini);
  color: var(--color-text-tertiary);
}

.conv-message {
  display: flex;
  align-items: center;
}

.message-text {
  font-size: var(--font-size-small);
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.conv-arrow {
  width: 18px;
  height: 18px;
  color: var(--color-text-quaternary);
  flex-shrink: 0;
}

.conv-arrow svg {
  width: 100%;
  height: 100%;
}
</style>
