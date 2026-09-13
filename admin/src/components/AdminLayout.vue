<script setup lang="ts">
import {ref, computed} from 'vue';
import TopNav from './admin/TopNav.vue';
import SideNav from './admin/SideNav.vue';

const isSidebarCollapsed = ref(false);
const sideNavRef = ref<any>(null);

// 切换侧边栏折叠状态
const handleToggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
  if (sideNavRef.value) {
    sideNavRef.value.toggleSidebar();
  }
};

// 计算内容区样式
const mainContentStyle = computed(() => ({
  marginLeft: isSidebarCollapsed.value ? '80px' : '200px',
  width: isSidebarCollapsed.value ? 'calc(100% - 80px)' : 'calc(100% - 200px)'
}));
</script>

<template>
  <div class="admin-layout">
    <TopNav @toggle-sidebar="handleToggleSidebar"/>
    <div class="admin-container">
      <SideNav ref="sideNavRef"/>
      <main class="main-content" :style="mainContentStyle">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="$route.path"/>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.admin-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.admin-container {
  display: flex;
  flex: 1;
  margin-top: 64px; /* 适配顶部导航高度 */
}

.main-content {
  flex: 1;
  padding: 1rem;
  box-sizing: border-box;
  transition: margin-left 0.3s ease, width 0.3s ease; /* 平滑过渡 */
  overflow-x: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    margin-left: 150px !important; /* 与侧边栏展开宽度同步 */
    width: calc(100% - 180px) !important;
  }

  .main-content[style*="margin-left: 80px"] {
    margin-left: 60px !important; /* 与侧边栏折叠宽度同步 */
    width: calc(100% - 60px) !important;
  }
}
</style>
