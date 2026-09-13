// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /tag/all */
export async function queryTagAll(options?: { [key: string]: any }) {
  return request<API.Result>('/tag/all', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /tag/getTop */
export async function queryTagTop(options?: { [key: string]: any }) {
  return request<API.Result>('/tag/getTop', {
    method: 'GET',
    ...(options || {}),
  })
}
