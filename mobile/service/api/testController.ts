// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /questions/${param0} */
export async function getAll1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAll1Params,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.ResultListQuestionVO>(`/questions/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /questions/submit */
export async function submit1(body: API.QuestionSubmitDTO, options?: { [key: string]: any }) {
  return request<API.ResultAnswerVO>('/questions/submit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
