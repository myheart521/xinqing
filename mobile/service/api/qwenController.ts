// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /qwen/stream */
export async function stream(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamParams,
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/stream', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/stream/flux */
export async function streamFlux(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamFluxParams,
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/stream/flux', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/stream/token */
export async function streamToken(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamTokenParams,
  options?: { [key: string]: any }
) {
  return request<API.TokenStream>('/qwen/stream/token', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /qwen/web/stream/flux */
export async function streamFluxWeb(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.streamFluxWebParams,
  options?: { [key: string]: any }
) {
  return request<string[]>('/qwen/web/stream/flux', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
