// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'
import {Uni} from '@dcloudio/types';
// @ts-ignore
import {requestUrl} from '@/utils/URL'

declare const uni: Uni;

/** 此处后端没有提供注释 GET /qwen/stream */
export async function stream(
  params: { prompt: string },
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/stream', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/stream/flux */
export async function streamFlux(
  params: { prompt: string, memoryId: string },
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/stream/flux', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/stream/token */
export async function streamToken(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamTokenParams,
  options?: { [key: string]: any }
) {
  return request<API.TokenStream>('/qwen/stream/token', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/web/stream/flux */
export async function streamFluxWeb(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamFluxWebParams,
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/web/stream/flux', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/**
 * ai生成的但是有点问题
 * @param params
 */
export async function streamFluxRaw(params: { prompt: string, memoryId: string }) {
  return new Promise((resolve, reject) => {

    // 构建URL查询参数（不使用URLSearchParams）
    const url = `${requestUrl}/qwen/stream/flux?prompt=${encodeURIComponent(params.prompt)}&memoryId=${encodeURIComponent(params.memoryId)}`;
    
    // 使用uni.request替代fetch
    uni.request({
      url: url,
      method: 'GET',
      header: {
        'token': uni.getStorageSync('token') ? `${uni.getStorageSync('token')}` : '',
      },
      success: (res) => {
        resolve(res.data);
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '请求失败'));
      }
    });
  });
}

/**
 * 使用uni.request的任务方式获取流式响应
 * @param params 包含prompt和memoryId的参数对象
 * @param onChunkReceived 接收数据块的回调函数
 * @param onComplete 完成回调
 * @param onError 错误回调
 */
export function requestTask(
  params: { prompt: string, memoryId: string },
  onChunkReceived?: (text: string) => void,
  onComplete?: (fullText?: string) => void,
  onError?: (error: any) => void
) {
  // 获取基础URL
  const baseUrl = requestUrl
  
  // 构建完整URL
  const url = `${baseUrl}/qwen/stream/flux?prompt=${encodeURIComponent(params.prompt)}&memoryId=${encodeURIComponent(params.memoryId)}`;
  
  // 创建请求任务
  const task: any = uni.request({
    url: url,
    timeout: 60000, // 60秒超时，流式响应可能需要更长时间
    responseType: 'text',
    method: 'GET',
    enableChunked: true, // 启用分块传输，关键配置；部分平台（H5/APP）可能不支持
    header: {
      'token': uni.getStorageSync('token') || '',
      'content-type': 'application/json'
    },
    success: (response) => {
      console.log('请求成功完成:', response);

      // 当平台不支持 onChunkReceived 时，直接一次性返回完整内容
      let fullText = '';
      try {
        const { data } = response as any;
        if (typeof data === 'string') {
          fullText = data;
        } else if (Array.isArray(data)) {
          fullText = data.join('');
        } else if (data !== undefined) {
          fullText = JSON.stringify(data);
        }
      } catch (e) {
        console.warn('解析响应数据失败:', e);
      }

      if (fullText && onChunkReceived) onChunkReceived(fullText);
      if (onComplete) onComplete(fullText);
    },
    fail: (error) => {
      console.error('请求失败:', error);
      if (onError) onError(error);
    }
  });
  
  // 部分平台（如安卓 App）uni.request 返回的 task 可能不包含 onChunkReceived/onHeadersReceived
  if (task && typeof task.onHeadersReceived === 'function') {
    task.onHeadersReceived(function(res: any) {
      console.log('收到响应头:', res.header);
    });
  }
  
  if (task && typeof task.onChunkReceived === 'function') {
    // 仅当平台支持分块传输时才监听
    task.onChunkReceived(function(res: any) {
      try {
        // 解码二进制数据
        const decoder = new TextDecoder('utf-8');
        const text = decoder.decode(new Uint8Array(res.data));
        // 调用回调处理数据块
        if (onChunkReceived) onChunkReceived(text);
      } catch (error) {
        console.error('处理数据块错误:', error);
      }
    });
  }
  
  return task;
}