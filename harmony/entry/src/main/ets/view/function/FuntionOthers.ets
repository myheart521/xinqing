import { FunctionConstant } from "../../common/constant/Function"
import { OtherTollsModel } from "../../model/function/OtherTollsModel"
import { OtherTollsViewModel } from "../../viewmodel/function/OtherTollsViewModel"
import { promptAction, router } from "@kit.ArkUI"

@Component
export struct FunctionOthers {
@Prop tollsData:OtherTollsViewModel

  build() {
    Grid() {
      // LazyForEach()
      LazyForEach(this.tollsData,(item:OtherTollsModel,index:number)=>{
        GridItem() {
          Column() {
            //图片
            Image(item.url)
              .width(80)
              .height(80)
            //标题
            Text(item.title)
              .margin({top:$r("app.float.lg")})
              .fontSize($r("app.float.3xl"))
              .fontWeight(FontWeight.Bold)
              .fontColor("#229EFD")
            //描述
            Text(item.des)
              .fontSize($r("app.float.xs"))
              .fontColor("#229EFD")
          }
          .onClick(()=>{
            if(index==0){
              router.pushUrl({
                url:"pages/function/pcc/ShowPCC"
              })
            }else if(index==1){
              router.pushUrl({
                url:"pages/function/knowledge/KnowledgePopular"
              })
            }
            else{
              promptAction.showToast({
                message: '正在开发中，尽请期待~',
                duration: 2000
              });
            }
          })
        }
        .height(FunctionConstant.TOOLS_HEIGHT)
        .padding($r("app.float.mp"))
        .backgroundColor(Color.White)
        .borderRadius(20)

      })
    }
    .columnsTemplate("1fr 1fr")
    .columnsGap($r("app.float.mp"))
    .rowsGap($r("app.float.mp"))
  }
}