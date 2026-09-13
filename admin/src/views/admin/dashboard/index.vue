<template>
  <div class="dashboard-container">
    <DashboardView />
  </div>
</template>

<script setup>
import DashboardView from '@/components/dashboard/DashboardView.vue';
</script>

<style scoped>
/* 整体背景采用深蓝，降低视觉干扰 */
.dashboard-container {
  padding: 20px;
  background-color: #131b28;
  min-height: 100vh;
}

.dashboard-title {
  margin-bottom: 20px;
  color: #303133;
}

.data-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #c0d2f5;
  margin-bottom: 10px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #e8f0ff;
  margin: 10px 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 14px;
}

.text-success {
  color: #67C23A;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.chart {
  height: 100%;
}
</style>
