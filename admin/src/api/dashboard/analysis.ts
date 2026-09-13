import request from '../../../../yudao-ui-admin-vue3/src/config/axios'

export interface AnalysisTotal {
  userTotal: number
  messageTotal: number
  transactionTotal: number
  shoppingTotal: number
}

export interface AnalysisChartData {
  pieData: any
  barData: any
  lineData: any
}

// 获取统计数据
export const getAnalysisTotal = () => {
  return request.get<AnalysisTotal>({ url: '/dashboard/analysis/total' })
}

// 获取图表数据
export const getAnalysisChartData = () => {
  return request.get<AnalysisChartData>({ url: '/dashboard/analysis/chart-data' })
}
