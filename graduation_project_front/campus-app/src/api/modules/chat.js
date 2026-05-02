import request from '../request'
import { wsManager } from '../../utils/websocket'

export function getConversations() {
  return request.get('/conversations')
}

export function getMessages(conversationId) {
  return request.get(`/conversations/${conversationId}/messages`)
}

export function sendMessage(conversationId, content) {
  return request.post(`/conversations/${conversationId}/messages`, { content })
}

export function createOrGetConversation(productId, sellerId) {
  return request.post('/conversations', { productId, sellerId })
}

export function markAsRead(conversationId) {
  return request.put(`/conversations/${conversationId}/read`)
}

export function initWebSocket() {
  wsManager.connect()
}

export function disconnectWebSocket() {
  wsManager.disconnect()
}
