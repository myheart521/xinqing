import { router } from "@kit.ArkUI"
import { CommonConstants } from "../../../common/constant/CommonConstants"
import { DiaryConstant } from "../../../common/constant/Function"

@Component
export default struct CustomTitle{
  @Link order:boolean
  @Link isAddDialogShow:boolean
  //复用按钮样式
  @Styles titleButtonStyle(){
    .width(40)
    .height(40)
    .borderRadius(30)
    .borderColor('rgba(107, 78, 70, 1)')
  }
  build(){
    Row(){
      //返回
      Row() {
        Image($r('app.media.back'))
          .width(DiaryConstant.DIARY_ICON_SIZE)
          .height(DiaryConstant.DIARY_ICON_SIZE)
          .onClick(() => {
            router.back()
          })
      }
      .padding(10)
      //升序
      Column(){
        if(this.order){
          Image($rawfile('functionImage/diary_up.png'))
            .width(DiaryConstant.DIARY_ICON_SIZE)
        }else{
          Image($rawfile('functionImage/diary_down.png'))
            .width(DiaryConstant.DIARY_ICON_SIZE)
        }
      }
      .onClick(()=>{
        this.order = !this.order
      })
      .titleButtonStyle()
      .justifyContent(FlexAlign.Center)
      .margin({left:$r("app.float.mp_lg")})
      .backgroundColor("#ececf6")

      Blank()
      //标题
      Column(){
        Text('记录心情')
          .fontSize($r("app.float.3xl"))
          .fontWeight(600)
          .fontColor(Color.Black)

      }
      Blank()
      //编辑
      Column(){
        Image($rawfile('functionImage/diary_edit.png'))
          .width(DiaryConstant.DIARY_ICON_SIZE)
      }
      .titleButtonStyle()
      .justifyContent(FlexAlign.Center)
      .margin({right:$r("app.float.mp_lg")})
      .onClick(()=>{
        this.isAddDialogShow = true
      })
      .backgroundColor("#ececf6")
    }
    .width(CommonConstants.FULL_PARENT)
    .height(60)
    // .linearGradient({direction:GradientDirection.Bottom,
    //   colors:[['#7B6558',0.5],['#725A4E',1]]
    // })
     .backgroundColor('#fefefe')
    .shadow({
      radius:10,
      color:'rgba(0,0,0,0.3)',
      offsetX:0,
      offsetY:25
    })
  }
}

@Preview
@Component
struct PreviewCustomTitle{
  @State order:boolean = true
  @State isAddDialogShow:boolean = false
  build(){
    CustomTitle({order:$order,isAddDialogShow:$isAddDialogShow})
  }
}