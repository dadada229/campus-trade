<template>
  <div :class="['bubble-wrapper', { 'is-mine': isMine }]">
    <div v-if="!isMine" class="avatar">
      <el-avatar :size="36" :src="avatar" />
    </div>
    <div class="bubble">
      <div class="content">{{ content }}</div>
      <div class="time">{{ formatTime(createdAt) }}</div>
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
  margin-bottom: 16px;
}
.bubble-wrapper.is-mine {
  justify-content: flex-end;
}
.avatar {
  margin-right: 12px;
}
.bubble-wrapper.is-mine .avatar {
  display: none;
}
.bubble {
  max-width: 70%;
}
.bubble .content {
  padding: 10px 14px;
  border-radius: 16px;
  background: #f2f3f5;
  color: #1f2329;
  word-break: break-word;
  line-height: 1.5;
}
.bubble-wrapper.is-mine .content {
  background: var(--color-primary);
  color: #fff;
}
.bubble .time {
  margin-top: 4px;
  font-size: 12px;
  color: var(--color-text-tertiary);
  text-align: left;
}
.bubble-wrapper.is-mine .time {
  text-align: right;
}
</style>
