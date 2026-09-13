// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 PUT /fellow/${param0}/${param1} */
export async function follow(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.followParams,
  options?: { [key: string]: any }
) {
  const { id: param0, isFollow: param1, ...queryParams } = params
  return request<API.Result>(`/fellow/${param0}/${param1}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /fellow/follow/list */
export async function followerList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.followerListParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/fellow/follow/list', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /fellow/or/not/${param0} */
export async function followOrNot(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.followOrNotParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/fellow/or/not/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
