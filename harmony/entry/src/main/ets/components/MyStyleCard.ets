import { HmSkeleton } from "@harmonypass/ivskeleton"

@Component
export struct MyStyleCard {
  @Prop resultType: string
  @Prop content: string
  @State isShow: boolean = false

  aboutToAppear() {
    this.isShow = true
  }

  build() {
    Column() {
      if (this.isShow) {
        Column() {
          Text(this.resultType + ': ')
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
            .margin({ bottom: 15 })
          Text(this.content)
            .fontSize(18)
            .maxLines(15)
            .lineHeight(20)
        }.cardStyle()
        .margin({ top: 15 })
        .justifyContent(FlexAlign.SpaceAround)
      } else {
        Column({ space: 20 }) {
          HmSkeleton({ count: 1 }) // 设置1排
        }
      }
    }.width("100%")
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