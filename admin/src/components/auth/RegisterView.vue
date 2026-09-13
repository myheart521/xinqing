<template>
  <div class="register-container">
    <h2>用户注册</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input v-model="form.confirmPassword" type="password" />
      </el-form-item>
      <el-button type="primary" @click="handleRegister">注册</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';

const form = reactive({
  username: 'testuser',
  password: '',
  confirmPassword: ''
});

const handleRegister = async () => {
  try {
    if (form.password !== form.confirmPassword) {
      ElMessage.error('两次输入密码不一致');
      return;
    }
    const { data } = await registerApi(form);
    ElMessage.success('注册成功，请登录');
    router.push('/login');
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败');
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>