<script setup lang="ts">
import { ref } from 'vue';
// import RecordList from './components/RecordList.vue';
// import WarningList from './components/WarningList.vue';
import WarningStudentList from './components/WarningStudentList.vue';
import { BellFilled } from '@element-plus/icons-vue';

const activeTab = ref('record');
</script>

<template>
  <div class="warning-page">
    <div class="title-bar">
      <div class="left">
        <el-icon class="icon"><BellFilled /></el-icon>
        <span class="title">心理预警与检测记录</span>
      </div>
      <div class="desc">每天凌晨 2:00 自动执行心理预警</div>
    </div>
    <!-- <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane name="record" label="情绪检测记录">
        <RecordList ref="recordRef" />
      </el-tab-pane>
      <el-tab-pane name="warning" label="心理预警">
        <WarningList ref="warningRef" />
      </el-tab-pane>
    </el-tabs> -->
    <WarningStudentList ref="warningRef" />
  </div>
</template>

<style scoped>
.warning-page {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
}

.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  background: #fff;
  padding: 12px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: 16px;
}

.left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon {
  font-size: 20px;
  color: #409eff;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.desc {
  font-size: 14px;
  color: #909399;
}
</style>