import { PCCModel } from "../../../model/function/pcc/PCCModel"
import { router } from "@kit.ArkUI"

@Component
export struct PCCItem {
  @ObjectLink @Watch("pCCWatch") pCCItem: PCCModel
  pCCWatch(changedPropertyName: string) {
    console.log("pCCItem",this.pCCItem.school)
  }

  build() {
    Row() {
      Column() {
        Image(this.pCCItem.src)
          .alt($r("app.media.load"))
          .height(100)
          .width(100)
          .borderRadius(8)
          .objectFit(ImageFit.Cover)
      }
      .padding(10)
      Column() {
        Row() {
          Text(this.pCCItem.school)
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

        Column({ space: 2 }) {
          Row() {
            Text('咨询电话: ')
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
            Text(this.pCCItem.phoneNumber)
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
            Text(this.pCCItem.location)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
              .maxLines(1)
              .width(200)
              .fontSize(13)
          }
          .alignItems(VerticalAlign.Center)
          Row() {
            Text('省份: ')
              .fontSize(13)
              .fontWeight(FontWeight.Bold)
            Text(this.pCCItem.province)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
              .maxLines(1)
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
    .backgroundColor("#ffffff")
    .borderRadius(15)
    .onClick(() => {
      router.pushUrl({
        url: 'pages/function/pcc/PCCIntroduction',
        params: {
          id: this.pCCItem.id
        }
      })
    })
    .width('100%')
    .height(120)
  }


}