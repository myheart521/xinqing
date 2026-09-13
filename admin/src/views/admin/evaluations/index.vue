<template>
  <div class="app-container">
    <div class="header-container">
      <h2 class="page-title">测评记录查看</h2>
      <div class="header-actions">
        <el-input
          placeholder="搜索学员姓名"
          style="width: 200px; margin-right: 12px"
          clearable
        />
        <el-button type="primary" icon="el-icon-refresh" @click="fetchData">刷新</el-button>
        <el-button type="success" icon="el-icon-download" @click="exportData">导出</el-button>
      </div>
    </div>
    <el-table
      :data="tableData"
      stripe
      border
      style="width: 100%"
      v-loading="loading"
    >
      <el-table-column prop="studentName" label="学员姓名" width="120" />
      <el-table-column prop="evalTime" label="测评时间" width="180" />
      <el-table-column prop="totalScore" label="总分" sortable />
      <el-table-column prop="emotionalState" label="情绪状态" />
      <el-table-column prop="learningProgress" label="学习进度" />
    </el-table>
    <el-pagination
      :current-page="pagination.current"
      :page-size="pagination.size"
      :total="pagination.total"
      @current-change="handlePageChange"
      layout="prev, pager, next"
      class="pagination-container"
    />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getEvaluations, exportEvaluations } from '@/api/teacher/evaluation';

export default {
  name: 'EvaluationManagement',
  setup() {
    const tableData = ref([]);
    const loading = ref(false);
    const pagination = ref({
      current: 1,
      size: 10,
      total: 0,
    });

    // 获取数据
    const fetchData = async () => {
      try {
        loading.value = true;
        const res = await getEvaluations({
          page: pagination.value.current,
          pageSize: pagination.value.size,
        });
        tableData.value = res.data.records;
        pagination.value.total = res.data.total;
      } finally {
        loading.value = false;
      }
    };

    // 导出数据
    const exportData = async () => {
      try {
        loading.value = true;
        const res = await exportEvaluations({}); // 如果需要传递参数，可以在这里添加
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = '测评记录.xlsx'; // 文件名可以根据需要修改
        link.click();
      } catch (error) {
        console.error('导出失败:', error);
      } finally {
        loading.value = false;
      }
    };

    // 分页切换
    const handlePageChange = (val) => {
      pagination.value.current = val;
      fetchData();
    };

    onMounted(fetchData);

    return {
      tableData,
      loading,
      pagination,
      handlePageChange,
      exportData,
    };
  },
};
</script>

<style scoped>
.app-container {
  padding: 20px;
  background: #f0f2f5;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #eaeaea;
}

.page-title {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.main-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>