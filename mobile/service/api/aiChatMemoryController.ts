// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /ai-chat-memory/list */
export async function getMemoryList(options?: { [key: string]: any }) {
  return request<API.Result>('/ai-chat-memory/list', {
    method: 'GET',
    ...(options || {}),
  })
}
