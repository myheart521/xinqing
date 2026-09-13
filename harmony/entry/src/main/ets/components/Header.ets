import { ComponentContent, router } from "@kit.ArkUI"
import { CommonConstants } from "../common/constant/CommonConstants"

@Component
export struct Header {
  @Prop title:string|Resource
  build() {
    Row() {
      Image($r('app.media.back'))
        .width($r('app.float.2xl'))
        .height($r('app.float.2xl'))
        .margin({ left: $r('app.float.mp') })
        .onClick(() => {
            router.back()
        })
      Text(this.title)
        .fontSize($r("app.float.2xl"))
        .fontWeight(FontWeight.Bold)
        .fontColor($r("app.color.purple_gray"))
        .opacity(0.9)
        .letterSpacing(2)
        .padding({ left: $r('app.float.mp_sm') })
      Blank()
      Image($r('app.media.musicMore'))
        .width(20)
        .height(20)
        .margin({ right: $r("app.float.mp") })
      // .bindMenu(this.getMenu())
    }
    .width(CommonConstants.FULL_PARENT)
    .height(50)
    .zIndex(2)
  }
}