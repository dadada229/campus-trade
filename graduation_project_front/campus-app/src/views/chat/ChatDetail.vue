<template>
  <div class="chat-detail-page">
    <div class="top-bar">
      <el-button class="back-btn" icon="ArrowLeft" circle @click="$router.back()"></el-button>
      <div class="user-info">
        <span class="name">{{ conversation?.user2Name }}</span>
      </div>
      <div class="placeholder"></div>
    </div>
    <div v-if="conversation?.productId" class="product-card" @click="goToProduct">
      <el-image class="product-img" :src="conversation.productImage" fit="cover"></el-image>
      <div class="product-info">
        <div class="product-title">{{ conversation.productTitle }}</div>
        <div class="product-tip">查看商品详情</div>
      </div>
      <el-icon class="arrow"><ArrowRight /></el-icon>
    </div>
    <div ref="messagesContainer" class="messages-container" @scroll="handleScroll">
      <template v-if="loading">
        <el-skeleton :rows="4" animated></el-skeleton>
      </template>
      <template v-else>
        <ChatBubble
          v-for="msg in messages"
          :key="msg.id"
          :message="msg"
          :avatar="conversation?.user2Avatar"
        ></ChatBubble>
      </template>
    </div>
    <div class="input-area">
      <el-input
        v-model="inputContent"
        type="textarea"
        :rows="1"
        autosize
        placeholder="请输入消息..."
        @keyup.enter.prevent="handleSend"
      ></el-input>
      <el-button type="primary" :disabled="!inputContent.trim() || sending" @click="handleSend" :loading="sending">
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
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
  if (!content) {
    return
  }
  if (sending.value) {
    return
  }
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
.top-bar {
  height: var(--navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-lg);
  background: var(--color-card);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 10;
}
.back-btn {
  padding: 0;
}
.user-info {
  flex: 1;
  text-align: center;
}
.name {
  font-size: 17px;
  font-weight: 500;
  color: var(--color-text-primary);
}
.placeholder {
  width: 32px;
}
.product-card {
  display: flex;
  align-items: center;
  margin: var(--spacing-md) var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--color-card);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  cursor: pointer;
}
.product-img {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  flex-shrink: 0;
}
.product-info {
  flex: 1;
  margin-left: var(--spacing-md);
  overflow: hidden;
}
.product-title {
  font-size: 14px;
  color: var(--color-text-primary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-tip {
  font-size: 12px;
  color: var(--color-text-tertiary);
  margin-top: 4px;
}
.arrow {
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md) var(--spacing-lg);
}
.input-area {
  display: flex;
  align-items: flex-end;
  padding: var(--spacing-md);
  background: var(--color-card);
  border-top: 1px solid var(--color-border);
  gap: var(--spacing-md);
}
.input-area :deep(.el-textarea__inner) {
  max-height: 120px;
  resize: none;
  border-radius: var(--radius-input);
}
</style>
