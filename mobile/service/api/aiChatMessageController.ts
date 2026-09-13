// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /ai-chat-message/selectMemory */
export async function selectMemory(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectMemoryParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/ai-chat-message/selectMemory', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
