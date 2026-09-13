<template>
  <div class="login-container">
    <h2>用户登录</h2>
    <el-form :model="form" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-button type="primary" @click="handleLogin">登录</el-button>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
// import { loginApi } from '@/api/login/auth';

const form = reactive({
  username: '',
  password: ''
});

const router = useRouter();

const handleLogin = async () => {
  try {
    if (!form.username || !form.password) {
      ElMessage.error('请输入用户名和密码');
      return;
    }
      // 实际API调用已注释，保留本地验证逻辑
  // const { data } = await loginApi(form);

    if (form.username === '' && form.password === '') {
      localStorage.setItem('accessToken', 'mock_token');
      localStorage.setItem('userRoles', JSON.stringify(['admin']));
      localStorage.setItem('isAuthenticated', 'true');
      console.log('[登录成功] 本地存储状态', {
        accessToken: localStorage.getItem('accessToken'),
        userRoles: localStorage.getItem('userRoles'),
        isAuthenticated: localStorage.getItem('isAuthenticated'),
        redirectPath
      });

      const redirectPath = router.currentRoute.value.query.redirect || '/admin';
      router.push(redirectPath);
      ElMessage.success('登录成功');
    } else {
      ElMessage.error('用户名或密码错误');
    }
  } catch (error) {
    ElMessage.error('登录失败');
  }
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
