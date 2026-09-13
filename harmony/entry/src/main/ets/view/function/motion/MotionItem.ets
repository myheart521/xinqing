import { MotionItemModel } from '../../../model/function/motion/MotionItemMoel'
import { MotionType } from './MotionType'
@Component
export struct MotionItem{
  @ObjectLink MotionItem:MotionItemModel
  build(){
    if(this.MotionItem.image){
      Row() {
        // 左侧图片
        Column() {
          Image(this.MotionItem.image)
            .alt($r("app.media.load"))
            .height(90)
            .width(100)
            .borderRadius(8)
            .objectFit(ImageFit.Cover)
        }
        .padding(10)
        // 右侧内容
        Column() {
          // 标题
          Row() {
            Text(this.MotionItem.title)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
              .width(200)
              .maxLines(1)
              .fontSize(17)
              .fontWeight(500)
          }
          .alignItems(VerticalAlign.Center)
          .margin({ right: 20 })
          .width('100%')
          .height(30)
          Column({ space: 4 }) {
            // 等级
            Row() {
              Text(this.MotionItem.league+" ")
                .fontColor('#686868')
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
              // 等级描述
              Text(this.MotionItem.leagueDescribe)
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .maxLines(1)
                .fontColor('#a9a9a9')
                .fontSize(13)
              Blank().width(10)
              // 时长
              Text(this.MotionItem.time+" ")
                .fontColor('#686868')
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
              Text("分钟")
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .maxLines(1)
                .fontColor('#a9a9a9')
                .width(200)
                .fontSize(13)
            }
            .alignItems(VerticalAlign.Center)
            // 运动特点
            Row() {
              Grid(){
                ForEach(this.MotionItem.motionFeatures,(item:string,index:number)=>{
                  GridItem(){
                    MotionType({content:this.MotionItem.motionFeatures[index]})
                  }
                })
              }
              .rowsTemplate('1fr 1fr')
              .columnsTemplate('1fr 1fr 1fr 1fr')
            }
            .padding({bottom:4})
            .borderWidth({bottom:0.5})
            .borderStyle(BorderStyle.Dashed)
            .borderColor("#a9a9a9")
            .alignItems(VerticalAlign.Center)
            .width(205)
            .height(45)
          }

          .justifyContent(FlexAlign.Start)
          .alignItems(HorizontalAlign.Start)
          .margin({ right: 20 })
          .width('100%')
          .height(62)
        }
        .justifyContent(FlexAlign.Start)
        .padding({ left: 10, top: 14 })
        .width('100%')
      }
      .backgroundColor("#ffffff")
      .borderRadius(10)
      .width('100%')
      .height(120)
    }
  }
}