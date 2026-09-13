<template>
  <!-- 简单统计卡片 -->
  <el-card class="stats-card" shadow="hover">
    <div class="stats-card__body">
      <p class="stats-card__title">{{ title }}</p>
      <p class="stats-card__value">{{ value }}</p>
    </div>
  </el-card>
</template>

<script setup lang="ts">
/**
 * 基础统计展示卡片
 * props:
 *  - title: 标题
 *  - value: 数值
 */
import { defineProps } from 'vue'

interface Props {
  title: string
  value: number | string
}

defineProps<Props>()
</script>

<style scoped>
/* 卡片整体 */
.stats-card {
  width: 100%;
  text-align: center;
  background: #24304b;
  border: 1px solid #33405e;
  border-radius: 10px;
  backdrop-filter: blur(4px);
}

/* 内容区域 */
.stats-card__body {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 8px;
}

/* 标题文字 */
.stats-card__title {
  font-size: 14px;
  color: #c0d2f5;
  margin: 0;
}

/* 数值文字 */
.stats-card__value {
  font-size: 32px;
  font-weight: 700;
  margin: 6px 0 0 0;
  line-height: 1;
  color: #e8f0ff;
}
</style>
