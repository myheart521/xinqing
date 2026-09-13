// src/utils/http.ts
import {Uni} from '@dcloudio/types';
import loading from './loading';

declare const uni: Uni;

// 通用请求配置类型
type RequestConfig<T = any> = {
    url: string;
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
    params?: Record<string, any>;  // URL query 参数
    data?: T;                      // Body 数据
    headers?: Record<string, string>;
    showLoading?: boolean;         // 是否显示loading
    [key: string]: any;            // 其他配置项
};

// 基础响应类型
interface ApiResult<T = any> {
    code: number;
    msg?: string;
    data?: T;
}
import {requestUrl} from '@/utils/URL'
// 环境配置
const BASE_URL = requestUrl;

// 请求错误处理
const handleError = (error: any) => {
    // 统一的错误处理
    let message = '';
    if (typeof error === 'string') {
        message = error;
    } else if (error instanceof Error) {
        message = error.message;
    } else {
        message = '未知错误';
    }

    // 显示错误提示
    uni.showToast({
        title: message,
        icon: 'none',
        duration: 2000
    });

    return Promise.reject(error);
};

/**
 * 通用请求封装
 * @param url 请求路径
 * @param config 请求配置
 * @returns Promise<ApiResult>
 */
export function request<T = any>(url: string, config: RequestConfig): Promise<ApiResult<T>> {
    // 默认显示loading
    if (config.showLoading !== false) {
        loading.show({
            text: '加载中...',
            mask: true
        });
    }

    // 1. 合并 Header
    const headers = {
        'Content-Type': 'application/json',
        'token': uni.getStorageSync('token') || '',
        ...config.headers,
    };

    // 2. 处理 query 参数
    let finalUrl = url;
    if (config.params) {
        const query = Object.entries(config.params)
            .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
            .join('&');
        if (query) {
            finalUrl += `?${query}`;
        }
    }

    // 3. 发起请求
    // @ts-ignore
    return new Promise((resolve, reject) => {
        uni.request({
            url: `${BASE_URL}${finalUrl}`,
            method: config.method,
            header: headers,
            data: config.data || {},
            success: (res) => {
                if (res.data.code===1) {
                    resolve(res.data as ApiResult<T>);
                } else {
                    // 显示错误提示
                    uni.showToast({
                        title: res.data.msg,
                        icon: 'none',
                        duration: 2000
                    });
                    reject(new Error(`请求失败: ${res.statusCode}`));
                }
            },
            fail: (err) => {
                reject(new Error(`网络错误: ${err.errMsg}`));
            },
            complete: () => {
                // 请求完成后隐藏loading
                if (config.showLoading !== false) {
                    loading.hide();
                }
            }
        });
    });
}

export default request;
