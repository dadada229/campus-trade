<template>
  <div class="lazy-image" :style="{ aspectRatio: aspectRatio }">
    <img
      ref="imgRef"
      :src="loaded ? src : placeholder"
      :alt="alt"
      :class="{ 'img-loaded': loaded }"
      @load="onLoad"
      @error="onError"
    />
    <div v-if="!loaded" class="img-placeholder">
      <el-icon class="placeholder-icon"><Picture /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  width: {
    type: [Number, String],
    default: '100%'
  },
  height: {
    type: [Number, String],
    default: '100%'
  },
  aspectRatio: {
    type: String,
    default: '1'
  }
})

const imgRef = ref(null)
const loaded = ref(false)
const observer = ref(null)
const placeholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9IiNlYWVhZWEiLz48L3N2Zz4='

const onLoad = () => {
  loaded.value = true
}

const onError = () => {
  loaded.value = false
}

onMounted(() => {
  if (!('IntersectionObserver' in window) || !imgRef.value) {
    loaded.value = true
    return
  }

  observer.value = new IntersectionObserver((entries) => {
    for (const entry of entries) {
      if (entry.isIntersecting) {
        loaded.value = true
        observer.value.unobserve(entry.target)
      }
    }
  }, {
    rootMargin: '100px 0px'
  })

  observer.value.observe(imgRef.value)
})

onUnmounted(() => {
  if (observer.value) {
    observer.value.disconnect()
  }
})
</script>

<style scoped>
.lazy-image {
  position: relative;
  width: 100%;
  overflow: hidden;
  border-radius: var(--radius-card, 12px);
}

.lazy-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lazy-image .img-loaded {
  opacity: 1;
}

.img-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
}

.placeholder-icon {
  font-size: 32px;
  color: var(--color-text-tertiary);
}
</style>
