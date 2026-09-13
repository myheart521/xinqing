// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /music/pages */
export async function getMusicPages(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getMusicPagesParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/music/pages', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}
