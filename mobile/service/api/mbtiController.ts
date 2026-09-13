// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /manage/getAll */
export async function getAll2(options?: { [key: string]: any }) {
  return request<API.ResultListMbtiTopic>('/manage/getAll', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /manage/submit */
export async function calculateAns(body: API.JSONObject, options?: { [key: string]: any }) {
  return request<API.ResultMbtiTestVO>('/manage/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
