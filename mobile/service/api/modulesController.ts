// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /modules/pages */
export async function getDefaultTest(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDefaultTestParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultListModulesPageVO>('/modules/pages', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}
