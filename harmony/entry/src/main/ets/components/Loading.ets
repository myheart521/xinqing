import { SpinKit, SpinKitHelper, SpinType } from "@pura/spinkit";
import { CommonConstants } from "../common/constant/CommonConstants";

@Preview
@Component
export struct Loading{
  @State isLoad:boolean=true

  build() {
    Column(){
      Stack(){
        SpinKit({
          spinType: SpinType.spinC,
          spinColor: "#dd5145",
          spinSize: 90
        })
      }
      .height(CommonConstants.FULL_PARENT)
      .width(CommonConstants.FULL_PARENT)
      .backgroundColor(Color.Black)
      .opacity(0.3)
    }.height(CommonConstants.FULL_PARENT)
    .width(CommonConstants.FULL_PARENT)

  }
}