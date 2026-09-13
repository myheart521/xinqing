import { AdaptiveQuestionModel } from "../../model/function/test/basic/AdaptiveQuestionModel"
import { AdaptiveRes } from "../../model/function/test/basic/AdaptiveRes"
import { characterModel } from "../../model/function/test/basic/CharacterModel"
import { Res } from "../../model/function/test/basic/CharacterTypeRatio"
import { questionDataModal } from "../../model/function/test/QuestionDataModal"
import TestingModel from "../../model/function/test/TestingModel"
import { TestQuestionItemModel } from "../../model/function/test/TestQuestionItemModel"
import { TestResultShow } from "../../pages/function/test/TestingShowRes"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "./MotionApi"

//获取测试题库列表
export const testList = () => {
  return axiosAPI.get<ApiResponse<TestingModel[]>>({
    url: "common/test"
  })
}

//根据id获取测试题内容
export const testContent = (id: number) => {
  return axiosAPI.get<ApiResponse<questionDataModal>>({
    url: "test/" + id
  })
}

//获取结果
export const testResult = (ans: string) => {
  return axiosAPI.post<ApiResponse<TestResultShow>>({
    url: "test/depression/submit",
    data: { ans: ans }
  })
}

//获取性格测试的题库
export const characterTestContent = () => {
  return axiosAPI.get<ApiResponse<characterModel[]>>({
    url: "manage/getAll"
  })
}

//获取性格测试结果
export const characterRes = (ans: string) => {
  return axiosAPI.post<ApiResponse<Res>>({
    url: "manage/submit",
    data: { ans: ans }
  })
}

//获取自适应测试的测试题目
export const adaptiveTestContent = () => {
  return axiosAPI.get<ApiResponse<AdaptiveQuestionModel[]>>({
    url: "test/getAll"
  })
}

//获取自适应测试的测试结果
export const adaptiveRes = (ans: string) => {
  return axiosAPI.post<ApiResponse<AdaptiveRes>>({
    url: "test/submit",
    data: { ans: ans }
  })
}