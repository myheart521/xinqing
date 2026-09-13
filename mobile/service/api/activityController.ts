// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /activity/${param0} */
export async function selectById2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectById2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/activity/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /activity/${param0} */
export async function delectById2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.delectById2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/activity/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /activity/add */
export async function add3(body: API.ActivityDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/activity/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /activity/all */
export async function selectAll1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectAll1Params,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/activity/all', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}
