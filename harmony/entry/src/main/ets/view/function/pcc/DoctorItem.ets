import { DoctorItemModel } from "../../../model/function/pcc/DoctorItemModel"

@Component
export struct DoctorItem {
  @ObjectLink doctorItem:DoctorItemModel
  build() {
    Column() {
      Row(){
        Column() {
          Image(this.doctorItem.photo)
            .height(80)
            .width(80)
            .borderRadius(8)
            .objectFit(ImageFit.Cover)
        }

        Column({ space: 2 }) {
          Row({space:10}) {
            Column(){
              Text(this.doctorItem.doctorName)
                .maxLines(1)
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
            }
            Column(){
              Text('女')
                .maxLines(1)
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
            }
            Column(){
              Text(this.doctorItem.rating+'岁')
                .width(200)
                .maxLines(1)
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
            }
          }

          .alignItems(VerticalAlign.Center)
          .margin({ right: 20 })
          .width('100%')

          Column({space:2}) {
            Row() {
              Text('国家二级心理咨询师')
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
            }
            .alignItems(VerticalAlign.Center)
            Row() {
              Text(this.doctorItem.background)
                .fontSize(13)
                .fontWeight(FontWeight.Bold)
            }
            .alignItems(VerticalAlign.Center)
            Row() {
              Text("擅长："+this.doctorItem.specializedFields)
                .fontSize(13)
                .maxLines(2)
                .textOverflow({ overflow: TextOverflow.Ellipsis })
                .fontWeight(FontWeight.Bold)
            }
            .alignItems(VerticalAlign.Top)
            .width(220)


          }
          .justifyContent(FlexAlign.Start)
          .alignItems(HorizontalAlign.Start)
          .margin({ right: 20 })
          .width('100%')
        }
        .justifyContent(FlexAlign.Center)
        .padding({ left: 20, top: 10 })
        .width('100%')
      }
      Column(){

        Text(this.doctorItem.otherInformation)
          .fontSize(13)
          .maxLines(4)
          .textOverflow({ overflow: TextOverflow.Ellipsis })

      }
      .height(50)
      .alignItems(HorizontalAlign.Start)
      .justifyContent(FlexAlign.Center)
      .margin({top:3})
    }
    .padding(10)
    .alignItems(HorizontalAlign.Start)
    .backgroundColor('#ffffff')
    .margin({ top: 10 })
    .borderRadius(10)
    .width('100%')
    .height(170)
  }
}