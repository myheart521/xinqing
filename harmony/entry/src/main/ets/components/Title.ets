@Component
export struct Title {
  @Prop title:string
  build() {
    Row() {
      Image($r("app.media.functionTitleStar"))
        .width(30)
        .height(30)
        .margin({ right: $r("app.float.mp_sm") })
      Text(this.title)
        .padding($r("app.float.mp_xs"))
        .fontSize($r("app.float.3xl"))
        .fontColor("#229EFD")
        .fontWeight(FontWeight.Bold)
      Image($r("app.media.functionTitleStar"))
        .width(30)
        .height(30)
        .margin({ right: $r("app.float.mp_sm") })
    }
    .margin({bottom:5})
  }
}