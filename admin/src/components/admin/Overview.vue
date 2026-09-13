<script setup lang="ts">
import { ref } from 'vue';

const statistics = ref({
  totalConsultations: 3147,
  activeUsers: 1892,
  satisfaction: '98%'
});
</script>

<template>
  <section class="overview-section">
    <div class="overview-card">
      <h3>总咨询量</h3>
      <div class="data-value">{{ statistics.totalConsultations }}</div>
    </div>
    <div class="overview-card">
      <h3>本月活跃用户</h3>
      <div class="data-value">{{ statistics.activeUsers }}</div>
    </div>
    <div class="overview-card">
      <h3>满意度</h3>
      <div class="data-value">{{ statistics.satisfaction }}</div>
    </div>
  </section>
</template>

<style scoped>
.overview-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 2rem;
  gap: 1rem;
}

.overview-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  flex: 1;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}

.overview-card h3 {
  color: #666;
  margin: 0 0 1rem 0;
  font-size: 1rem;
}

.data-value {
  font-size: 2rem;
  font-weight: bold;
  color: #1890ff;
  margin: 0.5rem 0;
}
</style>