import { AdaptiveRes } from "../../../../model/function/test/basic/AdaptiveRes"

@Component
export struct AdaptiveSuggest{
  @ObjectLink resReturn: AdaptiveRes

  build(){
    Column(){
      //比较好的地方
      Column({space:5}){
          Text('你值得赞许的地方:')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)

        ForEach(this.resReturn.good,(item:string)=>{
          Row(){
            Text(item)
          }
          .backgroundColor('#f1f5f8')
          .width('90%')
          .height(40)
          .justifyContent(FlexAlign.Center)
        })
      }
      .width('100%')
      .margin({bottom:20})

      //比较坏的地方
      Column({space:5}){
          Text('你需要改进的地方:')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
        ForEach(this.resReturn.bad,(item:string)=>{
          Row(){
            Text(item)
          }
          .backgroundColor('#f1f5f8')
          .width('90%')
          .height(40)
          .justifyContent(FlexAlign.Center)
        })
      }
      .width('100%')
      .margin({bottom:20})

      //建议
      Column(){

          Text('建议:')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)


        Row(){
          Text(this.resReturn.judge)
        }
        .backgroundColor('#f1f5f8')
        .width('90%')
        .height(40)
        .justifyContent(FlexAlign.Center)
      }.width('100%')
    }
    .width("95%")
    .padding(20)
    .backgroundColor(Color.White)
    .borderRadius(15)
    .shadow({ radius: 6, color: '#1F000000', offsetX: 2, offsetY: 4 })
    .margin({ top: 15 })
    .justifyContent(FlexAlign.Center)
  }
}
