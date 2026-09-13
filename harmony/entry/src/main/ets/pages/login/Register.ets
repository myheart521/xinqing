import { LoginConstant } from '../../common/constant/Login'
import { Header } from '../../components/Header'
import { promptAction, router } from '@kit.ArkUI'
import { VerificationCodeComponent } from 'verification_code/src/main/ets/components/VerificationCodeComponent'
import { photoAccessHelper } from '@kit.MediaLibraryKit'
import { fileIo } from '@kit.CoreFileKit'
import axios, { AxiosError, AxiosProgressEvent, AxiosResponse, FormData } from '@ohos/axios'
import fs from '@ohos.file.fs';
import image from '@ohos.multimedia.image';
import { BusinessError, request } from '@kit.BasicServicesKit'
import { codeApi, registerApi, uploadImageApi } from '../../api/login/RegisterApi'
import { DialogUtil } from '@pura/harmony-utils/src/main/ets/action/DialogUtil'
import { SpinKit, SpinType } from '@pura/spinkit'
import { registerModel } from '../../model/login/Register'
import { ToastUtil } from '@pura/harmony-utils/src/main/ets/action/ToastUtil'
import { ApiResponse } from '../../api/function/MotionApi'
import { JSON } from '@kit.ArkTS'

@Extend(TextInput)
function inputStyle() {
  .placeholderColor("#99182431")
  .height(50)
  .fontSize($r("app.float.xl"))
  .backgroundColor("#F1F3F5")
  .width('90%')
}

@Builder
function dialogArgs() {
  Text("两次输入的密码不相同").padding(50).backgroundColor(Color.White)
}

//校验是否是qq邮箱
export function isQQEmail(email: string) {
  const qqEmailRegex = /^[1-9][0-9]{4,10}@qq\.com$/;
  return qqEmailRegex.test(email);
}

//校验用户名
function validateUsername(username: string): string | null {
  const USERNAME_REGEX = /^(?:[^\s]){6,12}$/;
  // 检查是否包含空格
  if (/\s/.test(username)) {
    return "用户名不可包含空格";
  }
  // 检查长度是否在 6 到 12 位之间
  if (username.length < 6 || username.length > 12) {
    return "用户名长度必须在6到12位之间";
  }
  // 最终验证
  if (!USERNAME_REGEX.test(username)) {
    return "用户名格式不正确";
  }
  return null; // 无错误时返回 null
}

//校验密码
export function validatePassword(password: string): string | null {
  const PASSWORD_REGEX = /^\w{6,18}$/;

  // 检查长度是否在 6 到 18 位之间
  if (password.length < 6 || password.length > 18) {
    return "密码长度必须为6到18位";
  }
  // 检查是否只包含字母、数字或下划线
  if (!/^\w+$/.test(password)) {
    return "密码只能包含字母、数字或下划线";
  }
  // 最终验证完整格式
  if (!PASSWORD_REGEX.test(password)) {
    return "密码格式不正确";
  }
  return null; // 无错误时返回 null
}

// // 实例化 选项对象
// const photoSelectOptions = new picker.PhotoSelectOptions();
// // 过滤选择媒体文件类型为IMAGE
// photoSelectOptions.MIMEType = picker.PhotoViewMIMETypes.IMAGE_TYPE;
// // 选择媒体文件的最大数目
// photoSelectOptions.maxSelectNumber = 1;
//1. 打开相册选择一张图

const uploadImage = async () => {
  //选择图片的参数
  const options = new photoAccessHelper.PhotoSelectOptions()
  //指定文件类型是图片
  options.MIMEType = photoAccessHelper.PhotoViewMIMETypes.IMAGE_TYPE
  //选择一张图
  options.maxSelectNumber = 1
  //创建相册选择器
  const picker = new photoAccessHelper.PhotoViewPicker()
  //去选图片
  const selectResult = await picker.select(options)
  //取到选择图片的uri完整路径
  const uri = selectResult.photoUris[0]
  return uri
}


@Entry
@Component
struct Register {
  @State email: string = "demo@example.invalid"
  @State userName: string = "示例资料"
  @State password: string = ""
  @State rePassword: string = ""
  //获取验证码计时器的数字
  @State myReckon: number = 0
  //邮箱的错误信息
  @State emailError: string = ""
  //用户名的错误信息
  @State userNameError: string | null = ""
  //密码的错误信息
  @State passwordError: string | null = ""
  @State rePasswordError: string = ""
  //存储图片
  @State avatar: FormData = new FormData()
  @State profileImage: PixelMap = new Object() as PixelMap
  //回显的图片地址
  @State uri: string = ''
  //缓存地址
  @State cacheUri: string = ""
  //文件名字
  @State fileName: string = ""
  //上传加载
  @State isLoad: boolean = false
  //验证码
  @State code: string = ""
  //是否在等待获取验证码
  @State isWaite: boolean = false

  // 获取图片的pixelmap
  getPixelMap(uri: string) {
    // 通过uri打开图片文件，获取文件fd
    let file = fs.openSync(uri, fs.OpenMode.READ_WRITE);
    const imageSourceApi = image.createImageSource(file.fd);
    // 将图片解码为pixelmap
    imageSourceApi.createPixelMap().then(pixelmap => {
      this.profileImage = pixelmap
      console.log('Succeeded in creating pixelmap object through image decoding parameters.');
    }).catch((err: BusinessError) => {
      console.log('Failed to create pixelmap object through image decoding parameters.');
    })
  }

  build() {
    Stack() {
      Column() {
        Header({ title: "注册" })
        //上传头像
        Column() {
          Stack() {
            Image(this.profileImage)
              .alt($r('app.media.uploadImage'))
              .width(100)
              .height(100)
              .objectFit(ImageFit.Cover)
            if (this.uri) {
              Image($rawfile("functionImage/diary_cancel.png"))
                .position({ right: 7, top: 7 })
                .width(25)
                .height(25)
                .onClick(() => {
                  this.profileImage = new Object() as PixelMap
                  this.uri = ""
                })
            }
          }
          .width(100)
          .height(100)

          if (!this.uri) {
            Text("请上传图片")
              .fontSize(24)
              .margin({ top: 10 })
          }
        }
        .margin({ bottom: 30 })
        .onClick(async () => {
          if (!this.uri) {
            const uri = await uploadImage()
            if (uri) {
              try {
                // 以只读模式打开指定的文件，通过传入的uri参数确定要打开的文件位置
                const file = fileIo.openSync(uri, fileIo.OpenMode.READ_ONLY)
                // 构建一个新的文件路径，存储拷贝后的文件
                const filePath = getContext(this).cacheDir + '/' + file.name
                console.log("filePath", filePath)
                console.log("file.fd", file.fd)
                //设置回显的地址
                this.uri = filePath
                // 通过uri打开图片文件，获取文件fd
                const imageSourceApi = image.createImageSource(file.fd);
                // 将图片解码为pixelmap
                imageSourceApi.createPixelMap().then(pixelmap => {
                  this.profileImage = pixelmap
                  console.log('Succeeded in creating pixelmap object through image decoding parameters.');
                }).catch((err: BusinessError) => {
                  console.log('Failed to create pixelmap object through image decoding parameters.');
                })
                // 将打开的文件同步的复制到新构建的文件路径
                fileIo.copyFileSync(file.fd, filePath)
                // 关闭打开的文件，释放资源
                fileIo.closeSync(file.fd)
                // 创建一个新的FormData对象，用于准备文件上传 这是axios上传图片需要的对象类型 FormData
                // 添加一个名字叫file的数据,数据需要 `internal://cache/在沙箱目录的文件地址`
                // console.log("avatar", file.name)
                this.avatar.append('file', `internal://cache/${file.name}`)
                this.cacheUri = `internal://cache/${file.name}`
                console.log("cacheUri", this.cacheUri)
                console.log("this.avatar", JSON.stringify(this.avatar))
              } catch (e) {
                AlertDialog.show({ message: '头像选择失败' + JSON.stringify(e) })
              }
            } else {
              DialogUtil.showConfirmDialog({
                title: "获取图片失败",
                message: '',
                confirm: "确定",
                onAction: () => {
                }
              })
            }
          }
        })

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
        //验证码
        Row() {
          VerificationCodeComponent({
            slotWidthVP: 7,
            lineWidthVP: 22,
            onTextChanged: (value) => {
              this.code = value
            },
            onTextComplete: (value) => {
            }
          })
            .width("60%")
          Button(!this.isWaite ? "获取验证码" : `${this.myReckon}秒后重新获取`)
            .backgroundColor(!this.isWaite ? "#ed675f" : Color.Gray)
            .onClick(async () => {
              if (this.email != "" && this.emailError == "") {
                if (!this.isWaite) {
                  this.myReckon = 120
                  this.isWaite = true
                  let timer = setInterval(() => {
                    this.myReckon--
                    if (this.myReckon == 0) {
                      this.isWaite = false
                      clearInterval(timer)
                    }
                  }, 1000)
                  //请求获取验证码
                  const res = await codeApi(this.email)
                  if (res.code == 1) {
                    ToastUtil.showShort("验证码已发送请注意查收");
                  } else {
                    ToastUtil.showShort("验证码发送失败");
                  }

                }
              } else {
                ToastUtil.showShort("请检查邮箱是否填写正确");
              }
            })
            .width("40%")
        }
        .width("90%")
        .margin({ bottom: 30 })
        .justifyContent(FlexAlign.Center)
        .alignItems(VerticalAlign.Center)

        //输入用户名
        TextInput({ text: $$this.userName, placeholder: "请输入用户名" })
          .type(InputType.Normal)
          .inputStyle()
          .margin({ bottom: this.userNameError ? 0 : 30 })
          .showError(this.userNameError)
          .onBlur(() => {
            if (validateUsername(this.userName)) {
              this.userNameError = validateUsername(this.userName)
            } else {
              this.userNameError = ""
            }
          })
        //输入密码
        TextInput({ text: $$this.password, placeholder: "请输入密码" })
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
        //确认密码
        TextInput({ text: $$this.rePassword, placeholder: "请确认密码" })
          .type(InputType.Password)
          .inputStyle()
          .showError(this.rePasswordError)
          .margin({ bottom: this.rePasswordError ? 0 : 30 })
          .onBlur(() => {
            if (this.password !== this.rePassword) {
              this.rePasswordError = "两次输入的密码不相同"
            } else {
              this.rePasswordError = ""
            }
          })
        //确认注册
        Button("确认注册", { type: ButtonType.Capsule })
          .width("90%")
          .height(50)
          .fontSize($r("app.float.lg"))
          .fontWeight(FontWeight.Medium)
          .backgroundColor("#eb584f")
          .margin({ top: 20 })
          .onClick(async () => {
            if (this.emailError == "" && this.userNameError == "" && this.passwordError == "" &&
              this.rePasswordError == "" && this.code != "") {
              this.isLoad = true
              try {
                // 发送请求
                axios.post<string, AxiosResponse<ApiResponse<string>>, FormData>('http://localhost:8080/common/upload',
                  this.avatar, {
                    headers: { 'Content-Type': 'multipart/form-data' },
                    context: getContext(this),
                    onUploadProgress: (progressEvent: AxiosProgressEvent): void => {
                      console.info(progressEvent && progressEvent.loaded && progressEvent.total ?
                        Math.ceil(progressEvent.loaded / progressEvent.total * 100) + '%' : '0%');
                    },
                  }).then(async (res: AxiosResponse<ApiResponse<string>>) => {
                  console.info("result" + JSON.stringify(res.data));
                  if (res.data.data) {
                    //如果图片上传成功再上传的表单
                    const form: registerModel = {
                      email: this.email,
                      userName: this.userName,
                      password: this.password,
                      userAvatar: res.data.data,
                      checkPassword: this.rePassword,
                      code: this.code
                    }
                    const resRe = await registerApi(form);
                    // 处理成功情况
                    if (resRe.code == 1) {
                      this.isLoad = false
                      ToastUtil.showShort("注册成功");
                      setTimeout(() => {
                        router.back()
                      }, 1000)
                    } else {
                      this.isLoad = false
                      DialogUtil.showConfirmDialog({
                        title: "注册失败",
                        message: resRe.msg,
                        confirm: "确定",
                        onAction: () => {
                        }
                      })
                    }
                  } else {
                    this.isLoad = false
                    DialogUtil.showConfirmDialog({
                      title: "图片上传失败",
                      message: '',
                      confirm: "确定",
                      onAction: () => {
                      }
                    })
                  }
                }).catch((err: AxiosError) => {
                  this.isLoad = false
                  AlertDialog.show({ message: '头像上传失败了' + JSON.stringify(err) })
                  console.error("error:" + JSON.stringify(err));
                })
              } catch (e) {
                AlertDialog.show({ message: '头像上传失败' + JSON.stringify(e) })
                this.isLoad = false
              }
            } else {
              this.isLoad = false
              DialogUtil.showConfirmDialog({
                title: "信息格式不正确",
                message: '请确保填写信息符合要求',
                confirm: "确定",
                onAction: () => {
                }
              })
            }
          })
      }
      .width("100%")
      .justifyContent(FlexAlign.Center)
      .alignItems(HorizontalAlign.Center)

      //加载
      if (this.isLoad) {
        Column() {
          SpinKit({
            spinType: SpinType.spinC,
            spinColor: "#dd5145",
            spinSize: 80
          })
        }
        .zIndex(999)
        .justifyContent(FlexAlign.Center)
        .alignItems(HorizontalAlign.Center)
        .backgroundColor("#fff")
        .opacity(0.5)
        .width("100%")
        .height("100%")
      }
    }
  }
}