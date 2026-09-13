// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /historyTest/dataReport */
export async function dataReport2(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.dataReport2Params,
  options?: { [key: string]: any }
) {
  return request<API.ResultHistoryTestReportVO>('/historyTest/dataReport', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /historyTest/pages */
export async function getHistoryRecent(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getHistoryRecentParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultListHistoryTestPageVO>('/historyTest/pages', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /historyTest/pages/${param0} */
export async function getHistoryTestDetail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getHistoryTestDetailParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ResultAnswerVO>(`/historyTest/pages/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
