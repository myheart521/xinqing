import DiscussAddModel from "../../model/group/DiscussAddModel"
import DiscussModel, { DiscussPage, DiscussType } from "../../model/group/DiscussModel"
import InDiscussAddModel from "../../model/group/InDiscussModel"
import { replay, replayData } from "../../model/group/Replay"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"

//获取论坛的类型
export const getDiscussType = ()=>{
  return axiosAPI.get<ApiResponse<DiscussType[]>>({
    url:"lunTan/type"
  })
}

//获取论坛下的讨论内容
export const getDiscuss = (id:number) => {
  return axiosAPI.post<ApiResponse<DiscussPage>>(
    {
      url:"lunTan/page",
      data:{typeId:id}
    }
  )
}

//获取某个评论下的回复
export const getReplay=(id:number)=>{
  return axiosAPI.get<ApiResponse<replayData>>(
    {
      url:`pinglun/${id}`
    }
  )
}

//新增帖子
export const addDiscuss=(addData:DiscussAddModel)=>{
  return axiosAPI.post<ApiResponse<null>>({
    url:"lunTan/add",
    data:addData
  })
}

//新增评论
export const addInDiscuss=(addData:InDiscussAddModel)=>{
  return axiosAPI.post<ApiResponse<null>>({
    url:"pinglun",
    data:addData
  })
}