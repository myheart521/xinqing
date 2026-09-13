import TestingModel from "../../../model/function/test/TestingModel"

@Component
export default struct TestingItem {
  @ObjectLink testing: TestingModel
  onTestingClick: (testing: TestingModel) => void=()=>{}

  build() {
    Row() {
      Column() {
        Image(this.testing.src)
          .alt($r("app.media.load"))
          .height(100)
          .width(100)
          .borderRadius(8)
          .objectFit(ImageFit.Cover)
        // 保持宽高比进行缩小或者放大，使得图片两边都大于或等于显示边界。
      }
      .padding(10)
      .backgroundColor(Color.White)

      Column() {
        Row() {
          Text(this.testing.title)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
            .width(200)
            .maxLines(1)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
        }
        .alignItems(VerticalAlign.Center)
        .margin({ right: 20 })
        .width('100%')
        .height(40)

        Row() {
          Text(this.testing.introductions)
            .textOverflow({ overflow: TextOverflow.Ellipsis })
            .maxLines(3)
            .width(200)
            .fontSize(13)
        }
        .alignItems(VerticalAlign.Top)
        .margin({ right: 20 })
        .width('100%')
        .height(80)
      }
      .justifyContent(FlexAlign.Start)
      .padding(10)
      .width('100%')
      .backgroundColor(Color.White)

    }
    .width('100%')
    .height(120)
    .onClick(() => {
      this.onTestingClick(this.testing);
    })
    .margin({ top: 20 })
    .borderRadius(8)
  }
}
