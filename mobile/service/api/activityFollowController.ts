// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 PUT /activity-follow/${param0}/${param1} */
export async function follow2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.follow2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, isFollow: param1, ...queryParams } = params
  return request<API.Result>(`/activity-follow/${param0}/${param1}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /activity-follow/list */
export async function queryFollowList(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryFollowListParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/activity-follow/list', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /activity-follow/or/not/${param0} */
export async function followOrNot2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.followOrNot2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/activity-follow/or/not/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
