<script setup lang="ts">
import {ref} from 'vue';

const isSidebarCollapsed = ref(false);
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

defineExpose({
  toggleSidebar
});
</script>

<template>
  <aside class="admin-sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
    <nav>
      <ul>
        <!--        <li><router-link to="/admin" class="nav-item"><span class="nav-icon">📊</span> <span class="nav-text">数据概览</span></router-link></li>-->
        <!--        <li><router-link to="/admin" class="nav-item"><span class="nav-icon">📊</span> <span class="nav-text">数据概览</span></router-link></li>-->
        <li>
          <router-link to="/admin/ai-chat" class="nav-item"><span class="nav-icon">🤖</span> <span
              class="nav-text">智能体辅助</span></router-link>
        </li>
        <li>
          <router-link to="/admin/warning" class="nav-item"><img class="my-svg" src="@/assets/svg/warning.svg"/><span
              class="nav-text">心理预警</span></router-link>
        </li>
        <li>
          <router-link to="/admin/students" class="nav-item"><span class="nav-icon">👥</span> <span class="nav-text">学生信息管理</span>
          </router-link>
        </li>
        <li>
          <router-link to="/admin/chat" class="nav-item"><span class="nav-icon">💬</span> <span
              class="nav-text">与学生沟通</span></router-link>
        </li>
        <li>
          <router-link to="/admin/knowledge" class="nav-item"><span class="nav-icon">📚</span> <span class="nav-text">发布科普知识</span>
          </router-link>
        </li>
        <li>
          <router-link to="/admin/appointment" class="nav-item"><span class="nav-icon">📞</span><span
              class="nav-text">预约信息管理</span></router-link>
        </li>
        <li>
          <router-link to="/admin/sports" class="nav-item"><span class="nav-icon">🏃</span> <span
              class="nav-text">发布运动视频</span></router-link>
        </li>
        <li>
          <router-link to="/admin/configuration-index" class="nav-item"><img class="my-svg" src="@/assets/svg/digital-human.svg"/><span
              class="nav-text">数字人配置</span></router-link>
        </li>
        <li>
          <router-link to="/admin/ai-ppt" class="nav-item"><span class="nav-icon">📊</span><span
              class="nav-text">AI PPT生成器</span></router-link>
        </li>

      </ul>
    </nav>
  </aside>
</template>

<style scoped>
:root {
  --neon-cyan: #00ffcc;
  --neon-pink: #ff007f;
  --neon-purple: #9d00ff;
  --bg-dark: #0a0e1a;
  --bg-gradient: linear-gradient(180deg, #0a0e1a 0%, #1c2526 100%);
  --glass-bg: rgba(255, 255, 255, 0.05);
  --glow-shadow: 0 0 15px rgba(0, 255, 204, 0.5);
  --text-primary: #e0e7ff;
}

.my-svg{
  height: 30px;
  width: 30px;
}

/* Sidebar container */
.admin-sidebar {
  margin-top: 50px;
  width: 220px;
  background: var(--bg-gradient);
  color: var(--text-primary);
  padding: 1.5rem 0;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 900;
  overflow-y: auto;
  transition: width 0.3s ease, transform 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.5);
}

/* Collapsed state */
.admin-sidebar.collapsed {
  width: 80px;
}

/* Navigation list */
.admin-sidebar nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* Navigation item */
.nav-item {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  color: var(--text-primary);
  text-decoration: none;
  transition: all 0.3s ease;
  gap: 1.2rem;
  position: relative;
  border-left: 3px solid transparent;
  background: transparent;
  overflow: hidden;
}

/* Navigation icon */
.nav-icon {
  font-size: 1.5rem;
  min-width: 1.8rem;
  text-align: center;
  transition: transform 0.3s ease, filter 0.3s ease;
  filter: drop-shadow(0 0 5px var(--neon-cyan));
}

/* Collapsed state - hide text */
.collapsed .nav-text {
  display: none;
}

/* Hover effect */
.nav-item:hover {
  background: var(--glass-bg);
  border-left: 3px solid var(--neon-cyan);
  transform: translateX(5px);
  box-shadow: var(--glow-shadow);
  color: var(--neon-cyan);
}

.nav-item:hover .nav-icon {
  transform: scale(1.2);
  filter: drop-shadow(0 0 8px var(--neon-cyan));
}

/* Active state */
.nav-item.active {
  background: linear-gradient(90deg, rgba(0, 255, 204, 0.2), transparent);
  color: var(--neon-pink);
  font-weight: 600;
  border-left: 3px solid var(--neon-pink);
}

/* Dynamic neon bar on hover */
.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 0;
  background: var(--neon-cyan);
  transition: width 0.3s ease;
  z-index: -1;
}

.nav-item:hover::before {
  width: 100%;
  opacity: 0.2;
}

/* Breathing glow effect for sidebar */
.admin-sidebar::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top left, rgba(0, 255, 204, 0.15), transparent 70%);
  opacity: 0.5;
  animation: neonPulse 5s infinite;
  pointer-events: none;
}

/* Neon pulse animation */
@keyframes neonPulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

/* Particle-like background effect */
.admin-sidebar::after {
  content: '';
  position: absolute;
  inset: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="10" cy="10" r="1" fill="rgba(0,255,204,0.3)"/><circle cx="90" cy="90" r="1" fill="rgba(255,0,127,0.3)"/><circle cx="50" cy="20" r="0.8" fill="rgba(157,0,255,0.3)"/></svg>');
  background-size: 50px;
  opacity: 0.1;
  animation: particleDrift 20s linear infinite;
  pointer-events: none;
}

/* Particle drift animation */
@keyframes particleDrift {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 100px 100px;
  }
}

/* Scrollbar styling */
.admin-sidebar::-webkit-scrollbar {
  width: 6px;
}

.admin-sidebar::-webkit-scrollbar-thumb {
  background: var(--neon-cyan);
  border-radius: 3px;
}

.admin-sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

/* Responsive design */
@media (max-width: 768px) {
  .admin-sidebar {
    width: 180px;
  }

  .admin-sidebar.collapsed {
    width: 60px;
  }

  .nav-item {
    padding: 0.8rem 1rem;
  }

  .nav-icon {
    font-size: 1.3rem;
  }
}

/* High-resolution display optimization */
@media (min-resolution: 2dppx) {
  .admin-sidebar {
    box-shadow: 4px 0 30px rgba(0, 0, 0, 0.6);
  }
}
</style>
