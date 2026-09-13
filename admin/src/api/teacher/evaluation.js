import request from '@/utils/request'

export function getEvaluations(params) {
  return request({
    url: '/teacher/evaluation/list',
    method: 'get',
    params
  })
}

export function exportEvaluations(data) {
  return request({
    url: '/teacher/evaluation/export',
    method: 'post',
    data,
    responseType: 'blob'
  })
}