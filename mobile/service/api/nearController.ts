// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /near/blog */
export async function queryBlogByGeo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryBlogByGeoParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/near/blog', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',

      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /near/user */
export async function queryUserByGeo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryUserByGeoParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/near/user', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
