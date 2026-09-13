import { InMotionInfoModel } from "../../model/function/motion/InMotionInfoModel";
import { MotionItemModel } from "../../model/function/motion/MotionItemMoel";
import { MotionSearchModel } from "../../model/function/motion/MotionSearchModel";
import { axiosAPI } from "../../utils/http";

export interface ApiResponse<T> {
  code: number;
  msg: string;
  data: T
}

export const motionListRequest = (data: MotionSearchModel)=>{
  return axiosAPI.post<ApiResponse<MotionItemModel[]>>({
    url:"sport",
    data:data
  })
}

export const motionInfoRequest = (id:number)=>{
  return axiosAPI.get<ApiResponse<InMotionInfoModel>>({
    url:`sport/${id}`,
  })
}

