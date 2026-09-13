import {request} from '@/utils/request';


export function getStatsCards() {
    return request({
        url: '/static/statsCards',
        method: 'GET'
    })
}

export function getPieData() {
    return request({
        url: '/static/pieData',
        method: 'GET'
    })
}

export function getTeacherCommData() {
    return request({
        url: '/static/teacherCommData',
        method: 'GET'
    })
}

export function getEvaluationData() {
    return request({
        url: '/static/evaluationData',
        method: 'GET'
    })
}

export function getPostData() {
    return request({
        url: '/static/postData',
        method: 'GET'
    })
}

export function getActivityData() {
    return request({
        url: '/static/activityData',
        method: 'GET'
    })
}


export function getLine() {
    return request({
        url: '/static/line',
        method: 'GET'
    })
}


export function getRadar() {
    return request({
        url: '/static/radar',
        method: 'GET'
    })
}

export function getCollegePoints() {
    return request({
        url: '/static/collegePoints',
        method: 'GET'
    })
}





