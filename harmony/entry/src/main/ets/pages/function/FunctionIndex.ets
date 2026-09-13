import { CommonConstants } from "../../common/constant/CommonConstants"
import { OtherTollsModel } from "../../model/function/OtherTollsModel"
import { FunctionBanner } from "../../view/function/FunctionBanner"
import { FunctionTitle } from "../../view/function/FunctionTitle"
import { FunctionOthers } from "../../view/function/FuntionOthers"
import { OtherTollsViewModel } from "../../viewmodel/function/OtherTollsViewModel"

@Component
export struct FunctionIndex {
  //其他工具的数据
  @State tollsData: OtherTollsViewModel = new OtherTollsViewModel()
  // aboutToAppear(): void {
  //   this.tollsData = new OtherTollsViewModel().getTollsData()
  // }
  build() {
    Scroll() {
      Column(){
        //主要功能的轮播图
        Row() {
          FunctionBanner()
        }
        //其他功能文字
        Row() {
          FunctionTitle()
        }
        .margin({top:$r("app.float.mp_xl")})

        //底部其他功能
        Row() {
          FunctionOthers({tollsData:this.tollsData})
        }
        .margin({top:$r("app.float.mp")})

      }
      // .height(CommonConstants.FULL_PARENT)

      .alignItems(HorizontalAlign.Center)
      .justifyContent(FlexAlign.Start)
    }
    .padding($r("app.float.mp_xs"))

  }
}