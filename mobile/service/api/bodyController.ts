// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /body/data */
export async function dataReport3(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.dataReport3Params,
  options?: { [key: string]: any }
) {
  return request<API.ResultBodyReportVO>('/body/data', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /body/select */
export async function select3(options?: { [key: string]: any }) {
  return request<API.ResultBody>('/body/select', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /body/test */
export async function test(options?: { [key: string]: any }) {
  return request<API.Result>('/body/test', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /body/update */
export async function update2(options?: { [key: string]: any }) {
  return request<API.ResultBody>('/body/update', {
    method: 'GET',
    ...(options || {}),
  })
}
