import { accountUserLogin, loginInfo, userInfo, userLogin } from "../../model/login/Login"
import { axiosAPI } from "../../utils/http"
import { ApiResponse } from "../function/MotionApi"


export const login = (userData: userLogin) => {
  return axiosAPI.post<ApiResponse<loginInfo>>({
    url: "user/emaillogin",
    data: userData
  })
}

export const accountLogin=(accountUserData:accountUserLogin)=>{
  return axiosAPI.post<ApiResponse<loginInfo>>({
    url:"user/accountLogin",
    data:accountUserData
  })
}