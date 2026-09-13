import KnowledgeItem from "../../model/function/knowledge/KnowledgeItem"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "./MotionApi"


export const getKnowledgeData=()=>{
  return axiosAPI.get<ApiResponse<KnowledgeItem[]>>({
    url:"knowledge"
  })
}