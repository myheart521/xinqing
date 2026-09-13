// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /equipment/create/${param0} */
export async function createEquipment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.createEquipmentParams,
  options?: { [key: string]: any }
) {
  const { deviceType: param0, ...queryParams } = params
  return request<API.Result>(`/equipment/create/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}
