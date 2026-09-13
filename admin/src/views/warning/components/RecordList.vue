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
      <!-- 学生信息 -->
      <el-table-column label="学生" width="180">
        <template #default="scope">
          <el-avatar :src="scope.row.student.userAvatar" :size="32"/>
          <span style="margin-left: 8px">{{ scope.row.student.userName }}</span>
        </template>
      </el-table-column>
      <!-- 学号 -->
      <el-table-column
          label="学号"
          width="140"
          :formatter="row => row.student?.studentNumber ?? '-'"
      />
      <!-- 检测时间 -->
      <el-table-column
          label="检测时间"
          width="120"
          :formatter="row => formatTime(row.detectionTime).slice(0, 10)"
      />
      <!-- 情感类型 -->
      <el-table-column prop="emotionType" label="情感类型" width="100"/>
      <!-- 置信度 -->
      <el-table-column label="置信度" width="120">
        <template #default="scope">
          <el-progress :percentage="Math.round(scope.row.confidence * 100)"/>
        </template>
      </el-table-column>
      <!-- 代表内容 -->
      <el-table-column prop="relatedContent" label="代表内容" show-overflow-tooltip/>
      <!-- 学校 -->
      <el-table-column label="学校" width="120" :formatter="row => row.student?.school ?? '-'" />
      <!-- 专业班级 -->
      <el-table-column label="专业班级" width="140" :formatter="row => row.student?.majorClass ?? '-'" />
      <!-- 代表博客 -->
      <el-table-column label="代表博客" width="160">
        <template #default="scope">
          <el-tooltip effect="light" placement="top" :disabled="!scope.row.featureBlog">
            <template #content>
              <div v-if="scope.row.featureBlog" class="tooltip-content">
                <p><strong>标题：</strong>{{ scope.row.featureBlog.title }}</p>
                <p><strong>内容：</strong>{{ scope.row.featureBlog.content }}</p>
                <p><strong>发布时间：</strong>{{ formatTime(scope.row.featureBlog.createTime) }}</p>
              </div>
              <span v-else>-</span>
            </template>
            <span>{{ scope.row.featureBlog?.title || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- 代表活动 -->
      <el-table-column label="代表活动" width="160">
        <template #default="scope">
          <el-tooltip effect="light" placement="top" :disabled="!scope.row.featureActive">
            <template #content>
              <div v-if="scope.row.featureActive" class="tooltip-content">
                <p><strong>标题：</strong>{{ scope.row.featureActive.title }}</p>
                <p><strong>内容：</strong>{{ scope.row.featureActive.content }}</p>
                <p><strong>地址：</strong>{{ scope.row.featureActive.address }}</p>
                <p><strong>时间：</strong>{{ formatArrayTime(scope.row.featureActive.time) }}</p>
              </div>
              <span v-else>-</span>
            </template>
            <span>{{ scope.row.featureActive?.title || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <!-- 是否异常 -->
      <el-table-column label="是否异常" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isAbnormal === 1 ? 'danger' : 'success'">
            {{ scope.row.isAbnormal === 1 ? '异常' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 性别 -->
      <el-table-column label="性别" width="70" :formatter="row => row.student?.sex ?? '-'" />
      <!-- 省份 -->
      <el-table-column label="省份" width="100" :formatter="row => row.student?.province ?? '-'" />
      <!-- 邮箱 -->
      <el-table-column label="邮箱" width="200" :formatter="row => row.student?.email ?? '-'" />
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
import {ref, onMounted} from 'vue';
import {getRecord} from '@/api/warning/warning.js';
import {formatTime} from '@/utils/formate.js';

// 选项式 API
// const studentId = this.$route.query.studentId

// 数据状态
const tableData = ref([]);
const loading = ref(false);
const pagination = ref({current: 1, size: 10, total: 0});

// 格式化数组形式的时间
const formatArrayTime = (arr) => {
  if (!arr || arr.length < 6) return '';
  const [year, month, day, hour, minute, second] = arr;
  return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`;
};

const props = defineProps({
  studentId: {
    type: [String, Number],
    default: null
  }
});
// 获取数据
const fetchData = async () => {
  loading.value = true;
  try {
    
    console.log("子组件studentId",props.studentId)
    const res = await getRecord({pageNum: pagination.value.current, pageSize: pagination.value.size,studentId:props.studentId});
    if (res.code === 1) {
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

// 分页变化
const handlePageChange = (page) => {
  pagination.value.current = page;
  fetchData();
};

// 暴露给父组件以便刷新
const reload = () => fetchData();
defineExpose({reload});

onMounted(fetchData);
</script>

<style scoped>
.table-wrapper {
  width: 100%;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow-x: auto;
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
