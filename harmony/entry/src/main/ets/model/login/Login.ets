export class userLogin {
  email: string = "demo@example.invalid"
  password: string = ""
}

export class accountUserLogin{
  account:string=""
  password:string=""
}

export class userInfo {
  userName: string = "示例资料"
  userAvatar: string = ""
  userProfile: null | string = ""
  roleId: number = 0
  school: null | string = ""
  studentNumber: null | string = null
  email: string = "demo@example.invalid"
  sex: null | number = null
}

export class loginInfo {
  token: string = ""
  user: userInfo = new userInfo()
}