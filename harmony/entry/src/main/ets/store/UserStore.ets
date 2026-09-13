import { userInfo } from "../model/login/Login";


export function setUserInfo(userInfo: userInfo) {
  AppStorage.setOrCreate("userName", userInfo.userName)
  AppStorage.setOrCreate("userAvatar", userInfo.userAvatar)
  AppStorage.setOrCreate("userProfile", userInfo.userProfile)
  AppStorage.setOrCreate("roleId", userInfo.roleId)
  AppStorage.setOrCreate("school", userInfo.school)
  AppStorage.setOrCreate("email", userInfo.email)
  AppStorage.setOrCreate("sex", userInfo.sex)
}

export function setToken(token: string) {
  AppStorage.setOrCreate("token", token)

}