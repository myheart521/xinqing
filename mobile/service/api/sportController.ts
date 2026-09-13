// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 POST /sport */
export async function select(body: API.SportDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/sport', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /sport/${param0} */
export async function selectById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/sport/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /sport/watch/${param0} */
export async function watch(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.watchParams,
  options?: { [key: string]: any }
) {
  const { sportId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/sport/watch/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /sport/watch/graph/${param0} */
export async function watchGraph(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.watchGraphParams,
  options?: { [key: string]: any }
) {
  const { scale: param0, ...queryParams } = params
  return request<API.ResultObject>(`/sport/watch/graph/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /sport/watch/history */
export async function watchHistory(options?: { [key: string]: any }) {
  return request<API.ResultObject>('/sport/watch/history', {
    method: 'GET',
    ...(options || {}),
  })
}
