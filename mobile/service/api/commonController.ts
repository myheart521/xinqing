// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 POST /common/upload */
export async function upload(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.uploadParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultString>('/common/upload', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
