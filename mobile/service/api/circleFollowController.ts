// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 PUT /circle-follow/${param0}/${param1} */
export async function follow1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.follow1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, isFollow: param1, ...queryParams } = params
  return request<API.Result>(`/circle-follow/${param0}/${param1}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /circle-follow/list */
export async function getList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getListParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/circle-follow/list', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /circle-follow/or/not/${param0} */
export async function followOrNot1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.followOrNot1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/circle-follow/or/not/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
