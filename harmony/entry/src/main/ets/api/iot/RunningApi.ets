
import { runningChart, runningModel } from "../../model/iot/Running"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"

//跑步机更新
export const runningUpdate=()=>{
  return axiosAPI.get<ApiResponse<runningModel>>({
    url:"running/update"
  })
}

//跑步机请求数据
export const runningGet=()=>{
  return axiosAPI.get<ApiResponse<runningModel>>({
    url:"running/select"
  })
}

//请求图表数据
export const runningChartData=(begin:string,end:string)=>{
  return axiosAPI.get<ApiResponse<runningChart>>(
    {
      url:"running/dataReport",
      params:{
        begin:begin,
        end:end
      }
    }
  )
}