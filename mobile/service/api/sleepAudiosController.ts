// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /sleep-audios */
export async function select1(options?: { [key: string]: any }) {
  return request<API.Result>('/sleep-audios', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /sleep-audios/${param0} */
export async function updatePlay(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.updatePlayParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/sleep-audios/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}
