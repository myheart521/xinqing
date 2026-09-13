<template>
  <div class="app-container">
    <el-container>
      <el-header class="header-container">
        <h2 class="page-title">预约信息管理</h2>
        <div class="header-actions">
          <el-input
              placeholder="搜索用户名"
              v-model="searchKeyword"
              clearable
              style="width: 200px; margin-right: 12px"
          />
          <el-button type="primary" @click="fetchUsers">搜索</el-button>
          <!-- 如果后端没有导出接口，可以注释掉 -->
          <!-- <el-button type="success" icon="el-icon-download" @click="exportUsers">导出</el-button> -->
        </div>
      </el-header>
      <el-main class="main-container">
        <student-table
              :tableData="tableData" 
              :loading="loading" 
              :pagination="pagination" 
              @page-change="handlePageChange"
              @refresh-data="fetchUsers" 
              @edit="openEdit"
        />
        <student-edit-dialog
            style="z-index: 999"
            v-model:visible="editDialogVisible"
            :student="currentStudent"
        />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAppointmentTable } from '@/api/appointment/appointment.js';
import StudentTable from './components/AppointmentTable.vue';

const tableData = ref([]);
const loading = ref(false);
const searchKeyword = ref('');
const pagination = ref({ current: 1, size: 10, total: 0 });

const editDialogVisible = ref(false);
const currentStudent = ref(null);

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await getAppointmentTable({
      page: pagination.value.current,
      pageSize: pagination.value.size,
      name: searchKeyword.value
    });
    if (res.code === 1) {
      tableData.value = res.data.records || [];
      pagination.value.total = res.data.total || res.data.length || 0;
    } else {
      tableData.value = [];
      pagination.value.total = 0;
    }
  } catch (e) {
    tableData.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (val) => {
  pagination.value.current = val;
  fetchUsers();
};

const openEdit = (row) => {
  currentStudent.value = { ...row };
  editDialogVisible.value = true;
  console.log("打开编辑弹窗：",editDialogVisible.value)
};

// 如果后端支持删除，可以取消注释并实现
/*
const deleteUser = async (id) => {
  try {
    loading.value = true;
    const res = await deleteUser(id);
    if (res.code === 200) {
      this.$message.success('删除成功');
      fetchUsers();
    } else {
      this.$message.error(res.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除失败:', error);
    this.$message.error('删除失败');
  } finally {
    loading.value = false;
  }
};
*/

onMounted(fetchUsers);
</script>

<style scoped>
.app-container {
  background: #f0f2f5;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  height: calc(100vh - 156px);
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
