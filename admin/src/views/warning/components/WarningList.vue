<template>
  <div class="table-wrapper">
    <el-table
      :data="tableData"
      stripe
      border
      style="width: 100%"
      :loading="loading"
      empty-text="暂无数据"
    >
      <!-- 学生 -->
      <!-- <el-table-column label="学生" width="180">
        <template #default="scope">
          <el-avatar :src="scope.row.student.userAvatar" :size="32" />
          <span style="margin-left: 8px">{{ scope.row.student.userName }}</span>
        </template>
      </el-table-column> -->
      <!-- 学号 -->
      <!-- <el-table-column label="学号" width="140" :formatter="row => row.student?.studentNumber ?? '-'" /> -->
      <!-- 电话 -->
      <!-- <el-table-column label="电话" width="140" :formatter="row => row.student?.phone ?? '-'" /> -->
      <!-- 邮箱 -->
      <!-- <el-table-column label="邮箱" width="200" :formatter="row => row.student?.email ?? '-'" /> -->
      <!-- 触发时间 -->
      <el-table-column
        label="触发时间"
        width="120"
        :formatter="row => formatTime(row.triggerTime).slice(0, 10)"
      />
      <!-- 报警等级 -->
      <el-table-column label="预警等级" width="100">
        <template #default="scope">
          <el-tag :type="['success','warning','danger'][scope.row.alertLevel]">
            {{ ['低级','中级','紧急',"正常"][scope.row.alertLevel] }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 处理状态 -->
      <!-- <el-table-column label="处理状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.handlingStatus === 1 ? 'success' : 'info'">
            {{ scope.row.handlingStatus === 1 ? '已处理' : '未处理' }}
          </el-tag>
        </template>
      </el-table-column> -->
      <!-- 是否解决 -->
      <!-- <el-table-column label="是否解决" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isResolve === 1 ? 'success' : 'warning'">
            {{ scope.row.isResolve === 1 ? '已解决' : '未解决' }}
          </el-tag>
        </template>
      </el-table-column> -->
      <!-- 处理人 -->
      <!-- <el-table-column label="处理人" width="180">
        <template #default="scope">
          <el-tooltip effect="light" placement="top" :disabled="!scope.row.handlerUser">
            <template #content>
              <div v-if="scope.row.handlerUser" class="tooltip-content">
                <p><strong>姓名：</strong>{{ scope.row.handlerUser.userName }}</p>
                <p><strong>邮箱：</strong>{{ scope.row.handlerUser.email || '-' }}</p>
                <p><strong>电话：</strong>{{ scope.row.handlerUser.phone || '-' }}</p>
                <p><strong>角色：</strong>{{ scope.row.handlerUser.roleId === 2 ? '教师' : '管理员' }}</p>
              </div>
              <span v-else>-</span>
            </template>
            <div v-if="scope.row.handlerUser" style="display: flex; align-items: center;">
              <el-avatar :src="scope.row.handlerUser.userAvatar" :size="32" />
              <span style="margin-left: 8px">{{ scope.row.handlerUser.userName }}</span>
            </div>
            <span v-else>-</span>
          </el-tooltip>
        </template>
      </el-table-column> -->
      <!-- 备注 -->
      <!-- <el-table-column prop="note" label="备注" show-overflow-tooltip /> -->
      <el-table-column prop="suggestion" label="AI建议" show-overflow-tooltip />
      <el-table-column prop="recentBehavior" label="近期行为特征" show-overflow-tooltip />
    </el-table>

    <el-pagination
      class="pagination-container"
      :current-page="pagination.current"
      :page-size="pagination.size"
      :total="pagination.total"
      layout="prev, pager, next"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getWarning } from '@/api/warning/warning.js';
import { formatTime } from '@/utils/formate.js';
import { useRoute } from 'vue-router';

const tableData = ref([]);
const loading = ref(false);
const pagination = ref({ current: 1, size: 10, total: 0 });
const props = defineProps({
  studentId: {
    type: [String, Number],
    default: null
  }
});
const fetchData = async () => {
  loading.value = true;
  try {

    const params = { pageNum: pagination.value.current, pageSize: pagination.value.size,studentId:props.studentId};
    if (props.studentId) params.studentId = props.studentId;
    const res = await getWarning(params);
    if (res.code === 1) {
      console.log("---------------:",res)
      tableData.value = res.data.records || [];
      pagination.value.total = res.data.total || 0;
    } else {
      tableData.value = [];
      pagination.value.total = 0;
    }
  } catch (error) {
    tableData.value = [];
    pagination.value.total = 0;
  } finally {
    loading.value = false;
  }
};

const handlePageChange = (page) => {
  pagination.value.current = page;
  fetchData();
};

const reload = () => fetchData();
defineExpose({ reload });

onMounted(fetchData);
</script>

<style scoped>
.table-wrapper {
  width: 100%;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
.tooltip-content {
  max-width: 300px;
  padding: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.tooltip-content p {
  margin-bottom: 10px;
}

.tooltip-content strong {
  font-weight: bold;
  margin-right: 5px;
}
</style>
