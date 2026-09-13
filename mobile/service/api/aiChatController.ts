// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /chat/history/${param0}/${param1} */
export async function getHistory(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getHistoryParams,
  options?: { [key: string]: any }
) {
  const { receiverId: param0, currentPage: param1, ...queryParams } = params
  return request<API.ResultObject>(`/chat/history/${param0}/${param1}`, {
    method: 'GET',
    params: {
      ...queryParams,
    },
    ...(options || {}),
  })
}
