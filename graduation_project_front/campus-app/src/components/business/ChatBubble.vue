<template>
  <div :class="['bubble-wrapper', { 'is-mine': isMine }]">
    <div v-if="!isMine" class="avatar-wrapper">
      <img v-if="avatar" :src="avatar" class="avatar" alt="" />
      <div v-else class="avatar-placeholder">
        <svg viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
        </svg>
      </div>
    </div>
    <div class="bubble">
      <div class="bubble-content">{{ content }}</div>
      <div class="bubble-time">{{ formatTime(createdAt) }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  message: {
    type: Object,
    required: true
  },
  avatar: {
    type: String,
    default: ''
  }
})

const userStore = useUserStore()

const isMine = computed(() => props.message.senderId === userStore.userInfo?.id)
const content = computed(() => props.message.content)
const createdAt = computed(() => props.message.createdAt)

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}
</script>

<style scoped>
.bubble-wrapper {
  display: flex;
  align-items: flex-start;
  margin-bottom: var(--spacing-lg);
  gap: var(--spacing-sm);
}

.bubble-wrapper.is-mine {
  flex-direction: row-reverse;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-circle);
  object-fit: cover;
}

.avatar-placeholder {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-circle);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder svg {
  width: 20px;
  height: 20px;
  color: #fff;
}

.bubble {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.bubble-content {
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-body);
  line-height: 1.5;
  word-break: break-word;
  background: var(--color-card);
  color: var(--color-text-primary);
  box-shadow: var(--shadow-sm);
}

.bubble-wrapper.is-mine .bubble-content {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(61, 154, 139, 0.2);
}

/* 气泡尖角效果 */
.bubble-content {
  position: relative;
}

.bubble-wrapper:not(.is-mine) .bubble-content {
  border-top-left-radius: var(--radius-xs);
}

.bubble-wrapper.is-mine .bubble-content {
  border-top-right-radius: var(--radius-xs);
}

.bubble-time {
  margin-top: var(--spacing-xs);
  font-size: var(--font-size-mini);
  color: var(--color-text-quaternary);
}

.bubble-wrapper.is-mine .bubble-time {
  text-align: right;
}
</style>
