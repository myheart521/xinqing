import { request } from '@/utils/request';

// 教师查询预约表
export const getAppointmentTable = (params) => {
  return request({
    url: '/appointment/teacher', // 确保路径正确
    method: 'get',
    params
  });
};

export const update = (data) => {
  return request({
    url: '/appointment/update', // 确保路径正确
    method: 'put',
    data
  });
};




