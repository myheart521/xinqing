// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 此处后端没有提供注释 POST /user/accountLogin */
export async function login1(body: API.AccountLoginDTO, options?: { [key: string]: any }) {
  return request<API.ResultLoginVO>('/user/accountLogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/checkEmail */
export async function checkEmail(body: API.EmailCheckDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/user/checkEmail', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/emaillogin */
export async function emailLogin(body: API.EmailLoginDTO, options?: { [key: string]: any }) {
  return request<API.ResultLoginVO>('/user/emaillogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/feedback */
export async function feedback(body: API.FeedbackDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/user/feedback', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/info */
export async function getUserInfo(options?: { [key: string]: any }) {
  return request<API.ResultMapStringObject>('/user/info', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/info */
export async function modifyUserInfo(body: API.UserInfoDTO, options?: { [key: string]: any }) {
  return request<API.ResultObject>('/user/info', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/modify */
export async function modify(body: API.ModifyUserDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/user/modify', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/province */
export async function province(options?: { [key: string]: any }) {
  return request<API.ResultListProvince>('/user/province', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/province/school */
export async function universities(body: API.ShoolByProvinceDTO, options?: { [key: string]: any }) {
  return request<API.ResultListUniversities>('/user/province/school', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/queryUser */
export async function query(options?: { [key: string]: any }) {
  return request<API.ResultUserVO>('/user/queryUser', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/register */
export async function register(body: API.RegisterDTO, options?: { [key: string]: any }) {
  return request<API.Result>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/wxlogin */
export async function login(body: API.LoginDTO, options?: { [key: string]: any }) {
  return request<API.ResultLoginVO>('/user/wxlogin', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/wxlogin */
export async function getUserCount() {
  return request<API.Result>('/user/count', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
}
