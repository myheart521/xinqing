// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /blog/${param0} */
export async function selectById1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectById1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/blog/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /blog/${param0} */
export async function delectById1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.delectById1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/blog/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /blog/add */
export async function add2(body: API.BlogDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/blog/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/dateMessage/${param0} */
export async function queryUserdataMessage(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryUserdataMessageParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params
  return request<API.Result>(`/blog/dateMessage/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /blog/like/${param0} */
export async function likeBlog1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeBlog1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/blog/like/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/likes/${param0} */
export async function queryLikedUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryLikedUserParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/blog/likes/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/myLike */
export async function queryMyLike(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryMyLikeParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/blog/myLike', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/searchContent */
export async function searchByContent(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.searchByContentParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/blog/searchContent', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/searchTag */
export async function searchByTag(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.searchByTagParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/blog/searchTag', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/searchUserId */
export async function selectByUserId(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectByUserIdParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/blog/searchUserId', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /blog/user/${param0} */
export async function queryUserDate(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryUserDateParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params
  return request<API.Result>(`/blog/user/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
