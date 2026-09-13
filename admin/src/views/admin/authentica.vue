<template>
  <div class="complete-profile-container">
    <div class="card">
      <h2 class="title">完善个人信息</h2>
      <form @submit.prevent="handleSubmit" class="form">
        <div class="form-group" :class="{ 'has-error': errors.email }">
          <label for="email">邮箱</label>
          <input id="email" v-model="formData.email" type="email" required placeholder="请输入邮箱" />
          <span class="error-message" v-if="errors.email">{{ errors.email }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.password }">
          <label for="password">密码</label>
          <input id="password" v-model="formData.password" type="password" placeholder="请输入密码" />
          <span class="error-message" v-if="errors.password">{{ errors.password }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.checkPassword }">
          <label for="checkPassword">确认密码</label>
          <input id="checkPassword" v-model="formData.checkPassword" type="password" placeholder="请再次输入密码" />
          <span class="error-message" v-if="errors.checkPassword">{{ errors.checkPassword }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.userName }">
          <label for="userName">用户名</label>
          <input id="userName" v-model="formData.userName" type="text" required placeholder="请输入用户名" />
          <span class="error-message" v-if="errors.userName">{{ errors.userName }}</span>
        </div>

<!--        <div class="form-group">-->
<!--          <label for="userAvatar">头像</label>-->
<!--          <div class="avatar-upload">-->
<!--            <img v-if="avatarPreview" :src="avatarPreview" alt="Avatar Preview" class="avatar-preview" />-->
<!--            <input id="userAvatar" type="file" accept="image/*" @change="handleAvatarChange" ref="avatarInput" />-->
<!--          </div>-->
<!--          <span class="upload-tip">点击选择图片上传 (最大2MB)</span>-->
<!--        </div>-->

        <div class="form-group">
          <label for="userProfile">个人简介</label>
          <input id="userProfile" v-model="formData.userProfile" type="text" placeholder="简单介绍一下自己" />
        </div>

        <div class="form-group" :class="{ 'has-error': errors.userRole }">
          <label for="userRole">角色</label>
          <select id="userRole" v-model="formData.userRole">
            <option value="student">学生</option>
            <option v-if="isTeacher" value="teacher">老师</option>
          </select>
          <span class="error-message" v-if="errors.userRole">{{ errors.userRole }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.phone }">
          <label for="phone">手机号</label>
          <input id="phone" v-model="formData.phone" type="text" placeholder="请输入手机号" />
          <span class="error-message" v-if="errors.phone">{{ errors.phone }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.school }">
          <label for="schoolName">学校名称</label>
          <input id="schoolName" v-model="formData.school" type="text" placeholder="请输入学校名称" />
          <span class="error-message" v-if="errors.school">{{ errors.school }}</span>
        </div>

        <div class="form-group" :class="{ 'has-error': errors.province }">
          <label for="province">省份</label>
          <input id="province" v-model="formData.province" type="text" placeholder="请输入所在省份" />
          <span class="error-message" v-if="errors.province">{{ errors.province }}</span>
        </div>

        <div class="form-group">
          <label for="sex">性别</label>
          <select id="sex" v-model="formData.sex">
            <option value="男">男</option>
            <option value="女">女</option>
          </select>
        </div>

        <button type="submit" class="submit-btn" :disabled="isLoading">
          {{ isLoading ? '提交中...' : '提交' }}
        </button>
        <button type="button" @click="testSubmit" class="test-btn">测试提交</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth.js';
import { modifyUser } from '@/api/auth.js';
import { uploadImage } from '@/api/teacher/article.js';

const router = useRouter();
const authStore = useAuthStore();
const isLoading = ref(false);
const errors = reactive({});
const avatarPreview = ref(null);
const avatarFile = ref(null);
const avatarInput = ref(null);

const formData = reactive({
  email: '',
  password: '',
  checkPassword: '',
  userName: '',
  // userAvatar: '',
  userProfile: '',
  userRole: 'student',
  phone: '',
  school: '',
  province: '',
  sex: '男',
});

const isTeacher = ref(authStore.user?.userRole === 'teacher');

onMounted(() => {
  const user = authStore.user || {};
  Object.assign(formData, {
    email: user.email || '',
    password: '',
    checkPassword: '',
    userName: user.userName || '',
    // userAvatar: user.userAvatar || '',
    userProfile: user.userProfile || '',
    userRole: user.userRole || 'student',
    phone: user.phone || '',
    school: user.school || '',
    province: user.province || '',
    sex: user.sex || '男',
  });
  if (formData.userAvatar) {
    avatarPreview.value = formData.userAvatar;
  }
});

const handleAvatarChange = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
  if (!validTypes.includes(file.type)) {
    alert('只支持JPEG/PNG/GIF格式的图片');
    avatarInput.value.value = '';
    return;
  }
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过2MB');
    avatarInput.value.value = '';
    return;
  }

  avatarFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    avatarPreview.value = e.target.result;
  };
  reader.readAsDataURL(file);
};

const validateForm = () => {
  Object.keys(errors).forEach((key) => delete errors[key]);

  const rules = {
    email: [
      { test: (v) => !!v, message: '邮箱不能为空' },
      { test: (v) => /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(v), message: '请输入有效的邮箱地址' },
    ],
    password: [
      { test: (v) => !v || v.length >= 6, message: '密码长度至少6位' },
      { test: (v) => !v || v.length <= 20, message: '密码长度不能超过20位' },
    ],
    checkPassword: [
      { test: (v) => !formData.password || v === formData.password, message: '两次密码不一致' },
    ],
    userName: [
      { test: (v) => !!v, message: '用户名不能为空' },
      { test: (v) => /^[a-zA-Z0-9_]{3,16}$/.test(v), message: '用户名需3-16位字母数字或下划线' },
    ],
    phone: [
      { test: (v) => !v || /^1[3-9]\d{9}$/.test(v), message: '请输入有效的11位手机号' },
    ],
    school: [
      { test: (v) => !v || v.length <= 50, message: '学校名称不能超过50个字符' },
    ],
    province: [
      { test: (v) => !v || v.length <= 20, message: '省份名称不能超过20个字符' },
    ],
    userRole: [
      { test: (v) => !!v, message: '角色不能为空' },
    ],
  };

  let isValid = true;
  Object.keys(rules).forEach((key) => {
    const value = formData[key];
    rules[key].forEach((rule) => {
      if (!rule.test(value)) {
        errors[key] = rule.message;
        isValid = false;
      }
    });
  });

  return isValid;
};

const uploadAvatar = async (file) => {
  const formDataToUpload = new FormData();
  formDataToUpload.append('avatar', file);

  try {
    const response = await uploadImage(formDataToUpload);
    return response.url;
  } catch (error) {
    throw new Error('头像上传失败: ' + (error.message || '未知错误'));
  }
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  isLoading.value = true;
  try {
    let uploadedAvatarUrl = formData.userAvatar;

    if (avatarFile.value) {
      uploadedAvatarUrl = await uploadAvatar(avatarFile.value);
      formData.userAvatar = uploadedAvatarUrl;
    }

    const submitData = {
      email: formData.email,
      password: formData.password || undefined,
      userName: formData.userName,
      // userAvatar: formData.userAvatar || undefined,
      userProfile: formData.userProfile || undefined,
      userRole: formData.userRole,
      phone: formData.phone || undefined,
      school: formData.school || undefined,
      province: formData.province || undefined,
      sex: formData.sex,
    };

    const response = await modifyUser(submitData);
    authStore.setUser({ ...authStore.user, ...response });
    localStorage.setItem('isProfileComplete', 'true');
    alert('信息提交成功');

    await router.push('/admin');
  } catch (error) {
    const errorMessage = error.message || '提交失败，请稍后重试';
    alert(errorMessage);
    console.error('提交错误:', error);
  } finally {
    isLoading.value = false;
  }
};

const testSubmit = async () => {
  const testData = {
    email: 'maintainer@example.com',
    password: '',
    userName: 'ericwei',
    userAvatar: '/assets/placeholder.svg',
    userProfile: '八嘎',
    userRole: authStore.user?.userRole || 'student',
    phone: '00000000000',
    school: '北京大学',
    province: '河南',
    sex: '男',
  };
  try {
    const response = await modifyUser(testData);
    console.log('测试提交结果:', response);
    alert('测试提交成功');
  } catch (error) {
    console.error('测试提交失败:', error);
    alert('测试提交失败: ' + (error.message || '未知错误'));
  }
};
</script>

<style scoped>
.complete-profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  width: 100%;
  max-width: 500px;
}

.title {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
}

input,
select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input:focus,
select:focus {
  outline: none;
  border-color: #4caf50;
}

button.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #4caf50;
  color: white;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

button.submit-btn:hover:not(:disabled) {
  background-color: #45a049;
}

button.submit-btn:disabled {
  background-color: #bbb;
  cursor: not-allowed;
}

button.test-btn {
  width: 100%;
  padding: 0.75rem;
  background-color: #2196f3;
  color: white;
  font-size: 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
  transition: background-color 0.2s;
}

button.test-btn:hover {
  background-color: #1976d2;
}

.error-message {
  color: #d32f2f;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  display: block;
}

.has-error input,
.has-error select {
  border-color: #d32f2f;
}

.avatar-upload {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-upload img.avatar-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 0.5rem;
  border: 1px solid #ddd;
}

.upload-tip {
  font-size: 0.875rem;
  color: #888;
  text-align: center;
}
</style>
