import {defineStore} from "pinia";
import defaultAvatar from "@/static/avatar/default.png";
import {
    login1,
    checkEmail,
    register,
    emailLogin,
} from "@/service/api/userController";
import {
    getToken,
    getUserInfo,
    setToken,
    setUserInfo,
    removeToken,
    removeUserInfo,
} from "@/utils/userStorage";

// 从本地存储获取用户信息，如果不存在则使用默认值
const storedUserInfo = uni.getStorageSync('userInfo') || {};
// 定义默认用户信息对象
const defaultUserInfo = {
    id: storedUserInfo.id || null, // 用户ID
    token: storedUserInfo.token || "", // 用户令牌
    school: storedUserInfo.school || "", // 学校
    sex: storedUserInfo.sex || "", // 性别
    studentNumber: storedUserInfo.studentNumber || "", // 学号
    userAvatar: storedUserInfo.userAvatar || defaultAvatar, // 用户头像
    userName: storedUserInfo.userName || "还没有登录", // 用户名
    userProfile: storedUserInfo.userProfile || "还没有留下任何信息", // 用户简介
    email: storedUserInfo.email || "", // 邮箱
    roleId: storedUserInfo.roleId || null, // 角色ID
    phone: storedUserInfo.phone || "", // 电话
    province: storedUserInfo.province || "", // 省份
    avatars: storedUserInfo.avatars || "", // 头像列表
    college: storedUserInfo.college || "", // 学院
    majorClass: storedUserInfo.majorClass || "" // 专业班级
};

export const useUserStore = defineStore("user", {
    state: () => ({
        userInfo: {...defaultUserInfo},
        isLogin: false,
    }),

    getters: {
        // 获取用户头像
        userAvatar: (state) => {
            console.log("11111:", storedUserInfo)
            console.log("state.userInfo?.userAvatar", state.userInfo?.userAvatar);
            return state.userInfo?.userAvatar || defaultAvatar;
        },
        // 获取用户昵称
        userName: (state) => {
            return state.userInfo?.userName || "未登录";
        },
    },

    actions: {
        // 初始化用户信息
        initUserInfo() {
            try {
                const token = getToken();
                const storedUserInfo = getUserInfo();

                if (token && storedUserInfo) {
                    this.userInfo = {
                        ...defaultUserInfo,
                        ...storedUserInfo,
                        token,
                    };
                    this.isLogin = true;
                } else {
                    this.clearUserInfo();
                }
            } catch (error) {
                console.error("初始化用户信息失败:", error);
                this.clearUserInfo();
            }
        },

        // 设置 Token
        setToken(token) {
            this.userInfo.token = token;
            setToken(token);
        },

        // 设置用户信息
        setUserInfo(userInfo) {
            this.userInfo = {
                ...this.userInfo,
                ...userInfo,
            };
            this.isLogin = true;
            setUserInfo(this.userInfo);
        },

        // 账号密码登录
        async accountLogin(account, password) {
            try {
                const res = await login1({account, password});
                if (res.code === 1) {
                    this.setToken(res.data.token);
                    this.setUserInfo(res.data.user);
                    return Promise.resolve(res);
                }
                return Promise.reject(res);
            } catch (error) {
                return Promise.reject(error);
            }
        },

        // 邮箱登录
        async emailLogin(email, password) {
            // 表单验证
            if (!email || !password) {
                uni.showToast({
                    title: "请填写完整登录信息",
                    icon: "none",
                });
                return Promise.reject("表单不完整");
            }

            // 验证QQ邮箱格式
            const emailRegex = /^[1-9][0-9]{4,}@qq\.com$/;
            if (!emailRegex.test(email)) {
                uni.showToast({
                    title: "请输入正确的QQ邮箱格式",
                    icon: "none",
                });
                return Promise.reject("邮箱格式错误");
            }

            try {
                const res = await emailLogin({email, password});
                if (res.code === 1) {
                    // 保存登录状态
                    this.setToken(res.data.token);
                    this.setUserInfo(res.data.user);
                    // 展示登录成功
                    uni.showToast({
                        title: "登录成功",
                        icon: "success",
                    });
                    // 跳转至首页
                    uni.reLaunch({
                        url: "/pages/index/index",
                    });
                    return res; // 直接返回结果，不要包装在 Promise.resolve 中
                } else {
                    uni.showToast({
                        title: res.msg || "登录失败",
                        icon: "none",
                    });
                    throw new Error(res.msg || "登录失败"); // 使用 throw 而不是 Promise.reject
                }
            } catch (error) {
                uni.showToast({
                    title: "登录失败，请重试",
                    icon: "none",
                });
                throw error; // 使用 throw 而不是 Promise.reject
            }
        },

        // 退出登录
        logout() {
            this.userInfo = {
                id: null,
                token: "",
                school: "",
                sex: "",
                studentNumber: "",
                userAvatar: defaultAvatar,
                userName: "示例资料",
                userProfile: "还没有留下任何信息",
                email: "demo@example.invalid",
                roleId: null,
                phone: "13000000000",
                province: "",
                avatars: "",
                college: "",
                majorClass: ""
            };
            this.isLogin = false;
            removeToken();
            removeUserInfo();
            uni.reLaunch({
                url: "/mine_pages/login", // 修改为正确的登录页面路径
            });
        },

        // 检查登录状态
        checkLogin() {
            if (!this.userInfo.token || !this.userInfo) {
                uni.showToast({
                    title: "请先登录",
                    icon: "none",
                });
                uni.navigateTo({
                    url: "/mine_pages/login", // 修改为正确的登录页面路径
                });
                return false;
            }
            return true;
        },

        // 更新用户信息
        updateUserInfo(info) {
            this.userInfo = {
                ...this.userInfo,
                ...info,
            };
        },

        // 设置登录状态
        setLoginState(state) {
            this.isLogin = state;
        },

        // 清除用户信息
        clearUserInfo() {
            this.userInfo = {...defaultUserInfo};
            this.isLogin = false;
            removeToken();
            removeUserInfo();
        },
    },

    persist: {
        enabled: true,
        strategies: [
            {
                key: "user_store",
                storage: {
                    getItem: uni.getStorageSync,
                    setItem: uni.setStorageSync,
                },
                paths: ["userInfo", "isLogin"], // 明确指定需要持久化的字段
            },
        ],
    },
});
