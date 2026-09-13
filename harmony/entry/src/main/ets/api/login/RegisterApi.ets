import axios, { AxiosError, AxiosHeaders, AxiosResponse, FormData } from '@ohos/axios'
import { FileUpload } from '@ohos/commons-fileupload'
import { registerModel } from '../../model/login/Register'
import { axiosAPI } from '../../utils/http'
import { ApiResponse } from '../function/MotionApi'
import { request } from '@kit.BasicServicesKit'

let fileUpload = new FileUpload({
  baseUrl: "http://localhost:8080/",
  readTimeout: 60000,
  connectTimeout: 60000
})


//头像上传接口
// export const uploadImageApi=(file:FormData)=>{
//   return fileUpload.post({
//     url:"common/upload",
//     extraData:file
//   })
// }


interface ResponseType {
  data: ApiResponse<string>;
};

export const uploadImageApi = (file: FormData) => {
  return axios.post("http://localhost:8080/common/upload", file, {
    headers: {
      'Content-Type': 'multipart/form-data',
    }
  })
    .then((res: ResponseType) => {
      // 处理成功响应，将文件路径返回
      return res.data;
    })
    .catch((error: AxiosError<ApiResponse<null>>) => {
      // 处理错误响应，抛出错误以便调用者捕获
      return Promise.reject(error);
    });
}


export const uploadImage = async (context: Context, files: request.File[]) => {
  let config: request.UploadConfig = {
    url: 'http://localhost:8080/common/upload',
    method: 'POST', // 请求类型
    header: {
      "Content-Type": "multipart/form-data" // 上传文件的参数类型
      // multipart/form-data  专门传文件的结构
    },
    files,
    data: [] // 批量上传需要携带的参数 用不上
  }
}


//注册
export const registerApi = (form: registerModel) => {
  return axiosAPI.post<ApiResponse<null>>({
    url: "user/register",
    data: form
  })
}

//验证码
export const codeApi = (email: string) => {
  return axiosAPI.post<ApiResponse<null>>({
    url: "user/checkEmail",
    data: { email: email }
  })
}
