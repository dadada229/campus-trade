import { storage } from './storage'

class WebSocketManager {
  constructor() {
    this.ws = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
    this.messageHandlers = {}
    this.isManualClose = false
  }

  connect() {
    const token = storage.getToken()
    if (!token) {
      console.warn('No token found, skipping WebSocket connection')
      return
    }
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const host = window.location.hostname
    const port = 8080
    const url = `${protocol}//${host}:${port}/ws/chat?token=${token}`
    try {
      this.ws = new WebSocket(url)
      this.ws.onopen = () => {
        this.reconnectAttempts = 0
        this.triggerHandler('connected')
      }
      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          this.handleMessage(message)
        } catch (e) {
          console.error('Failed to parse message:', e)
        }
      }
      this.ws.onclose = (event) => {
        this.triggerHandler('disconnected')
        if (!this.isManualClose) {
          this.reconnect()
        }
      }
      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error)
        this.triggerHandler('error', error)
      }
    } catch (e) {
      console.error('Failed to create WebSocket:', e)
    }
  }

  disconnect() {
    this.isManualClose = true
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      setTimeout(() => {
        this.connect()
      }, this.reconnectDelay)
    } else {
      console.error('Max reconnect attempts reached')
    }
  }

  sendMessage(type, data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = { type, data }
      this.ws.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket is not connected')
    }
  }

  sendChatMessage(conversationId, content) {
    this.sendMessage('send_message', { conversationId, content })
  }

  handleMessage(message) {
    const { type, data } = message
    if (this.messageHandlers[type]) {
      this.triggerHandler(type, data)
    }
    this.triggerHandler('message', message)
  }

  on(event, handler) {
    if (!this.messageHandlers[event]) {
      this.messageHandlers[event] = []
    }
    this.messageHandlers[event].push(handler)
  }

  off(event, handler) {
    if (this.messageHandlers[event]) {
      if (handler) {
        this.messageHandlers[event] = this.messageHandlers[event].filter(h => h !== handler)
      } else {
        delete this.messageHandlers[event]
      }
    }
  }

  triggerHandler(event, data) {
    if (this.messageHandlers[event]) {
      this.messageHandlers[event].forEach(handler => {
        try {
          handler(data)
        } catch (e) {
          console.error(`WebSocket handler error for ${event}:`, e)
        }
      })
    }
  }
}

export const wsManager = new WebSocketManager()
