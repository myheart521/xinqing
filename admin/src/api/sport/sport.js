import { request } from "@/utils/request.js";

// 通用错误处理函数
const handleError = (error, message) => {
  console.error(message, error);
  if (error.response) {
    console.error('响应数据:', error.response.data);
  }
  throw new Error(message);
};

// 获取所有 Banner
export const getBanners = async () => {
  try {
    const response = await request.get('/teacher/banners');
    return response.data;
  } catch (error) {
    handleError(error, '获取 Banner 失败');
  }
};

// 获取所有 Swiper
export const getSwiperList = async () => {
  try {
    const response = await request.get('/teacher/swipers');
    return response.data;
  } catch (error) {
    handleError(error, '获取 Swiper 列表失败');
  }
};

// 获取热门文章（暂无接口，保留空函数）
export const getHotArticles = async () => {
  // 暂无接口，返回默认值或空数组
  console.warn("暂无后端接口：getHotArticles");
  return []; // 返回空数组作为默认值
};

// 添加 Banner
export const addBanner = async (data) => {
  try {
    const response = await request.post('/teacher/publish/banner', data);
    return response.data;
  } catch (error) {
    handleError(error, '添加 Banner 失败');
  }
};

// 添加 Swiper
export const addSwiper = async (data) => {
  try {
    const response = await request.post('/teacher/publish/card', data);
    return response.data;
  } catch (error) {
    handleError(error, '添加 Swiper 失败');
    throw error; // 可选：将错误向上抛出，以便调用者可以进一步处理
  }
};

// 添加热门文章（暂无接口，保留空函数）
export const addHotArticle = async (data) => {
  console.warn("暂无后端接口：addHotArticle");
  return { success: false, message: "暂无后端接口" }; // 返回默认的失败响应
};

// 编辑 Banner
export const updateBanner = async (id, data) => {
  try {
    const response = await request.put(`/teacher/banner/${id}`, data);
    return response.data;
  } catch (error) {
    handleError(error, `更新 Banner (ID: ${id}) 失败`);
  }
};

// 编辑 Swiper
export const updateSwiper = async (id, data) => {
  try {
    const response = await request.put(`/teacher/swiper/${id}`, data);
    return response.data;
  } catch (error) {
    handleError(error, `更新 Swiper (ID: ${id}) 失败`);
  }
};

// 编辑热门文章（暂无接口，保留空函数）
export const updateHotArticle = async (id, data) => {
  console.warn("暂无后端接口：updateHotArticle");
  return { success: false, message: "暂无后端接口" }; // 返回默认的失败响应
};

// 删除 Banner
export const deleteBanner = async (id) => {
  try {
    const response = await request.delete(`/teacher/banner/${id}`);
    return response.data;
  } catch (error) {
    handleError(error, `删除 Banner (ID: ${id}) 失败`);
  }
};

// 删除 Swiper
export const deleteSwiper = async (id) => {
  try {
    const response = await request.delete(`/teacher/swiper/${id}`);
    return response.data;
  } catch (error) {
    handleError(error, `删除 Swiper (ID: ${id}) 失败`);
  }
};

// 删除热门文章（暂无接口，保留空函数）
export const deleteHotArticle = async (id) => {
  console.warn("暂无后端接口：deleteHotArticle");
  return { success: false, message: "暂无后端接口" }; // 返回默认的失败响应
};
