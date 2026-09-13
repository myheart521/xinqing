@Component
export struct BlankCard {
  build() {
    Column() {
      Image($r("app.media.blank"))
        .width(110)
        .height(110)
      Text("没有设备，请添加设备")
        .fontSize($r("app.float.2xl"))
    }
    .justifyContent(FlexAlign.Center)
    .alignItems(HorizontalAlign.Center)
  }
}