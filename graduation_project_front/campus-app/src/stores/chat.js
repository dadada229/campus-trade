import { defineStore } from 'pinia'
import { ref } from 'vue'
import { wsManager } from '../utils/websocket'

export const useChatStore = defineStore('chat', () => {
  const conversations = ref([])
  const currentConversation = ref(null)
  const messages = ref([])

  const setConversations = (list) => {
    conversations.value = list
  }

  const setCurrentConversation = (conv) => {
    currentConversation.value = conv
  }

  const setMessages = (msgs) => {
    messages.value = msgs
  }

  const addMessage = (msg) => {
    const conv = conversations.value.find(c => c.id === msg.conversationId)
    if (conv) {
      conv.lastMessage = msg.content
      conv.lastMessageAt = msg.createdAt
      if (!currentConversation.value || currentConversation.value.id !== msg.conversationId) {
        conv.unreadCount = (conv.unreadCount || 0) + 1
      }
    }
  }

  const updateUnreadCount = (conversationId, count) => {
    const conv = conversations.value.find(c => c.id === conversationId)
    if (conv) {
      conv.unreadCount = count
    }
  }

  const clearUnreadCount = (conversationId) => {
    const conv = conversations.value.find(c => c.id === conversationId)
    if (conv) {
      conv.unreadCount = 0
    }
  }

  const setupWebSocketHandlers = () => {
    wsManager.on('new_message', (msg) => {
      addMessage(msg)
    })
  }

  return {
    conversations,
    currentConversation,
    messages,
    setConversations,
    setCurrentConversation,
    setMessages,
    addMessage,
    updateUnreadCount,
    clearUnreadCount,
    setupWebSocketHandlers
  }
})
