<template>
  <div class="page-navigation">
    <div class="nav-buttons">
      <div 
        v-for="page in pages" 
        :key="page.value"
        :class="['nav-button', { active: currentPage === page.value }]"
        @click="handlePageChange(page.value)"
      >
        {{ page.label }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Props
const props = defineProps({
  currentPage: {
    type: String,
    default: 'creator'
  }
})

// Emits
const emit = defineEmits(['page-change'])

// 页面配置
const pages = ref([
  { value: 'creator', label: '生成PPT' },
  { value: 'dashboard', label: 'PPT列表' },
  { value: 'customTemplate', label: '自定义模板' }
])

// 方法
const handlePageChange = (page) => {
  if (page !== props.currentPage) {
    emit('page-change', page)
  }
}
</script>

<style scoped>
.page-navigation {
  padding: 10px 0;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.nav-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.nav-button {
  padding: 10px 20px;
  cursor: pointer;
  user-select: none;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  background: #fff;
  color: #333;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-button:hover {
  border-color: #f57bb0;
  color: #f57bb0;
}

.nav-button.active {
  background: linear-gradient(-157deg, #f57bb0, #867dea);
  border-color: transparent;
  color: white;
}
</style> 