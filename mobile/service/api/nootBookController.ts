// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 DELETE /nootBook */
export async function deleteUsingDelete(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteUsingDELETEParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/nootBook', {
    method: 'DELETE',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /nootBook/${param0} */
export async function queryById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.queryByIdParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/nootBook/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /nootBook/add */
export async function add(body: API.NootBook, options?: { [key: string]: any }) {
  return request<API.Result>('/nootBook/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /nootBook/list */
export async function list1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.list1Params,
  options?: { [key: string]: any }
) {
  return request<API.PageResult>('/nootBook/list', {
    method: 'GET',
    params: {
      ...params,
      nootBookDTO: undefined,
      ...params['nootBookDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /nootBook/update */
export async function update(body: API.NootBook, options?: { [key: string]: any }) {
  return request<API.Result>('/nootBook/update', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
