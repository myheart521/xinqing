<script setup lang="ts">
</script>

<template>
  <main class="admin-main">
    <div class="admin-content">
      <slot></slot>
    </div>
  </main>
</template>

<style scoped>
.admin-main {
  flex: 1;
  padding: 2rem;
  padding-top: calc(64px + 2rem);
  background-color: #f0f2f5;
  transition: all 0.3s;
  margin-top: -64px;
}

.admin-content {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>