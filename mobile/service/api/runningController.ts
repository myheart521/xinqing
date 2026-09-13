// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /running/dataReport */
export async function dataReport1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.dataReport1Params,
  options?: { [key: string]: any }
) {
  return request<API.ResultRunningReportVO>('/running/dataReport', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /running/select */
export async function select2(options?: { [key: string]: any }) {
  return request<API.ResultRunning>('/running/select', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /running/update */
export async function update1(options?: { [key: string]: any }) {
  return request<API.ResultRunning>('/running/update', {
    method: 'GET',
    ...(options || {}),
  })
}
