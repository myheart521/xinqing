<template>
  <div id="app">
    <router-view :key="$route.fullPath"></router-view>
    <!-- 数字人组件 - 放在路由视图外部，不受路由切换影响 -->
    <DigitalHumanAvatar />
  </div>
</template>

<script setup lang="ts">
import DigitalHumanAvatar from './components/DigitalHumanAvatar.vue';
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
  margin-top: 64px;
}
</style>

<style scoped>
.dashboard-container {
  min-height: 100%;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #333333;
  overflow: hidden;
}

.dashboard-header {
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.login-btn {
  background: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: #096dd9;
  transform: translateY(-2px);
}

.overview-card {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  min-width: 180px;
  margin: 0 0.5rem;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(24, 144, 255, 0.3);
}

.data-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 0.5rem 0;
  background: linear-gradient(45deg, #1890ff, #69c0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.header-actions {
  display: flex;
  gap: 1rem;
}

.login-btn:hover {
  background: #c41d7f;
  transform: translateY(-2px);
}

.dashboard-main {
  padding: 2rem;
}

.overview-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 1rem;
  padding: 0 1rem;
}

.overview-card {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
  min-width: 180px;
  margin: 0 0.5rem;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 133, 192, 0.3);
}

.overview-card h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333333;
  opacity: 0.8;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 1.5rem;
  max-width: 1800px;
  margin: 0 auto;
  padding: 0 1rem;
  height: calc(100vh - 200px);
}

.map-container {
  grid-column: 2;
  grid-row: 1 / span 2;
  height: 100%;
  min-height: 600px;
}

.wide-card {
  grid-column: 1;
  margin-top: 0;
}

.chart-container {
  width: 100%;
  height: 100%;
  min-height: 260px;
}

.data-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 0.5rem 0;
  background: linear-gradient(45deg, #eb2f96, #f759ab);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
