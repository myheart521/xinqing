<template>
  <div class="auth-container">
    <div class="glass-card">
      <div class="form-header">
        <h2>加入我们</h2>
        <p>注册获取专属权益</p>
      </div>

      <form @submit.prevent="handleSubmit" class="auth-form">
        <div class="form-grid">
          <!-- 账号 -->
          <!-- <div class="input-group" :class="{ 'has-error': !isUserAccountValid && form.userAccount }">
            <i class="fas fa-id-card"></i>
            <input
                v-model.trim="form.userAccount"
                type="text"
                required
                placeholder="账号 (6-30位)"
                @focus="userAccountFocused = true"
                @blur="userAccountFocused = false"
            />
            <span v-if="!isUserAccountValid && form.userAccount" class="error-message">
              需6-30位字母、数字或下划线
            </span>
          </div> -->

          <!-- 用户名 -->
          <div class="input-group" :class="{ 'has-error': !isUsernameValid && form.userName }">
            <i class="fas fa-user"></i>
            <input
                v-model.trim="form.userName"
                type="text"
                required
                placeholder="用户名 (3-20位)"
                @focus="usernameFocused = true"
                @blur="usernameFocused = false"
            />
            <span v-if="!isUsernameValid && form.userName" class="error-message">
              需3-20位字母、数字或下划线
            </span>
          </div>

          <!-- 账号 -->
          <div class="input-group" :class="{ 'has-error': !isAccountValid && form.account }">
            <i class="fas fa-id-card"></i>
            <input
                v-model.trim="form.account"
                type="text"
                required
                placeholder="账号 (3-20位)"
                @focus="accountFocused = true"
                @blur="accountFocused = false"
            />
            <span v-if="!isAccountValid && form.account" class="error-message">
              需3-20位字母、数字或下划线
            </span>
          </div>

          <!-- 邮箱 -->
          <div class="input-group" :class="{ 'has-error': !isEmailValid && form.email }">
            <i class="fas fa-envelope"></i>
            <input
                v-model.trim="form.email"
                type="email"
                required
                placeholder="电子邮箱"
                @focus="emailFocused = true"
                @blur="emailFocused = false"
            />
            <span v-if="!isEmailValid && form.email" class="error-message">
              请输入有效的邮箱地址
            </span>
          </div>

          <!-- 验证码 -->
          <div class="input-group code-group" :class="{ 'has-error': !form.code && codeFocused }">
            <i class="fas fa-key"></i>
            <input
                v-model.trim="form.code"
                type="text"
                required
                placeholder="验证码"
                @focus="codeFocused = true"
                @blur="codeFocused = false"
            />
            <button
                type="button"
                class="send-code-btn"
                :disabled="!isEmailValid || countdown > 0 || sendingCode"
                @click="sendEmailCode"
            >
              {{ countdown > 0 ? `${countdown}s` : sendingCode ? '发送中...' : '获取验证码' }}
            </button>
          </div>

          <!-- 密码 -->
          <div class="input-group" :class="{ 'has-error': !isPasswordStrong && form.password }">
            <i class="fas fa-lock"></i>
            <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                placeholder="密码 (至少6位)"
                @focus="passwordFocused = true"
                @blur="passwordFocused = false"
            />
            <button
                type="button"
                class="toggle-password"
                @click="showPassword = CONFIGURE_ON_SERVER"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
            <span v-if="!isPasswordStrong && form.password" class="error-message">
              需至少6位，含字母和数字
            </span>
          </div>

          <!-- 确认密码 -->
          <div class="input-group" :class="{ 'has-error': passwordMismatch && form.checkPassword }">
            <i class="fas fa-lock"></i>
            <input
                v-model="form.checkPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                required
                placeholder="确认密码"
                @focus="confirmPasswordFocused = true"
                @blur="confirmPasswordFocused = false"
            />
            <button
                type="button"
                class="toggle-password"
                @click="showConfirmPassword = !showConfirmPassword"
            >
              <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
            <span v-if="passwordMismatch && form.checkPassword" class="error-message">
              两次密码不一致
            </span>
          </div>

          <!-- 省份 -->
          <div class="input-group" :class="{ 'has-error': !form.provinceId && provinceTouched }">
            <i class="fas fa-map-marker-alt"></i>
            <select
              v-model="form.provinceId"
              required
              @change="onProvinceChange"
              @blur="provinceTouched = true"
              class="custom-select"
              :class="{ 'select-error': !form.provinceId && provinceTouched }"
            >
              <option value="" disabled>请选择省份</option>
              <option v-for="province in provinceList" :key="province.id" :value="province.id">{{ province.name }}</option>
            </select>
            <span v-if="!form.provinceId && provinceTouched" class="error-message">请选择省份</span>
          </div>

          <!-- 学校 -->
          <div class="input-group" :class="{ 'has-error': !form.schoolId && schoolTouched }">
            <i class="fas fa-university"></i>
            <select
              v-model="form.schoolId"
              required
              @change="onSchoolChange"
              @blur="schoolTouched = true"
              class="custom-select"
              :class="{ 'select-error': !form.schoolId && schoolTouched }"
            >
              <option value="" disabled>请选择学校</option>
              <option v-for="school in schoolList" :key="school.id" :value="school.id">{{ school.name }}</option>
            </select>
            <span v-if="!form.schoolId && schoolTouched" class="error-message">请选择学校</span>
          </div>
        </div>

        <button
            type="submit"
            class="submit-btn"
            :disabled="loading || !formComplete"
        >
          <span v-if="!loading">立即注册</span>
          <div v-else class="loading-spinner"></div>
        </button>
      </form>

      <p class="toggle-form">
        已有账号？<a @click="switchToLogin">立即登录</a>
      </p>
    </div>

    <div class="deco-bubble bubble1"></div>
    <div class="deco-bubble bubble2"></div>
    <div class="deco-bubble bubble3"></div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { useRouter } from 'vue-router';

const router = useRouter();

// 省份和学校相关
const provinceList = ref([]);
const schoolList = ref([]);
const provinceTouched = ref(false);
const schoolTouched = ref(false);

// 状态管理
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const loading = ref(false);
const sendingCode = ref(false);
const countdown = ref(0);
const usernameFocused = ref(false);
const accountFocused = ref(false);
const emailFocused = ref(false);
const codeFocused = ref(false);
const passwordFocused = ref(false);
const confirmPasswordFocused = ref(false);

const form = reactive({
  userAccount: '',
  userName: '',
  account: '',
  email: '',
  code: '',
  password: '',
  checkPassword: '',
  provinceId: '',
  province: '',
  schoolId: '',
  school: ''
});

// 获取省份列表
const fetchProvinces = async () => {
  try {
    const res = await request({
      url: 'http://localhost:8080/user/province',
      method: 'get'
    });
    if (res.code === 1) {
      provinceList.value = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取省份失败');
    }
  } catch (e) {
    ElMessage.error('获取省份失败');
  }
};

// 获取学校列表
const fetchSchools = async (provinceId) => {
  schoolList.value = [];
  form.schoolId = '';
  if (!provinceId) return;
  try {
    const res = await request({
      url: 'http://localhost:8080/user/province/school',
      method: 'post',
      data: { pageNo: '1', pageSize: '100', provinceId: String(provinceId) }
    });
    if (res.code === 1) {
      schoolList.value = res.data || [];
    } else {
      ElMessage.error(res.msg || '获取学校失败');
    }
  } catch (e) {
    ElMessage.error('获取学校失败');
  }
};

const onProvinceChange = (e) => {
  const selected = provinceList.value.find(p => p.id == form.provinceId);
  form.province = selected ? selected.name : '';
  fetchSchools(form.provinceId);
  provinceTouched.value = true;
  schoolTouched.value = false;
  form.school = '';
  form.schoolId = '';
};

const onSchoolChange = (e) => {
  const selected = schoolList.value.find(s => s.id == form.schoolId);
  form.school = selected ? selected.name : '';
};

onMounted(() => {
  fetchProvinces();
});

// 表单验证
//const isUserAccountValid = computed(() => /^[a-zA-Z0-9_]{6,30}$/.test(form.userAccount));
const isUsernameValid = computed(() => /^[a-zA-Z0-9_]{3,20}$/.test(form.userName));
const isAccountValid = computed(() => /^[a-zA-Z0-9_]{3,20}$/.test(form.account));
const isEmailValid = computed(() => /^\S+@\S+\.\S+$/.test(form.email));
const isPasswordStrong = computed(() => /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/.test(form.password));
const passwordMismatch = computed(() => form.password !== form.checkPassword);
const isProvinceValid = computed(() => !!form.provinceId);
const isSchoolValid = computed(() => !!form.schoolId);
const formComplete = computed(() =>
    isUsernameValid.value &&
    isAccountValid.value &&
    isEmailValid.value &&
    form.code &&
    isPasswordStrong.value &&
    !passwordMismatch.value &&
    // isUserAccountValid.value &&
    isProvinceValid.value &&
    isSchoolValid.value
);

// 发送验证码
const sendEmailCode = async () => {
  console.log('sendEmailCode 被调用, 邮箱:', form.email, '有效性:', isEmailValid.value);
  if (!isEmailValid.value) {
    ElMessage.error('请输入有效的邮箱地址');
    return;
  }

  sendingCode.value = true;
  try {
    console.log('开始发送请求...');
    const response = await request({
      url: '/dev-api/user/checkEmail',
      method: 'post',
      data: { email: form.email },
      successCode: 1 // 明确指定成功码
    });
    console.log('后端响应:', response);

    if (response.code === 1) {
      ElMessage.success('验证码已发送，请检查邮箱');
      countdown.value = 60;
      const interval = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) clearInterval(interval);
      }, 1000);
    } else {
      ElMessage.error(response.msg || '验证码发送失败，请稍后重试');
    }
  } catch (error) {
      ElMessage.error('发送失败，请稍后重试');
  } finally {
    sendingCode.value = false;
  }
};


// 表单提交
const handleSubmit = async () => {
  provinceTouched.value = true;
  schoolTouched.value = true;
  if (!formComplete.value) {
    ElMessage.error('请完善所有必填信息');
    return;
  }
  // 省份和学校名同步
  const selectedProvince = provinceList.value.find(p => p.id == form.provinceId);
  form.province = selectedProvince ? selectedProvince.name : '';
  const selectedSchool = schoolList.value.find(s => s.id == form.schoolId);
  form.school = selectedSchool ? selectedSchool.name : '';
  loading.value = true;
  try {
    const response = await request({
      url: '/dev-api/user/teacher/register',
      method: 'post',
      data: form,
      successCode: 1
    });
    if (response.code===1) {
      ElMessage.success('注册成功');
      router.push('/login');
    } else {
      ElMessage.error(response.message || '注册失败');
    }
  } catch (error) {
    ElMessage.error('注册失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const socialLogin = (provider) => {
  window.location.href = `/api/auth/${provider}?redirect=/callback`;
};

const switchToLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 20px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 32px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.glass-card:hover {
  transform: translateY(-5px);
}

.form-header {
  text-align: center;
  margin-bottom: 24px;
}

.form-header h2 {
  color: #2d3748;
  font-size: 24px;
  margin-bottom: 8px;
}

.form-header p {
  color: #718096;
  font-size: 14px;
}

.social-login {
  display: grid;
  gap: 12px;
  margin-bottom: 24px;
}

.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #667eea;
  background: #f8faff;
}

.divider {
  text-align: center;
  margin: 24px 0;
  color: #718096;
  position: relative;
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

.divider span {
  background: white;
  padding: 0 16px;
  position: relative;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-grid {
  display: grid;
  gap: 20px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-group input {
  flex: 1;
  padding: 12px 12px 12px 40px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.input-group i {
  position: absolute;
  left: 12px;
  color: #a0aec0;
  transition: color 0.3s ease;
}

.input-group input:focus {
  border-color: #667eea;
  outline: none;
}

.input-group input:focus + i {
  color: #667eea;
}

.code-group {
  position: relative;
}

.send-code-btn {
  position: absolute;
  right: 8px;
  padding: 6px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.send-code-btn:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #a0aec0;
  cursor: pointer;
}

.has-error input {
  border-color: #ef4444;
}

.error-message {
  position: absolute;
  bottom: -18px;
  left: 40px;
  color: #ef4444;
  font-size: 12px;
}

.submit-btn {
  padding: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.toggle-form {
  text-align: center;
  margin-top: 20px;
  color: #718096;
}

.toggle-form a {
  color: #667eea;
  cursor: pointer;
}

.deco-bubble {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  filter: blur(40px);
}

.bubble1 { width: 300px; height: 300px; top: -150px; right: -150px; }
.bubble2 { width: 200px; height: 200px; bottom: -100px; left: -100px; }
.bubble3 { width: 150px; height: 150px; top: 50%; left: 20%; }

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .glass-card {
    padding: 20px;
  }

  .form-header h2 {
    font-size: 20px;
  }
}

.custom-select {
  flex: 1;
  padding: 12px 12px 12px 40px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  background: white;
  appearance: none;
  outline: none;
}
.custom-select:focus {
  border-color: #667eea;
}
.select-error {
  border-color: #ef4444;
}
</style>
