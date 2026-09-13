// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /teacher/article */
export async function getArticle(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getArticleParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultObject>('/teacher/article', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /teacher/article/${param0} */
export async function deleteArticle(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteArticleParams,
  options?: { [key: string]: any }
) {
  const { articleId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/teacher/article/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /teacher/banner */
export async function getBanner(options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/banner', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /teacher/banner/${param0} */
export async function deleteBanner(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteBannerParams,
  options?: { [key: string]: any }
) {
  const { bannerId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/teacher/banner/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /teacher/card */
export async function getCard(options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/card', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /teacher/card/${param0} */
export async function deleteCard(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteCardParams,
  options?: { [key: string]: any }
) {
  const { cardId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/teacher/card/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 DELETE /teacher/diet/${param0} */
export async function deleteDiet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteDietParams,
  options?: { [key: string]: any }
) {
  const { dietId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/teacher/diet/${param0}`, {
    method: 'DELETE',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /teacher/info/main/${param0} */
export async function getStuInfo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getStuInfoParams,
  options?: { [key: string]: any }
) {
  const { stuId: param0, ...queryParams } = params
  return request<API.ResultObject>(`/teacher/info/main/${param0}`, {
    method: 'GET',
    params: {
      ...queryParams,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /teacher/publish/article */
export async function publishArticle(body: API.NewKnowledgeDTO, options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/publish/article', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /teacher/publish/banner */
export async function publishBanner(body: API.BannerDTO, options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/publish/banner', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /teacher/publish/card */
export async function publishCard(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.publishCardParams,
  options?: { [key: string]: any }
) {
  return request<API.ResultObject>('/teacher/publish/card', {
    method: 'POST',
    params: {
      ...params,
      swiperDTO: undefined,
      ...params['swiperDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /teacher/publish/diet */
export async function publishDiet(body: API.DietDTO, options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/publish/diet', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /teacher/stu/list */
export async function getStuList(options?: { [key: string]: any }) {
  return request<API.ResultObject>('/teacher/stu/list', {
    method: 'GET',
    ...(options || {}),
  })
}
