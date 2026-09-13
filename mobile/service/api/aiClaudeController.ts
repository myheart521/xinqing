// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /claude/ai */
export async function generation(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.generationParams,
  options?: { [key: string]: any }
) {
  return request<string>('/claude/ai', {
    method: 'GET',
    params: {
      // message has a default value: 你好
      message: '你好',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /claude/analysis */
export async function analysis(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.analysisParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultMentalAnalysisResult>('/claude/analysis', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /claude/stream */
export async function generationStream(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.generationStreamParams,
  options?: { [key: string]: any }
) {
  return request<string[]>('/claude/stream', {
    method: 'GET',
    params: {
      // message has a default value: 你好
      message: '你好',
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /claude/stream/t */
export async function streamTest(options?: { [key: string]: any }) {
  return request<string[]>('/claude/stream/t', {
    method: 'GET',
    ...(options || {}),
  })
}
