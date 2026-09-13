// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /circle/${param0} */
export async function queryById1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryById1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/circle/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /circle/getAll */
export async function getAll3(options?: { [key: string]: any }) {
  return request<API.Result>('/circle/getAll', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /circle/getTop */
export async function getTop(options?: { [key: string]: any }) {
  return request<API.Result>('/circle/getTop', {
    method: 'GET',
    ...(options || {}),
  })
}
