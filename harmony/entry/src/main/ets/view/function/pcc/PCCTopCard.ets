import { PCCIntroModel } from "../../../model/function/pcc/PCCIntroModel"
import { PCCTimeModel } from "../../../model/function/pcc/PCCTimeModel"

@Component
export struct PCCTopCard {
  @ObjectLink introductionPCC: PCCIntroModel

  build() {
    Column() {
      Column() {

        Row() {
          Column() {
            Image(this.introductionPCC.src)
              .height(100)
              .width(100)
              .borderRadius(8)
              .objectFit(ImageFit.Cover)
            // 保持宽高比进行缩小或者放大，使得图片两边都大于或等于显示边界。
          }
          .padding(10)

          Column() {
            Row() {
              Text(this.introductionPCC.school)
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .width(200)
                .maxLines(1)
                .fontSize(17)
                .fontWeight(FontWeight.Bold)
            }
            .alignItems(VerticalAlign.Center)
            .margin({ right: 20 })
            .width('100%')
            .height(30)

            Column({ space: 3 }) {
              Row() {
                Text('咨询电话: ')
                  .fontSize(13)
                  .fontWeight(FontWeight.Bold)
                Text(this.introductionPCC.phoneNumber)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .maxLines(1)
                  .width(200)
                  .fontSize(13)
              }
              .alignItems(VerticalAlign.Center)

              Row() {
                Text('email: 'demo@example.invalid'位置: ')
                  .fontSize(13)
                  .fontWeight(FontWeight.Bold)
                Text(this.introductionPCC.location)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .maxLines(2)
                  .width(200)
                  .fontSize(13)
              }
              .alignItems(VerticalAlign.Center)

              Row() {
                Text('省份: ')
                  .fontSize(13)
                  .fontWeight(FontWeight.Bold)
                Text(this.introductionPCC.province)
                  .textOverflow({ overflow: TextOverflow.Ellipsis })
                  .maxLines(2)
                  .width(200)
                  .fontSize(13)
              }
              .alignItems(VerticalAlign.Center)
            }
            .justifyContent(FlexAlign.Start)
            .alignItems(HorizontalAlign.Start)
            .margin({ right: 20 })
            .width('100%')
            .height(80)
          }
          .justifyContent(FlexAlign.Start)
          .padding({ left: 20, top: 10 })
          .width('100%')
        }
      //咨询室上班时间
        Row() {
          List(){
            ForEach(this.introductionPCC.timeSlots, (item:PCCTimeModel, index) => {
                ListItem(){
                  Text(item.startTime+"-"+item.endTime+"    "+item.timeDesc)
                    .textOverflow({ overflow: TextOverflow.Ellipsis })
                    .maxLines(1)
                    .width(250)
                    .fontSize(13)
                }
            })
          }
          .scrollBar(BarState.Auto)
        }
        Column() {
          Row() {
            Text('咨询方法: ')
              .fontSize(15)
              .fontWeight(FontWeight.Bold)
          }

          Row() {
            Text(this.introductionPCC.method)
          }
          .margin({ bottom: 5 })

          Row() {
            Text('微信公众号: ')
              .fontSize(15)
              .fontWeight(FontWeight.Bold)
          }

          Row() {
            Text(this.introductionPCC.publicAccount)
          }
          .margin({ bottom: 5 })

          Row() {
            Text('详细介绍：')
              .fontSize(15)
              .fontWeight(FontWeight.Bold)
          }

          Row() {
            Text(this.introductionPCC.description)
          }
        }
        .alignItems(HorizontalAlign.Start)

      }.cardStyle()
      .margin({ top: 15 })
      .justifyContent(FlexAlign.Start)
      .alignItems(HorizontalAlign.Center)
    }
    .width("100%")
    .alignItems(HorizontalAlign.Center)
  }
}

//自定义卡片样式
@Styles function cardStyle() {
  .width("95%")
  .padding(20)
  .backgroundColor(Color.White)
  .borderRadius(15)
  .shadow({ radius: 6, color: '#1F000000', offsetX: 2, offsetY: 4 })
}
