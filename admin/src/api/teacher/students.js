import { request } from '@/utils/request';

export const getUsers = (params) => {
  return request({
    url: '/teacher/stu/list', // 确保路径正确
    method: 'get',
    params
  });
};

export const getAppointmentTable = (params) => {
  return request({
    url: '/appointment/teacher', // 确保路径正确
    method: 'get',
    params
  });
};
