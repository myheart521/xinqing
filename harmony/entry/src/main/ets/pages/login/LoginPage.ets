import { SpinKit, SpinType } from '@pura/spinkit'
import { LoginConstant } from '../../common/constant/Login'
import { LogoAndDescribe } from '../../view/login/LogoAndDescribe'
import { isQQEmail, validatePassword } from './Register'
import { router } from '@kit.ArkUI'
import { accountLogin, login } from '../../api/login/LoginApi'
import { accountUserLogin, userLogin } from '../../model/login/Login'
import { DialogUtil, ToastUtil } from '@pura/harmony-utils'
import { setToken, setUserInfo } from '../../store/UserStore'

@Extend(TextInput)
function inputStyle() {
  .placeholderColor("#99182431")
  .height(50)
  .fontSize($r("app.float.xl"))
  .backgroundColor("#F1F3F5")
  .width('90%')
  .margin({ top: 12 })
}

@Entry
@Component
struct LoginPage {
  // 加载
  @State isShowProgress: boolean = false
  // 邮箱
  @State email: string = "demo@example.invalid"
  //邮箱错误信息
  @State emailError: string = ""
  // 密码
  @State password: string = ""
  //密码的错误信息
  @State passwordError: string | null = ""
  //登录操作
  userLogin = async (userData: userLogin) => {
    this.isShowProgress = true
    try {
      const res = await login(userData)
      if (res.code == 1) {
        ToastUtil.showShort("登录成功");
        this.isShowProgress = false
        //将用户信息保存到本地
        setUserInfo(res.data.user)
        setToken(res.data.token)
        router.pushUrl({
          url: "pages/Index"
        })
      } else {
        this.isShowProgress = false
        DialogUtil.showConfirmDialog({
          title: "登录失败",
          message: res.msg,
          confirm: "确定",
          onAction: () => {
          }
        })
      }
    } catch (e) {
      this.isShowProgress = false
    }
  }
  //账号密码登录
  accountUserLogin = async (userAccountData: accountUserLogin) => {
    this.isShowProgress = true
    const res = await accountLogin(userAccountData)
    if (res.code == 1) {
      ToastUtil.showShort("登录成功");
      this.isShowProgress = false
      //将用户信息保存到本地
      setUserInfo(res.data.user)
      setToken(res.data.token)
      router.pushUrl({
        url: "pages/Index"
      })
    } else {
      this.isShowProgress = false
      DialogUtil.showConfirmDialog({
        title: "登录失败",
        message: res.msg,
        confirm: "确定",
        onAction: () => {
        }
      })
    }
  }

  aboutToAppear(): void {
    router.clear()
  }

  build() {

    Column() {
      // 顶部LOGO
      LogoAndDescribe({ title: $r("app.string.login") })
      //输入邮箱
      TextInput({ text: $$this.email, placeholder: "请输入qq邮箱" })
        .type(InputType.Email)
        .inputStyle()
        .showError(this.emailError)
        .onBlur(() => {
          if (!isQQEmail(this.email)) {
            this.emailError = "请输入正确的邮箱号"
          } else {
            this.emailError = ""
          }
        })
        .margin({ bottom: this.emailError ? 0 : 30 })
      //输入密码
      TextInput({ text: $$this.password, placeholder: "请输入密码" })
        .maxLength(LoginConstant.INPUT_ACCOUNT_LENGTH)
        .type(InputType.Password)
        .inputStyle()
        .showError(this.passwordError)
        .margin({ bottom: this.passwordError ? 0 : 30 })
        .onBlur(() => {
          if (validatePassword(this.password)) {
            this.passwordError = validatePassword(this.password)
          } else {
            this.passwordError = ""
          }
        })
      //登录及注册和忘记密码
      this.login()
      //登录时的加载
      this.loading()
    }
    .height("100%")
    .width("100%")
    .padding({
      left: 12,
      right: 12,
      bottom: 24
    })
  }

  @Builder
  login() {
    Button($r("app.string.login"), { type: ButtonType.Capsule })
      .width("90%")
      .height(50)
      .fontSize($r("app.float.2xl"))
      .fontWeight(FontWeight.Medium)
      .backgroundColor("#eb584f")
      .margin({ top: 87, bottom: 12 })
      .onClick(() => {
        const userData: userLogin = {
          email: this.email,
          password: this.password
        }
        const userAccountData: accountUserLogin = {
          account: this.email,
          password: this.password
        }
        if (this.email != "admin" && this.password != "admin") {
          this.userLogin(userData)
        } else {
          this.accountUserLogin(userAccountData)
        }
      })
    Row() {
      Text($r("app.string.sign_up"))
        .fontSize(20)
        .margin({ right: 5 })
        .onClick(() => {
          router.pushUrl({
            url: "pages/login/Register"
          })
        })
      Text('|')
        .fontSize(20)
      Text($r("app.string.forgot_password"))
        .fontSize(20)
        .margin({ left: 5 })
        .onClick(() => {
        })
    }
    .margin(20) // 设置外边距
    .justifyContent(FlexAlign.Center)
  }

  @Builder
  loading() {
    if (this.isShowProgress) {
      SpinKit({
        spinType: SpinType.spinC,
        spinColor: "#dd5145",
        spinSize: 40
      })
    }
    Blank() //空格
  }
}