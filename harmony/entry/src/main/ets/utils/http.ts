/**
 * @ProjectName :
 * @FileName : BaseRequest
 * @Author : Project contributors
 * @Time : 2024/7/22 15:00
 * @Description :
 */
// import axios from  '@ohos/axios'
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse }  from '@ohos/axios'
import { promptAction } from '@kit.ArkUI'

class BaseRequest {
  instance: AxiosInstance;

  constructor(config: AxiosRequestConfig<AxiosResponse>) {
    this.instance = axios.create(config);

    // 请求拦截
    this.instance.interceptors.request.use((config) => {
      let tokenStorage: SubscribedAbstractProperty<string>|undefined = AppStorage.link('token');
      if (tokenStorage?.get()) {

        config.headers.token = tokenStorage.get();
      }

      return config;
    }, (err) => {
      console.error('Request error', err);
      return Promise.reject(err);
    }
    );
    // 响应拦截
    this.instance.interceptors.response.use((response: AxiosResponse<any, any>) => {
      let data = response.data;

      if (typeof data === 'string') {
        data = JSON.parse(data.trim());
      }
      const { code, msg } = data;
      if (code === 1) {  // 处理成功情况
        console.log("成功")
        return response.data;
      } else{
        console.log("发生错误")
        promptAction.showToast({
          message: msg
        });
        return Promise.reject(new Error(msg));
      }
    }, (err) => {
      console.error('Response error', err);
      return Promise.reject(err);
    }
    );
  }

  request<T = any>(config: AxiosRequestConfig): Promise<T> {

    return this.instance.request<any, T>(config);
  }

  get<T = any>(config: AxiosRequestConfig): Promise<T> {
    return this.request<T>({ ...config, method: 'GET' });
  }

  post<T = any>(config: AxiosRequestConfig): Promise<T> {
    return this.request<T>({ ...config, method: 'POST' });
  }
}

export const axiosAPI = new BaseRequest({
  baseURL: 'http://localhost:8080/',
  timeout: 3000,
  headers: {'Content-Type': 'application/json'}
})
