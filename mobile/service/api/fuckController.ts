// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /fuck/a */
export async function yean(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.yeanParams,
  options?: { [key: string]: any }
) {
  return request<string>('/fuck/a', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /fuck/b */
export async function a(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.aParams,
  options?: { [key: string]: any }
) {
  return request<string>('/fuck/b', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
