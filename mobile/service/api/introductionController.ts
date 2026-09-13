// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /introduction/pcc */
export async function getPcc(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPCCParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultListGetPCCVO>('/introduction/pcc', {
    method: 'GET',
    params: {
      ...params,
      introductionPCCDTO: undefined,
      ...params['introductionPCCDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /introduction/pcc/${param0} */
export async function getPcc1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPCC1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ResultGetPCCDatailVO>(`/introduction/pcc/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
