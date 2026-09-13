// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /device/${param0} */
export async function checkEmail1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.checkEmail1Params,
  options?: { [key: string]: any }
) {
  const { deviceId: param0, ...queryParams } = params
  return request<API.Result>(`/device/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
