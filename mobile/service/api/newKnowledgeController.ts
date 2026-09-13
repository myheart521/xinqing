// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /knowledge/new/${param0} */
export async function getDietPages(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getDietPagesParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/knowledge/new/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /knowledge/new/collection/${param0} */
export async function collection(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.collectionParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/knowledge/new/collection/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /knowledge/new/hot */
export async function getHotKnowledge(options?: { [key: string]: any }) {
  return request<API.ResultListHotArticleVO>('/knowledge/new/hot', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 PUT /knowledge/new/like/${param0} */
export async function like(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.Result>(`/knowledge/new/like/${param0}`, {
    method: 'PUT',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /knowledge/new/pages */
export async function getKnowledgePages(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getKnowledgePagesParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/knowledge/new/pages', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /knowledge/new/pages/collections */
export async function getKnowledgeCollectionsPages(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getKnowledgeCollectionsPagesParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/knowledge/new/pages/collections', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}
