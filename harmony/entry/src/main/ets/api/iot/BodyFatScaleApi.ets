import {  bodyFatScaleChart, bodyFatScaleModel } from "../../model/iot/BodyFatScale"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"

//体脂秤更新
export const bodyFatScaleUpdate=()=>{
  return axiosAPI.get<ApiResponse<bodyFatScaleModel>>({
    url:"body/update"
  })
}

//体脂秤请求数据
export const bodyFatScaleGet=()=>{
  return axiosAPI.get<ApiResponse<bodyFatScaleModel>>({
    url:"body/select"
  })
}

//请求图表数据
export const bodyFatChartData=(begin:string,end:string)=>{
  return axiosAPI.get<ApiResponse<bodyFatScaleChart>>(
    {
      url:"body/data",
      params:{
        begin:begin,
        end:end
      }
    }
  )
}