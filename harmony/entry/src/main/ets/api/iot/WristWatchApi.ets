import { runningChart, runningModel } from "../../model/iot/Running"
import { wristWatchChart, wristWatchModel } from "../../model/iot/WristWatch"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"

//手表更新
export const watchUpdate=()=>{
  return axiosAPI.get<ApiResponse<wristWatchModel>>({
    url:"smartWatch/smartWatch"
  })
}

//手表请求数据
export const watchGet=()=>{
  return axiosAPI.get<ApiResponse<wristWatchModel>>({
    url:"smartWatch/getNewDate/smartWatch"
  })
}

//手表请求图表数据
export const watchChartData=(begin:string,end:string)=>{
  return axiosAPI.get<ApiResponse<wristWatchChart>>(
    {
      url:"smartWatch/data",
      params:{
        begin:begin,
        end:end
      }
    }
  )
}