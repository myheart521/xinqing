import axios from 'axios';

const API_URL = '/api/****';

// 创建 Offer 并发送到后端
export async function createOffer(data) {
  try {
    const response = await axios.post(`${API_URL}/video-chat/offer`, data);
    return response.data;
  } catch (error) {
    console.error('创建 Offer 失败:', error);
    throw error;
  }
}

// 创建 Answer 并发送到后端
export async function createAnswer(data) {
  try {
    const response = await axios.post(`${API_URL}/video-chat/answer`, data);
    return response.data;
  } catch (error) {
    console.error('创建 Answer 失败:', error);
    throw error;
  }
}

// 向后端添加 ICE 候选
export async function addIceCandidate(data) {
  try {
    const response = await axios.post(`${API_URL}/video-chat/ice-candidate`, data);
    return response.data;
  } catch (error) {
    console.error('添加 ICE 候选失败:', error);
    throw error;
  }
}
