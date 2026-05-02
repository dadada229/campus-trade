<template>
  <div class="chat-detail-page">
    <!-- 顶部导航 -->
    <header class="chat-header">
      <div class="header-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </div>
      <div class="header-title">
        <span class="title-name">{{ conversation?.user2Name || '聊天' }}</span>
      </div>
      <div class="header-placeholder"></div>
    </header>

    <!-- 商品卡片 -->
    <div v-if="conversation?.productId" class="product-card" @click="goToProduct">
      <el-image class="product-img" :src="conversation.productImage" fit="cover"></el-image>
      <div class="product-info">
        <div class="product-title">{{ conversation.productTitle }}</div>
        <div class="product-action">
          <span>查看商品</span>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 18l6-6-6-6"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- 消息区域 -->
    <div ref="messagesContainer" class="messages-container" @scroll="handleScroll">
      <template v-if="loading">
        <div class="loading-messages">
          <div class="loading-spinner"></div>
          <span>加载消息中...</span>
        </div>
      </template>
      <template v-else>
        <div v-if="messages.length === 0" class="empty-messages">
          <p>还没有消息，快来打个招呼吧</p>
        </div>
        <ChatBubble
          v-for="msg in messages"
          :key="msg.id"
          :message="msg"
          :avatar="conversation?.user2Avatar"
        />
      </template>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputContent"
          type="textarea"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="请输入消息..."
          resize="none"
          @keydown.enter.exact.prevent="handleSend"
        />
      </div>
      <button 
        class="send-btn" 
        :class="{ active: inputContent.trim() && !sending }"
        :disabled="!inputContent.trim() || sending"
        @click="handleSend"
      >
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ChatBubble from '@/components/business/ChatBubble.vue'
import { useChatStore } from '@/stores/chat'
import { getMessages, markAsRead, sendMessage as sendMessageApi } from '@/api/modules/chat'
import { wsManager } from '@/utils/websocket'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()

const messagesContainer = ref(null)
const loading = ref(true)
const messages = ref([])
const conversation = ref(null)
const inputContent = ref('')
const sending = ref(false)

const loadMessages = async () => {
  loading.value = true
  try {
    const res = await getMessages(parseInt(route.params.id))
    if (res.code === 200) {
      messages.value = res.data
      chatStore.setMessages(res.data)
    }
  } catch (e) {
    console.error('加载消息列表失败:', e)
    ElMessage.error('加载消息失败')
  } finally {
    loading.value = false
    nextTick(() => scrollToBottom())
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const handleScroll = () => {}

const handleSend = async () => {
  const content = inputContent.value.trim()
  if (!content || sending.value) return
  
  const conversationId = parseInt(route.params.id)
  const originalContent = content
  inputContent.value = ''
  sending.value = true
  
  try {
    const res = await sendMessageApi(conversationId, originalContent)
    if (res.code !== 200) {
      ElMessage.error(res.message || '发送失败')
      inputContent.value = originalContent
    }
  } catch (e) {
    console.error('发送异常:', e)
    ElMessage.error('发送失败')
    inputContent.value = originalContent
  } finally {
    sending.value = false
  }
}

const handleNewMessage = (msg) => {
  if (msg.conversationId === parseInt(route.params.id)) {
    const exists = messages.value.find(m => m.id === msg.id)
    if (!exists) {
      messages.value.push(msg)
      nextTick(() => scrollToBottom())
    }
  }
  chatStore.addMessage(msg)
}

const goToProduct = () => {
  if (conversation.value?.productId) {
    router.push(`/product/${conversation.value.productId}`)
  }
}

onMounted(async () => {
  conversation.value = chatStore.currentConversation || chatStore.conversations.find(c => c.id === parseInt(route.params.id))
  await loadMessages()
  await markAsRead(parseInt(route.params.id))
  chatStore.clearUnreadCount(parseInt(route.params.id))
  wsManager.on('new_message', handleNewMessage)
  if (!wsManager.ws || wsManager.ws.readyState !== WebSocket.OPEN) {
    wsManager.connect()
  }
})

onUnmounted(() => {
  wsManager.off('new_message', handleNewMessage)
})
</script>

<style scoped>
.chat-detail-page {
  min-height: 100vh;
  background: var(--color-bg);
  display: flex;
  flex-direction: column;
}

/* 顶部导航 */
.chat-header {
  height: var(--navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: var(--shadow-xs);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-circle);
  cursor: pointer;
  transition: all var(--duration-fast);
  margin-left: -8px;
}

.header-btn:hover {
  background: var(--color-bg);
}

.header-btn:active {
  transform: scale(0.92);
}

.header-btn svg {
  width: 22px;
  height: 22px;
  color: var(--color-text-primary);
}

.header-title {
  flex: 1;
  text-align: center;
}

.title-name {
  font-size: var(--font-size-body);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.header-placeholder {
  width: 36px;
}

/* 商品卡片 */
.product-card {
  display: flex;
  align-items: center;
  margin: var(--spacing-md) var(--padding-page);
  padding: var(--spacing-md);
  background: var(--color-card);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.product-card:active {
  transform: scale(0.99);
}

.product-img {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  margin-left: var(--spacing-md);
  overflow: hidden;
}

.product-title {
  font-size: var(--font-size-body);
  color: var(--color-text-primary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.product-action {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: var(--font-size-small);
  color: var(--color-primary);
}

.product-action svg {
  width: 14px;
  height: 14px;
}

/* 消息区域 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg) var(--padding-page);
  padding-bottom: 100px;
}

.loading-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxxl);
  gap: var(--spacing-md);
  color: var(--color-text-tertiary);
  font-size: var(--font-size-small);
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-messages {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxxl);
}

.empty-messages p {
  font-size: var(--font-size-small);
  color: var(--color-text-tertiary);
  margin: 0;
}

/* 输入区域 */
.input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: flex-end;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--padding-page);
  padding-bottom: calc(var(--spacing-md) + env(safe-area-inset-bottom));
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid var(--color-divider);
  z-index: 100;
}

.input-wrapper {
  flex: 1;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: var(--radius-xl) !important;
  padding: var(--spacing-md) var(--spacing-lg) !important;
  font-size: var(--font-size-body) !important;
  line-height: 1.5 !important;
  max-height: 120px;
  resize: none;
  border: 1.5px solid var(--color-border) !important;
  box-shadow: none !important;
  transition: all var(--duration-fast) !important;
}

.input-wrapper :deep(.el-textarea__inner:focus) {
  border-color: var(--color-primary) !important;
}

.send-btn {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-border);
  border: none;
  border-radius: var(--radius-circle);
  cursor: not-allowed;
  transition: all var(--duration-fast);
  flex-shrink: 0;
}

.send-btn.active {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(61, 154, 139, 0.3);
}

.send-btn.active:active {
  transform: scale(0.95);
}

.send-btn svg {
  width: 20px;
  height: 20px;
  color: #fff;
}
</style>
