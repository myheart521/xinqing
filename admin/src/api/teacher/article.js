import axios, {post} from '@/utils/request';
import {request} from '@/utils/request';
import {requestUrl} from "@/utils/URL.js";

axios.defaults.headers.post['Content-Type'] = 'application/json';

// 通用错误处理函数
const handleError = (error, message) => {
    console.error(message, error);
    if (error.response) {
        console.error('响应内容:', error.response.data);
    }
    throw new Error(error.response?.data?.msg || message);
};

// 获取 token
const getToken = () => localStorage.getItem('accessToken');

// 添加请求拦截器，自动携带 token
axios.interceptors.request.use(
    (config) => {
        const token = getToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export const uploadImage = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await request({
            url: '/common/upload',
            method: 'POST',
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });

        // response.data 直接是 URL，response.code 是 1 表示成功
        if (response.code === 1 && response.data) {
            console.log('图片上传成功:', response.data);
            return response.data; // ✅ 直接返回图片 URL
        } else {
            console.warn('图片上传失败:', response);
            throw new Error(response.msg || '上传失败');
        }
    } catch (error) {
        console.error('图片上传失败:', error);
        throw new Error('上传失败');
    }
};


// 视频上传接口
export const uploadVideo = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await axios.post('/teacher/upload/video', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        if (response.data.code === 1 && response.data.data?.url) {
            console.log('视频上传成功:', response.data.data.url);
            return response.data.data.url;
        } else {
            console.warn('视频上传失败:', response.data);
            return null;
        }
    } catch (error) {
        handleError(error, '视频上传失败');
    }
};

// 发布文章接口
export const publishArticle = async (articleData) => {
    try {
        const response = await request({
            url: '/teacher/publish/article',
            method: 'post', // 确保使用 POST 方法
            data: articleData, // 提交的数据
            successCode: 1 // 假设后端返回的 success code 是 1
        });
        if (response.code === 1) {
            console.log('文章发布成功:', response.data);
            return true;
        } else {
            console.warn('文章发布失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '发布文章时出错');
    }
};

// 删除文章接口
export const deleteArticle = async (articleId) => {
    try {
        const response = await request(`/teacher/article/${articleId}`);
        if (response.data.code === 1) {
            console.log('文章删除成功:', response.data);
            return true;
        } else {
            console.warn('文章删除失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '删除文章时出错');
    }
};

// 发布食谱接口
export const publishDiet = async (data) => {
    try {
        const response = await request({
            url: '/teacher/publish/diet',
            method: 'post', // 确保使用 POST 方法
            data: data, // 提交的数据
            successCode: 1
        });
        if (response.code === 1) {
            console.log('食谱发布成功:', response.data);
            return true;
        } else {
            console.warn('食谱发布失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '发布食谱时出错');
    }
};

// 删除食谱接口
export const deleteDiet = async (dietId) => {
    try {
        const response = await request(`/teacher/diet/${dietId}`);

        if (response.data.code === 1) {
            console.log('食谱删除成功:', response.data);
            return true;
        } else {
            console.warn('食谱删除失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '删除食谱时出错');
    }
};


// 获取知识文章
export const getKnowledgeArticles = async (params) => {
    try {
        const response = await request(requestUrl + '/knowledge/new/pages', {
            params, // { page, size }
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token'),
            },
        });
        return response.data;
    } catch (error) {
        console.error('获取知识文章失败', error);
        throw error;
    }
};

// 获取食谱文章
export const getDietArticles = async (params) => {
    try {
        const response = await request(requestUrl + '/diet/pages', {
            params, // { page, size }
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token'),
            },
        });
        return response.data;
    } catch (error) {
        console.error('获取食谱文章失败', error);
        throw error;
    }
};

// 获取轮播图接口
export async function getBanners() {
    try {
        const response = await request({
            url: '/teacher/banner',
            method: 'GET'
        });
        console.log('getBanners response:', response);
        return response;
    } catch (error) {
        console.log('轮播图获取失败:', error);
        throw new Error('获取轮播图时出错');
    }
}

//发布轮播图
export const publishBanner = async (bannerData) => {
    try {
        const response = await request({
            url: '/teacher/publish/banner',
            method: 'POST',
            data: bannerData,
        });
        if (response.code === 1) {
            console.log('轮播图发布成功:', response.data);
            return true;
        } else {
            console.warn('轮播图发布失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        console.error('发布轮播图时出错', error);
        console.log('响应内容:', error.response.data);
        throw error;
    }
};


// 删除轮播图接口
export async function deleteBanner(bannerId) {
    if (!bannerId || isNaN(bannerId)) {
        throw new Error('Banner ID 无效');
    }
    try {
        const response = await axios.delete(`/teacher/banner/${bannerId}`);
        const result = response.data;
        if (response.code === 1) {
            return true; // Success
        } else {
            throw new Error(result.msg || '删除Banner失败');
        }
    } catch (error) {
        throw new Error(error.response?.data?.msg || error.message || '删除Banner失败');
    }
}

// 获取卡片轮播图接口
export async function getCards() {
    try {
        const response = await request({
            url: '/teacher/card',
            method: 'GET'
        });
        console.log('getCards response:', response);
        return response; // 返回 data 数组
    } catch (error) {
        console.log('卡片轮播图获取失败:', error);
        throw new Error('获取卡片轮播图时出错');
    }
}

// 发布卡片轮播图接口
export const publishCard = async (cardData) => {
    try {
        // 确保使用 POST 方法发送请求
        const response = await request({
            url: '/teacher/publish/card',
            method: 'POST',  // 明确使用 POST 方法
            data: cardData,  // 请求体
        });

        // 检查响应数据
        if (response.code === 1) {
            console.log('卡片轮播图发布成功:', response.data);
            return true;
        } else {
            console.warn('卡片轮播图发布失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '发布卡片轮播图时出错');
        return false; // 返回失败状态
    }
};


// 删除卡片轮播图接口
export const deleteCard = async (cardId) => {
    try {
        const response = await request({
            url: `/teacher/card/${cardId}`,
            method: 'DELETE', // 明确指定 DELETE 方法
        });
        if (response.code === 1) {
            console.log('卡片轮播图删除成功:', response.data);
            return true;
        } else {
            console.warn('卡片轮播图删除失败:', response.data.msg);
            return false;
        }
    } catch (error) {
        handleError(error, '删除卡片轮播图时出错');
    }
};


