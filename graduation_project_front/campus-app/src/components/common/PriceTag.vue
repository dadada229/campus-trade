<template>
  <div class="price-tag" :class="[size, { highlight }]">
    <span class="price-symbol">¥</span>
    <span class="price-integer">{{ integerPart }}</span>
    <span v-if="showDecimal && decimalPart" class="price-decimal">.{{ decimalPart }}</span>
    <span v-if="originalPrice" class="original-price">¥{{ formatPrice(originalPrice) }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  price: {
    type: [Number, String],
    required: true
  },
  originalPrice: {
    type: [Number, String],
    default: null
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  showDecimal: {
    type: Boolean,
    default: true
  },
  highlight: {
    type: Boolean,
    default: false
  }
})

const formatPrice = (value) => {
  return Number(value).toFixed(2)
}

const integerPart = computed(() => {
  const num = parseFloat(props.price) || 0
  return Math.floor(num).toString()
})

const decimalPart = computed(() => {
  const num = parseFloat(props.price) || 0
  const decimal = (num % 1).toFixed(2).substring(2)
  return decimal === '00' ? '' : decimal
})
</script>

<style scoped>
.price-tag {
  display: inline-flex;
  align-items: baseline;
  font-weight: 700;
  color: var(--color-primary);
  font-family: var(--font-family-sans);
}

.price-tag.highlight {
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Small size */
.price-tag.small .price-symbol {
  font-size: 12px;
}

.price-tag.small .price-integer {
  font-size: 16px;
}

.price-tag.small .price-decimal {
  font-size: 12px;
}

.price-tag.small .original-price {
  font-size: 11px;
}

/* Medium size (default) */
.price-tag.medium .price-symbol {
  font-size: 14px;
}

.price-tag.medium .price-integer {
  font-size: 20px;
}

.price-tag.medium .price-decimal {
  font-size: 14px;
}

.price-tag.medium .original-price {
  font-size: 12px;
}

/* Large size */
.price-tag.large .price-symbol {
  font-size: 18px;
}

.price-tag.large .price-integer {
  font-size: 28px;
}

.price-tag.large .price-decimal {
  font-size: 16px;
}

.price-tag.large .original-price {
  font-size: 14px;
}

.price-symbol {
  margin-right: 1px;
}

.price-decimal {
  margin-left: 1px;
}

.original-price {
  color: var(--color-text-tertiary);
  text-decoration: line-through;
  margin-left: var(--spacing-sm);
  font-weight: 400;
}
</style>
