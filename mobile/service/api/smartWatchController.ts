// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /smartWatch/data */
export async function dataReport(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.dataReportParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultSmartwatchReportVO>('/smartWatch/data', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /smartWatch/getNewDate/${param0} */
export async function getNewDate(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getNewDateParams,
  options?: { [key: string]: any }
) {
  const { deviceType: param0, ...queryParams } = params
  return request<API.ResultSmartWatchVO>(`/smartWatch/getNewDate/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /smartWatch/smartWatch */
export async function getSmartWatch(options?: { [key: string]: any }) {
  return request<API.ResultSmartWatchVO>('/smartWatch/smartWatch', {
    method: 'GET',
    ...(options || {}),
  })
}
