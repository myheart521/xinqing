import { PCCIntroModel } from "../../model/function/pcc/PCCIntroModel"
import { PCCModel } from "../../model/function/pcc/PCCModel"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "./MotionApi"


export const pccInfo=(pccId:number)=>{
    return axiosAPI.get<ApiResponse<PCCIntroModel>>({
        url:"http://localhost:8080/introduction/pcc/"+pccId
      })
}

export const pccData = () =>{
  return axiosAPI.get<ApiResponse<PCCModel[]>>({
    url:"http://localhost:8080/introduction/pcc"
  })
}