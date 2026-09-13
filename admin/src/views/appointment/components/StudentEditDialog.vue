<template>
  <el-dialog v-model="innerVisible" title="编辑学生信息" width="400px">
    <el-form :model="student" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="student.userName" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="student.email" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="student.phone" />
      </el-form-item>
      <!-- 其他字段可按需添加 -->
    </el-form>
    <template #footer>
      <el-button @click="innerVisible = false">取消</el-button>
      <el-button type="primary" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';

// 接收父组件 v-model:visible 和 student
const props = defineProps({
  visible: Boolean,
  student: Object
});
const emit = defineEmits(['update:visible']);

// 内部可变可写的弹窗显隐状态
const innerVisible = ref(props.visible);

// 监听父 prop 同步到内部
watch(() => props.visible, (val) => {
  innerVisible.value = val;
});
// 内部变化同步给父组件
watch(innerVisible, (val) => emit('update:visible', val));

const save = () => {
  // TODO: 保存逻辑，可对 student 进行请求提交
  innerVisible.value = false;
};
</script>