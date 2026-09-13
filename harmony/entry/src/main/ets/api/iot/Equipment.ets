import { equipmentType } from "../../model/iot/Equipment"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"

//请求设备
export const getEquipment=()=>{
  return axiosAPI.get<ApiResponse<equipmentType[]>>({
    url:"equipment/select"
  })
}

//添加新设备
export const addEquipment=(equipment:string)=>{
  return axiosAPI.get<ApiResponse<null>>({
    url:`equipment/create/${equipment}`
  })
}