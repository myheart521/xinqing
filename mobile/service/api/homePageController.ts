// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 GET /home/banners */
export async function getBanner1(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getBanner1Params,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/home/banners', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /home/swiper */
export async function getSwiper(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSwiperParams,
  options?: { [key: string]: any }
) {
  return request<API.Result>('/home/swiper', {
    method: 'GET',
    params: {
      ...params,
      pageDTO: undefined,
      ...params['pageDTO'],
    },
    ...(options || {}),
  })
}
