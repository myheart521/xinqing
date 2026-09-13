import axios from 'axios';
import { ElMessage } from 'element-plus';
import type { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import {requestUrl} from "@/utils/URL.js";

interface CustomAxiosRequestConfig extends AxiosRequestConfig {
    successCode?: number;  // 可选的成功代码，默认 1
}

const service: axios.AxiosInstance = axios.create({
    baseURL: requestUrl,  // 设置基础路径
    timeout: 10000,  // 设置请求超时
    headers: {
        'Content-Type': 'application/json;charset=utf-8',  // 设置请求头
    }
});

// 请求拦截器
service.interceptors.request.use(
    (config: CustomAxiosRequestConfig) => {

        // 处理绝对URL的情况
        if (config.url && (config.url.startsWith('http://') || config.url.startsWith('https://'))) {
            // 对于绝对URL，不使用baseURL
            config.baseURL = '';
            console.log('检测到绝对URL，已禁用baseURL');
        }

        // 优先使用配置中传入的token
        let token = '';
        if (config.headers && config.headers['token']) {
            token = config.headers['token'];
        } else {
            // 否则从localStorage获取
            token = localStorage.getItem('accessToken') || localStorage.getItem('token') || '';
        }
        
        if (token) {

            config.headers = config.headers || {};  // 确保 headers 不为 undefined
            config.headers['token'] = `${token}`;  // 将 Token 添加到请求头
        } else {
            console.warn('Token 不存在，可能需要登录');
            // 创建一个临时token用于测试，实际环境中应该删除这行
            config.headers = config.headers || {};
        }
        return config;
    },
    (error: AxiosError) => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        const res = response.data;
        const config = response.config as CustomAxiosRequestConfig;
        const successCode = config.successCode || 1;  // 设置成功代码

        // 如果返回的 code 不等于 successCode，则提示错误
        if (res.code !== undefined && res.code !== successCode) {
            const errorMsg = res.msg || '请求失败';
            if (res.msg) {
                ElMessage.error(errorMsg);
            }
            return Promise.reject(new Error(errorMsg));
        }
        return res;  // 返回成功的响应数据
    },
    (error: AxiosError) => {
        ElMessage.error(error.message || '请求异常');  // 弹出错误信息
        return Promise.reject(error);
    }
);

// 直接使用 axios 的 POST 方法
export function post(url: string, data: any, config?: CustomAxiosRequestConfig) {
    return service.post(url, data, config);  // 直接调用 axios 的 post 方法
}

// 直接使用 axios.request 方法，通用
export function request(config: CustomAxiosRequestConfig) {
    return service(config);  // 返回 axios 实例执行的请求
}

export default service;
