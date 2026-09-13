import {request} from '@/utils/request';

export function getRecord(params) {
    return request({
        url: '/warning/record',
        method: 'GET',
        params
    })
}

export function getWarning(params) {
    return request({
        url: '/warning/warning',
        method: 'GET',
        params
    })
}


export function getWarningStudent(params) {
    return request({
        url: '/warning/warning/student',
        method: 'GET',
        params
    })
}

export function update(params) {
    return request({
        url: '/warning/update/warning',
        method: 'PUT',
        params
    })
}

export function getStudentTestReport(params) {
    return request({
        url: '/historyTest/admin/dataReport',
        method: 'GET',
        params
    })
}

