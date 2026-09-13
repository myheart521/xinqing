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
      <!-- <el-table-column prop="id" label="ID" width="60"/> -->
      <el-table-column prop="userName" label="姓名" width="100"/>
      <el-table-column prop="userAvatar" label="头像" width="80">
        <template #default="scope">
          <img :src="scope.row.userAvatar" alt="头像" class="avatar-img"/>
        </template>
      </el-table-column>
      <!-- <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip/> -->
      <el-table-column prop="phone" label="手机号" width="120"/>
      <el-table-column prop="sex" label="性别" width="60">
        <template #default="scope">
          <el-tag :type="scope.row.sex === '男' ? 'primary' : 'danger'">
            {{ scope.row.sex === '男' ? "男" : '女' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="日期" width="120"/>
      <el-table-column prop="startTime" label="开始时间" width="120"/>
      <el-table-column prop="endTime" label="结束时间" width="120"/>
      <el-table-column prop="status" label="状态" width="100"/>
      <el-table-column prop="college" label="学院" width="100"/>
      <el-table-column prop="majorClass" label="班级" width="150"/>
      <el-table-column prop="studentNumber" label="学号" width="160"/>
      
      <!-- <el-table-column prop="createTime" label="创建时间" width="160">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="160">
        <template #default="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column> -->
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="scope">
          <el-button v-if="scope.row.status==='未处理'" type="primary" text @click="accept(scope.row)">
              同意
          </el-button>
          <el-button v-if="scope.row.status==='未处理'" type="primary" text @click="refuse(scope.row)">
              驳回
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
<!-- 添加驳回原因选择对话框 -->
<el-dialog
      title="请选择驳回原因"
      v-model="showReasonDialog"
      width="500px"
    >
      <el-radio-group v-model="selectedReason" class="reason-group">
        <el-radio 
          v-for="(reason, index) in refuseReasons" 
          :key="index" 
          :label="reason"
          border
          class="reason-item"
        >
          {{ reason }}
        </el-radio>
        <el-radio label="other" border class="reason-item">其他原因</el-radio>
      </el-radio-group>
      
      <el-input
        v-if="selectedReason === 'other'"
        v-model="customReason"
        type="textarea"
        :rows="3"
        placeholder="请输入驳回原因"
        class="custom-reason-input"
      ></el-input>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReasonDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleSubmitRefuse"
            :disabled="!isReasonValid"
          >
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {defineProps, ref, defineEmits, onMounted,computed} from 'vue';
import {update, getAppointmentTable} from '@/api/appointment/appointment.js';
import { formatTime } from '@/utils/formate.js';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';

const loading = ref(false);
const searchKeyword = ref('');
// 添加驳回原因选项
const refuseReasons = ref([
  '当前时间段已有其他预约',
  '您的问题可能需要更专业的心理医生介入',
  '建议您先尝试自我调节方法',
  '我的专业领域与您的问题不太匹配',
  '建议您改约其他时间段'
]);
const selectedReason = ref(''); // 选择的原因类型
const customReason = ref(''); // 自定义原因内容
const showReasonDialog = ref(false); // 控制原因选择对话框显示
const currentRow = ref(null); // 当前操作的行

const props = defineProps({
  tableData: Array,
  loading: Boolean,
  pagination: Object
});

// 定义事件
const emit = defineEmits(['page-change', 'refresh-data']);

// 刷新数据的方法
const fetchUsers = async () => {
  try {
    console.info("刷新预约信息");
    // 通知父组件需要刷新数据
    emit('refresh-data');
  } catch (e) {
    console.error('刷新数据失败:', e);
    ElMessage.error('刷新数据失败');
  }
};

// 同意预约
const accept = (row) => {
  console.info("同意")
  ElMessageBox.confirm(
    '你是否接收该学生的预约？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    // 构造请求体
    const appointmentData = {
      id: row.id,
      status: 1,
    };
    // 调用API更新预约状态
    try {
      const res = await update(appointmentData)
      console.log("响应结果:", res)
      if (res.code === 1) {
        ElMessage.success(res.msg || '操作成功');
        // 刷新表格
        console.log("刷新表格")
        fetchUsers(); 
      } else {
        ElMessage.error(res.msg || '操作失败');
      }
    } catch (e) {
      console.error('请求失败:', e);
      ElMessage.error('请求失败');
    }
  }).catch(() => {
    // 用户取消，无需处理
  });
};

// 驳回预约
const refuse = (row) => {
  console.info("驳回");
  // 保存当前行数据
  currentRow.value = row;
  // 重置自定义原因
  customReason.value = '';
  // 显示驳回原因对话框
  showReasonDialog.value = true;
    // console.info("驳回")
  // ElMessageBox.confirm(
  //   '你是否要驳回该学生的预约？',
  //   '提示',
  //   {
  //     confirmButtonText: '确定',
  //     cancelButtonText: '取消',
  //     type: 'warning',
  //   }
  // ).then(async () => {
  //   // 构造请求体
  //   const appointmentData = {
  //     id: row.id,
  //     status: 2, // 假设2表示驳回状态
  //     excuse: excuse // 驳回原因
  //   };
  //   // 调用API更新预约状态
  //   try {
  //     const res = await update(appointmentData)
  //     if (res.code === 1) {
  //       ElMessage.success(res.msg || '操作成功');
  //       // 刷新表格
};

// 判断是否有有效的驳回原因
const isReasonValid = computed(() => {
  return selectedReason.value && (selectedReason.value !== 'other' || customReason.value.trim() !== '');
});

// 处理提交驳回
const handleSubmitRefuse = () => {
  // 确定最终的驳回原因
  const finalReason = selectedReason.value === 'other' ? customReason.value : selectedReason.value;
  submitRefuse(finalReason);
};

// 提交驳回请求
const submitRefuse = async (excuse) => {
  if (!currentRow.value) return;
  
  ElMessageBox.confirm(
    `确定要驳回该预约吗？
    
     驳回原因: ${excuse}`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    // 构造请求体
    const appointmentData = {
      id: currentRow.value.id,
      status: 2, // 驳回状态
      excuse: excuse // 驳回原因
    };
    
    // 调用API更新预约状态
    try {
      const res = await update(appointmentData);
      if (res.code === 1) {
        ElMessage.success(res.msg || '驳回成功');
        // 刷新表格
        fetchUsers();
        // 重置状态
        showReasonDialog.value = false;
        currentRow.value = null;
        customReason.value = '';
      } else {
        ElMessage.error(res.msg || '驳回失败');
      }
    } catch (e) {
      console.error('请求失败:', e);
      ElMessage.error('请求失败');
    }
  }).catch(() => {
    // 用户取消，无需处理
  });
};

// 组件挂载时检查父组件是否监听了refresh-data事件
onMounted(() => {
  console.log("组件已挂载，请确保父组件监听了refresh-data事件");
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

/* 添加新样式 */
.reason-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.reason-item {
  margin-right: 0 !important;
  width: 100%;
}

.custom-reason-input {
  margin-top: 15px;
}
</style>