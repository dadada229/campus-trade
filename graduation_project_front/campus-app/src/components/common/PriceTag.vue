<template>
  <div class="price-tag">
    <span class="price-symbol">¥</span>
    <span class="price-value">{{ formatPrice(price) }}</span>
    <span v-if="originalPrice" class="original-price">¥{{ formatPrice(originalPrice) }}</span>
  </div>
</template>

<script setup>
const props = defineProps({
  price: {
    type: [Number, String],
    required: true
  },
  originalPrice: {
    type: [Number, String],
    default: null
  }
})

const formatPrice = (value) => {
  return Number(value).toFixed(2)
}
</script>

<style scoped>
.price-tag {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-symbol {
  font-size: 14px;
  color: var(--color-primary);
  font-weight: 500;
}

.price-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
}

.original-price {
  font-size: 12px;
  color: var(--color-text-tertiary);
  text-decoration: line-through;
  margin-left: 4px;
}
</style>
