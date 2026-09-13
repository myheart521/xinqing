// src/api.js
import {post, request} from '@/utils/request';

// @/api/auth.js
// 修改用户信息
export async function modifyUser(data) {
    try {
        const token = localStorage.getItem('accessToken'); // 获取 Token
        if (!token) {
            throw new Error('未找到 Token，请先登录');
        }

        const response = await fetch('/dev-api/user/modify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'token': `${token}`, // 使用 Bearer Token 格式
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json(); // 尝试获取后端返回的错误信息
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        return result; // 返回后端响应数据
    } catch (error) {
        console.error('修改用户信息失败:', error);
        throw error; // 抛出错误供调用方处理
    }
}

// 获取学校数据
export async function fetchSchoolData(schoolName) {
    try {
        const response = await request(`/dev-api/users?schoolName=${schoolName}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        return result; // 返回学校相关用户信息
    } catch (error) {
        console.error('获取学校数据失败:', error);
        return mockData[schoolName] || [];
    }
}
