<template>
  <div class="auth-container">
    <div class="glass-card">
      <div class="form-header">
        <h2>欢迎回来</h2>
        <p>立即登录开启旅程</p>
      </div>

      <!--      <div class="social-login">-->
      <!--        <button class="social-btn google" @click="socialLogin('google')">-->
      <!--          <i class="fab fa-google"></i>-->
      <!--          Google账号登录-->
      <!--        </button>-->
      <!--        <button class="social-btn github" @click="socialLogin('github')">-->
      <!--          <i class="fab fa-github"></i>-->
      <!--          GitHub账号登录-->
      <!--        </button>-->
      <!--      </div>-->

      <!--      <div class="divider">-->
      <!--        <span>或</span>-->
      <!--      </div>-->

      <form @submit.prevent="handleSubmit">
        <!-- 邮箱输入框 -->
        <div class="input-group" :class="{ focused: emailFocused }">
          <i class="fas fa-envelope"></i>
          <input
              v-model="form.email"
              type="text"
              required
              @focus="emailFocused = true"
              @blur="emailFocused = false"
              placeholder="请输入电子邮箱"
          />
          <span v-if="!isEmailValid && emailFocused" class="error-message">请输入有效的邮箱地址</span>
          <span class="input-border"></span>
        </div>

        <!-- 密码输入框 -->
        <div class="input-group" :class="{ focused: passwordFocused }">
          <i class="fas fa-lock"></i>
          <input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              required
              @focus="passwordFocused = true"
              @blur="passwordFocused = false"
              placeholder="请输入密码"
          />
          <button
              type="button"
              class="toggle-password"
              @click="showPassword = CONFIGURE_ON_SERVER"
          >
            <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
          </button>
          <span v-if="!isPasswordStrong && passwordFocused" class="error-message">密码强度较弱</span>
          <span class="input-border"></span>
        </div>

        <!-- 提交按钮 -->
        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="!loading">立即登录</span>
          <div v-else class="loading-spinner"></div>
        </button>
      </form>

      <p class="toggle-form">
        没有账号？
        <a @click="switchToRegister">立即注册</a>
      </p>
    </div>

    <div class="deco-bubble bubble1"></div>
    <div class="deco-bubble bubble2"></div>
    <div class="deco-bubble bubble3"></div>
  </div>
</template>

<script setup>
import {ref, reactive, computed} from 'vue';
import {ElMessage} from 'element-plus';
import request from '@/utils/request'; // 引入封装的请求工具
import {useRouter} from 'vue-router';
import {useUserStore} from '@/stores/user'; // 导入用户 store

const router = useRouter();
const userStore = useUserStore(); // 使用 Pinia store

const showPassword = ref(false);
const loading = ref(false);
const emailFocused = ref(false);
const passwordFocused = ref(false);

const form = reactive({
  email: '',
  password: ''
});

// 邮箱格式验证
const isEmailValid = computed(() => {
  return /^\S+@\S+\.\S+$/.test(form.email); // 恢复邮箱验证
  // return true; // 如果不需要验证，可保留此行
});

// 密码强度验证（至少6位，包含字母和数字）
const isPasswordStrong = computed(() => {
  return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/.test(form.password); // 恢复密码强度验证
  // return true; // 如果不需要验证，可保留此行
});

const handleSubmit = async () => {
  if (!isEmailValid.value) {
    ElMessage.error('请输入有效的邮箱地址');
    return;
  }
  if (!form.password) {
    ElMessage.error('请输入密码');
    return;
  }

  loading.value = true;
  try {
    const response = await request({
      url: '/user/emaillogin',
      method: 'POST', // 明确指定 POST 方法
      data: {email: form.email, password: form.password}, // 调整字段名与后端一致
      successCode: 1
    });

    // 根据您提供的返回数据结构处理响应
    if (response.code === 1) {
      // 使用 Pinia store 保存用户信息
      userStore.setLoginInfo(response.data);

      ElMessage.success('登录成功！');
      router.push('/admin'); // 跳转到主页
    } else {
      ElMessage.error(response.msg || '登录失败，请检查邮箱和密码是否正确');
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败，请稍后再试');
  } finally {
    loading.value = false;
  }
};

const socialLogin = (provider) => {
  window.location.href = `/api/auth/${provider}?redirect=/callback`;
};

const switchToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
/* 样式部分保持不变 */
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 40px;
  width: 440px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  transform: translateY(0);
  transition: all 0.3s ease;
}

.glass-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  color: #2d3748;
  font-size: 28px;
  margin-bottom: 8px;
}

.form-header p {
  color: #718096;
  font-size: 14px;
}

.input-group {
  position: relative;
  margin-bottom: 24px;
  width: 85%;
}

.input-group i {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #a0aec0;
  transition: all 0.3s ease;
}

.input-group input {
  width: 100%;
  height: 48px;
  padding: 0 16px 0 48px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

.input-group input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.input-group.focused i {
  color: #667eea;
  transform: translateY(-50%) scale(1.1);
}

.toggle-password {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #a0aec0;
  cursor: pointer;
  padding: 4px;
  transition: transform 0.3s ease;
}

.toggle-password:hover {
  transform: translateY(-50%) scale(1.1);
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.social-login {
  margin: 24px 0;
}

.social-btn {
  width: 100%;
  height: 48px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #c3dafe;
  background: #f8faff;
}

.social-btn i {
  font-size: 18px;
}

.google {
  color: #db4437;
}

.github {
  color: #333;
}

.divider {
  position: relative;
  margin: 24px 0;
  text-align: center;
}

.divider span {
  background: white;
  padding: 0 16px;
  color: #718096;
  position: relative;
  z-index: 1;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e2e8f0;
}

.toggle-form {
  text-align: center;
  margin-top: 24px;
  color: #718096;
}

.toggle-form a {
  color: #667eea;
  cursor: pointer;
  font-weight: 500;
}

.toggle-form a:hover {
  text-decoration: underline;
}

.deco-bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  filter: blur(40px);
  z-index: 0;
}

.bubble1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  background: linear-gradient(120deg, #667eea, #764ba2);
}

.bubble2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  background: linear-gradient(60deg, #4c51bf, #667eea);
}

.bubble3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 20%;
  background: linear-gradient(30deg, #553c9a, #6b46c1);
}

.error-message {
  font-size: 12px;
  color: red;
  position: absolute;
  bottom: -20px;
  left: 0;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 600px) {
  .glass-card {
    width: 90%;
    padding: 20px;
  }
}
</style>
