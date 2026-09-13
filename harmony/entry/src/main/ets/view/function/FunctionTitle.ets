import { FunctionConstant } from "../../common/constant/Function";

@Component
export struct FunctionTitle{
  build() {
    Row(){
      Image($r("app.media.functionTitleStar"))
        .width(30)
        .height(30)
        .margin({right:$r("app.float.mp_sm")})
      Text(FunctionConstant.OTHER_FUNCTION_TITLE)
        .padding($r("app.float.mp_xs"))
        .fontSize($r("app.float.3xl"))
        .fontColor("#229EFD")
        .fontWeight(FontWeight.Bold)
      Image($r("app.media.functionTitleStar"))
        .width(30)
        .height(30)
        .margin({left:$r("app.float.mp_sm")})

    }
    .alignItems(VerticalAlign.Center)
  }
}