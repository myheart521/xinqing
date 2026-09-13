import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import LocalStorage from '@/utils/localStorage';

// 存储键名
const TOKEN_KEY = 'accessToken';
const USER_INFO_KEY = 'userInfo';
const USER_ROLES_KEY = 'userRoles';
const IS_AUTHENTICATED_KEY = 'isAuthenticated';
const USER_ID_KEY = 'userId';

export const useUserStore = defineStore('user', () => {
  // 状态 (state)
  const token = ref(LocalStorage.get(TOKEN_KEY, ''));
  const userInfo = ref(LocalStorage.get(USER_INFO_KEY, {}));
  const roles = ref(LocalStorage.get(USER_ROLES_KEY, []));
  const isAuthenticated = ref(LocalStorage.get(IS_AUTHENTICATED_KEY, false));
  const userId = ref(LocalStorage.get(USER_ID_KEY, null));

  // 计算属性 (getters)
  /**
   * 获取用户名
   */
  const username = computed(() => userInfo.value?.userName || '');
  
  /**
   * 获取用户头像
   */
  const avatar = computed(() => userInfo.value?.userAvatar || '');
  
  /**
   * 判断用户是否已登录
   */
  const isLoggedIn = computed(() => !!token.value && isAuthenticated.value);
  
  /**
   * 获取用户角色ID
   */
  const roleId = computed(() => userInfo.value?.roleId || null);
  
  /**
   * 判断是否为管理员角色
   */
  const isAdmin = computed(() => userInfo.value?.roleId === 1);
  
  /**
   * 获取用户个人资料
   */
  const profile = computed(() => ({
    email: userInfo.value?.email || '',
    phone: userInfo.value?.phone || '',
    school: userInfo.value?.school || '',
    studentNumber: userInfo.value?.studentNumber || '',
    sex: userInfo.value?.sex || '',
    province: userInfo.value?.province || '',
    userProfile: userInfo.value?.userProfile || '',
  }));

  // 方法 (actions)
  /**
   * 将状态同步到 localStorage
   */
  function syncToLocalStorage() {
    LocalStorage.set(TOKEN_KEY, token.value);
    LocalStorage.set(USER_INFO_KEY, userInfo.value);
    LocalStorage.set(USER_ROLES_KEY, roles.value);
    LocalStorage.set(IS_AUTHENTICATED_KEY, isAuthenticated.value);
    LocalStorage.set(USER_ID_KEY, userId.value);
  }
  
  /**
   * 设置用户登录信息
   * @param {Object} loginResponse - 登录响应数据
   */
  function setLoginInfo(loginResponse) {
    if (!loginResponse) return;
    
    const { token: newToken, user } = loginResponse;
    
    // 更新 store 状态
    token.value = newToken;
    userInfo.value = user;
    roles.value = [user.roleId];
    isAuthenticated.value = true;
    userId.value = user.id;
    
    // 同步到 localStorage
    syncToLocalStorage();
  }
  
  /**
   * 更新用户信息
   * @param {Object} newUserInfo - 新的用户信息
   */
  function updateUserInfo(newUserInfo) {
    userInfo.value = { ...userInfo.value, ...newUserInfo };
    LocalStorage.set(USER_INFO_KEY, userInfo.value);
  }
  
  /**
   * 清除登录状态
   */
  function logout() {
    token.value = '';
    userInfo.value = {};
    roles.value = [];
    isAuthenticated.value = false;
    userId.value = null;
    
    // 清除 localStorage 中的登录信息
    LocalStorage.remove(TOKEN_KEY);
    LocalStorage.remove(USER_INFO_KEY);
    LocalStorage.remove(USER_ROLES_KEY);
    LocalStorage.remove(IS_AUTHENTICATED_KEY);
    LocalStorage.remove(USER_ID_KEY);
  }

  // 返回所有状态和方法
  return {
    // 状态
    token,
    userInfo,
    roles,
    isAuthenticated,
    userId,
    
    // 计算属性
    username,
    avatar,
    isLoggedIn,
    roleId,
    isAdmin,
    profile,
    
    // 方法
    setLoginInfo,
    syncToLocalStorage,
    updateUserInfo,
    logout
  };
});