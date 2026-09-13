// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 POST /comments/add */
export async function add1(body: API.Comments, options?: { [key: string]: any }) {
  return request<API.Result>('/comments/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /comments/delect/${param0} */
export async function delectById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.delectByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/comments/delect/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /comments/first */
export async function selectFirstComments(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectFirstCommentsParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/comments/first', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /comments/forUser */
export async function selectBlogByUserId(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectBlogByUserIdParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/comments/forUser', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /comments/like/${param0} */
export async function likeBlog(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeBlogParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/comments/like/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /comments/two */
export async function selectTwoComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.selectTwoCommentParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/comments/two', {
    method: 'GET',
    params: {
      // current has a default value: 1
      current: '1',
      ...params,
    },
    ...(options || {}),
  })
}
