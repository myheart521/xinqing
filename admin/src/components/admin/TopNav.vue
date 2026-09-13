<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchUserInfo } from '@/api/top/index';
import { ElMessage } from 'element-plus';

interface UserInfo {
  id: number;
  userName: string;
  avatar: string;
  userProfile: string | null;
  school: string | null;
  studentNumber?: string | null;
  stuNumber?: string | null;
  email: string;
  sex: string | null;
}

const router = useRouter();
const emit = defineEmits(['toggle-sidebar']);
const showDropdown = ref(false);
const sideNavRef = ref(null);

const userInfo = ref<UserInfo>({
  id: 0,
  userName: '',
  avatar: '',
  userProfile: null,
  school: null,
  studentNumber: null,
  stuNumber: null,
  email: '',
  sex: null
});

const fetchUser = async () => {
  try {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      throw new Error('未找到认证令牌，请重新登录');
    }

    const data = await fetchUserInfo(token);
    userInfo.value = {
      id: data.id || 0,
      userName: data.name || '管理员',
      avatar: data.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin',
      userProfile: data.userProfile || null,
      school: data.school || null,
      stuNumber: data.stuNumber || null,
      email: data.email || 'maintainer@example.com',
      sex: data.sex || null,
    };
    
    // 保存用户头像到localStorage
    if (data.avatar) {
      localStorage.setItem('userAvatar', data.avatar);
    }
  } catch (error) {
    console.error('Failed to fetch user info:', error);
    userInfo.value = {
      id: 0,
      userName: '管理员',
      avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin',
      userProfile: null,
      school: null,
      studentNumber: null,
      stuNumber: null,
      email: 'maintainer@example.com',
      sex: null,
    };
    ElMessage.error('获取用户信息失败，请重新登录');
    router.push('/login');
  }
};

const handleToggleSidebar = () => {
  emit('toggle-sidebar');
};

const handleLogout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userInfo');
  localStorage.removeItem('isAuthenticated');
  localStorage.removeItem('requiresProfile');
  localStorage.removeItem('requiresAuth');
  localStorage.removeItem('userAvatar');
  router.push('/login');
};

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

onMounted(() => {
  fetchUser();
  if (sideNavRef.value) {
    sideNavRef.value.isCollapsed = false;
  }
});
</script>

<template>
  <header class="admin-header">
    <div class="header-content">
      <div class="header-left">
        <h1 class="header-title">倾心-教师端</h1>
        <button class="menu-toggle" @click="handleToggleSidebar">
          <span class="menu-icon"></span>
        </button>
      </div>
      <div class="header-right">
        <div class="user-profile" @click="toggleDropdown">
          <div class="avatar">
            <img :src="userInfo.avatar" alt="用户头像" />
          </div>
          <div class="dropdown-menu" v-show="showDropdown">
            <div class="dropdown-item user-info">
              <strong>{{ userInfo.userName }}</strong>
              <div class="user-details">
                <p>邮箱: {{ userInfo.email }}</p>
                <p v-if="userInfo.school">学校: {{ userInfo.school }}</p>
                <p v-if="userInfo.studentNumber">学号: {{ userInfo.studentNumber }}</p>
                <p v-if="userInfo.sex">性别: {{ userInfo.sex }}</p>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <div class="dropdown-item logout" @click="handleLogout">
              <i class="el-icon-switch-button"></i> 退出登录
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.admin-header {
  background: linear-gradient(135deg, #3B82F6, #1E40AF); /* 调整渐变色，使其更柔和 */
  color: white;
  padding: 0 2rem;
  height: 80px; /* 增加高度，让布局更舒适 */
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
  transition: all 0.3s ease;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.header-title {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 2px;
  background: linear-gradient(to right, #ffffff, #d1e0ff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  transition: transform 0.3s ease;
}

.header-title:hover {
  transform: scale(1.08);
}

.header-right {
  margin-right: 1rem;
  padding-right: 5%;
}

.user-profile {
  position: relative;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.9);
  transition: all 0.4s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.avatar:hover {
  transform: rotate(360deg) scale(1.1);
  border-color: #60a5fa;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.75rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  min-width: 280px;
  z-index: 1000;
  overflow: hidden;
  animation: slideDown 0.3s ease-out;
}

.dropdown-item {
  padding: 1rem 1.5rem;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.dropdown-item:hover {
  background: linear-gradient(to right, #eff6ff, #dbeafe);
  transform: translateX(5px);
}

.user-info {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.75rem;
}

.user-info strong {
  font-size: 1.1rem;
  color: #1e3a8a;
}

.user-details p {
  margin: 0;
  color: #4b5563;
  font-size: 0.9rem;
  line-height: 1.5;
}

.dropdown-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 0.5rem 0;
}

.logout {
  color: #dc2626;
  font-weight: 500;
}

.logout:hover {
  background: #fef2f2;
  color: #b91c1c;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.menu-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s ease;
  border-radius: 8px;
}

.menu-toggle:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: rotate(90deg);
}

.menu-icon {
  position: relative;
  width: 24px;
  height: 3px;
  background-color: white;
  border-radius: 2px;
  transition: all 0.4s ease;
}

.menu-icon::before,
.menu-icon::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 3px;
  background-color: white;
  border-radius: 2px;
  transition: all 0.4s ease;
}

.menu-icon::before {
  top: -8px;
}

.menu-icon::after {
  bottom: -8px;
}

.menu-toggle:hover .menu-icon,
.menu-toggle:hover .menu-icon::before,
.menu-toggle:hover .menu-icon::after {
  background-color: #dbeafe;
}
</style>
