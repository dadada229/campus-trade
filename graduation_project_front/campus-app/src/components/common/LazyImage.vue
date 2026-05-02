<template>
  <div class="lazy-image" :style="{ aspectRatio: aspectRatio }">
    <img
      ref="imgRef"
      :src="loaded ? src : placeholder"
      :alt="alt"
      :class="{ 'img-loaded': loaded, 'img-error': hasError }"
      @load="onLoad"
      @error="onError"
    />
    <div v-if="!loaded && !hasError" class="img-placeholder">
      <div class="loading-shimmer"></div>
    </div>
    <div v-if="hasError" class="img-error-state">
      <el-icon :size="28"><Picture /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
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
  aspectRatio: {
    type: String,
    default: '1'
  }
})

const imgRef = ref(null)
const loaded = ref(false)
const hasError = ref(false)
const observer = ref(null)
const placeholder = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9IiNmMGYwZjAiLz48L3N2Zz4='

const onLoad = () => {
  loaded.value = true
  hasError.value = false
}

const onError = () => {
  loaded.value = false
  hasError.value = true
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
  border-radius: inherit;
  background: var(--color-bg);
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

.lazy-image .img-error {
  opacity: 0;
}

.img-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
}

.loading-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    90deg,
    var(--color-bg) 25%,
    var(--color-bg-secondary) 50%,
    var(--color-bg) 75%
  );
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.img-error-state {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary);
  color: var(--color-text-tertiary);
}
</style>
