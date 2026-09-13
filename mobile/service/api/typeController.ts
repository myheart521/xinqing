// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /type */
export async function selectAll(options?: { [key: string]: any }) {
  return request<API.Result>('/type', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /type/feedback */
export async function getFeedback(options?: { [key: string]: any }) {
  return request<API.ResultObject>('/type/feedback', {
    method: 'GET',
    ...(options || {}),
  })
}
