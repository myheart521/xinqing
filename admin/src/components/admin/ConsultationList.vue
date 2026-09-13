<script setup lang="ts">
import { ref } from 'vue';

const consultations = ref([
  {
    id: 1,
    clientName: '张三',
    consultantName: '李医生',
    type: '情绪困扰',
    date: '2024-01-15',
    status: '已完成'
  },
  {
    id: 2,
    clientName: '李四',
    consultantName: '王医生',
    type: '学业压力',
    date: '2024-01-16',
    status: '进行中'
  }
]);
</script>

<template>
  <div class="consultation-list">
    <div class="list-header">
      <h2>咨询记录</h2>
      <div class="header-actions">
        <input type="text" placeholder="搜索咨询记录" class="search-input" />
        <button class="add-btn">新增记录</button>
      </div>
    </div>

    <table class="consultation-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>来访者</th>
          <th>咨询师</th>
          <th>咨询类型</th>
          <th>日期</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in consultations" :key="item.id">
          <td>{{ item.id }}</td>
          <td>{{ item.clientName }}</td>
          <td>{{ item.consultantName }}</td>
          <td>{{ item.type }}</td>
          <td>{{ item.date }}</td>
          <td>
            <span class="status-tag" :class="item.status === '已完成' ? 'completed' : 'ongoing'">
              {{ item.status }}
            </span>
          </td>
          <td>
            <button class="action-btn">查看</button>
            <button class="action-btn">编辑</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button class="page-btn">&lt;</button>
      <button class="page-btn active">1</button>
      <button class="page-btn">2</button>
      <button class="page-btn">3</button>
      <button class="page-btn">&gt;</button>
    </div>
  </div>
</template>

<style scoped>
.consultation-list {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.search-input {
  padding: 0.5rem 1rem;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  width: 200px;
}

.add-btn {
  background: #1890ff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.add-btn:hover {
  background: #096dd9;
}

.consultation-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1.5rem;
}

.consultation-table th,
.consultation-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
}

.consultation-table th {
  background: #fafafa;
  font-weight: 600;
}

.status-tag {
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
}

.status-tag.completed {
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.status-tag.ongoing {
  background: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.action-btn {
  background: none;
  border: 1px solid #d9d9d9;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  margin-right: 0.5rem;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn:hover {
  color: #1890ff;
  border-color: #1890ff;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
}

.page-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover,
.page-btn.active {
  color: #1890ff;
  border-color: #1890ff;
}
</style>