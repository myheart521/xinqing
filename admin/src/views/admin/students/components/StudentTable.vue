<template>
  <div class="table-wrapper">
    <el-table
        :data="tableData"
        stripe
        border
        style="width: 100%;height: 100%"
        :loading="loading"
        empty-text="暂无数据"
    >
      <el-table-column prop="id" label="ID" width="60"/>
      <el-table-column prop="userName" label="用户名" width="120"/>
      <el-table-column prop="userAvatar" label="头像" width="80">
        <template #default="scope">
          <img :src="scope.row.userAvatar" alt="头像" class="avatar-img"/>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip/>
      <el-table-column prop="phone" label="手机号" width="120"/>
      <el-table-column prop="sex" label="性别" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.sex === '男' ? 'primary' : 'danger'">
            {{ scope.row.sex === '男' ? "男" : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="roleId" label="角色" width="80">
        <template #default="scope">
          <el-tag
              :type="
              scope.row.roleId === 1
                ? 'danger'
                : scope.row.roleId === 2
                ? 'warning'
                : 'success'
            "
          >
            {{
              scope.row.roleId === 1
                  ? '管理员'
                  : scope.row.roleId === 2
                      ? '教师'
                      : '学生'
            }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="college" label="学院" width="140"/>
      <el-table-column prop="majorClass" label="班级" width="120"/>
      <el-table-column prop="school" label="学校" width="140"/>
      <el-table-column prop="province" label="省份" width="100"/>
      <el-table-column prop="studentNumber" label="学号" width="160"/>
      <el-table-column prop="userProfile" label="简介" show-overflow-tooltip/>
      <el-table-column prop="avatars" label="相册" width="140">
        <template #default="scope">
          <el-image
              v-for="(url, index) in scope.row.avatars?.split(',')"
              :key="index"
              :src="url"
              :preview-src-list="scope.row.avatars?.split(',')"
              fit="cover"
              style="width: 30px; height: 30px; border-radius: 4px; margin-right: 4px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="160">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="100">
        <template #default="scope">
          <el-button type="primary" text @click="$emit('edit', scope.row)">
            编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :total="pagination.total"
        @current-change="$emit('page-change', $event)"
        layout="prev, pager, next"
        class="pagination-container"
    />
  </div>
</template>

<script setup>
import {defineProps} from 'vue';
import {formatTime} from '@/utils/formate.js';

defineProps({
  tableData: Array,
  loading: Boolean,
  pagination: Object
});
</script>

<style>
.table-wrapper {
  width: 100%;
  height: 90%;
  overflow-x: auto;
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .avatar-img {
    width: 32px;
    height: 32px;
  }
}

.pagination-container {
  position: fixed;
  bottom: 50px;
}
</style>