// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /test/getAll */
export async function getAll(options?: { [key: string]: any }) {
  return request<API.ResultListPsychologicalTest>('/test/getAll', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /test/submit */
export async function submit(body: API.JSONObject, options?: { [key: string]: any }) {
  return request<API.ResultTestVO>('/test/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
