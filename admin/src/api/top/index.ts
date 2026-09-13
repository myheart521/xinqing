// src/api/top/index.ts
import axios from 'axios';
import {requestUrl} from "@/utils/URL";

// 定义后端返回的用户信息类型
interface UserData {
    id: number;
    userName: string;
    userAvatar: string;
    userProfile: string | null;
    roleId: number;
    school: string | null;
    studentNumber: string | null;
    email: string;
    sex: string | null;
}

// 前端需要的类型
interface UserInfo {
    id: number;
    avatar: string;
    name: string;
    email: string;
    stuNumber: string;
    sex: string;
    school: string;
    userProfile: string | null;  // 新增：用户个人简介
}

export const fetchUserInfo = async (token: string): Promise<UserInfo> => {
    try {

        // 调用后端接口获取用户数据
        const response = await axios.get(requestUrl + '/user/queryUser', {
            headers: {
                token: `${token}` // 添加 token
            }
        });

        console.log('fetchUserInfo response:', response.data); // 调试输出

        // 提取 data 字段
        const data: UserData = response.data.data;

        // 映射到前端需要的格式
        return {
            id: data.id || 0,  // 用户ID
            avatar: data.userAvatar || '',  // 用户头像
            name: data.userName || '',  // 用户名
            email: data.email || '',  // 邮箱
            stuNumber: data.studentNumber || '',  // 学号
            sex: data.sex || '',  // 性别
            school: data.school || '',  // 学校
            userProfile: data.userProfile || null,  // 用户简介
        };
    } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
            console.error('Error response:', error.response.data);
            throw new Error(
                `获取用户信息失败: ${error.response.status} - ${error.response.data?.msg || error.message}`
            );
        } else {
            console.error('Network or unknown error:', error);
            throw new Error('获取用户信息失败，请检查网络或后端服务');
        }
    }
};
