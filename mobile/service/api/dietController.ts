// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /diet/detail/${param0} */
export async function getDietPages2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDietPages2Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/diet/detail/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /diet/pages */
export async function getDietPages1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDietPages1Params,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/diet/pages', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}
