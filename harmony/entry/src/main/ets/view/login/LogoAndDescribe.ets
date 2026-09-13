@Component
export struct LogoAndDescribe {
  @Prop title:string|Resource
  build() {
    Column() {
      // logo
      Image($r('app.media.logo'))
        .width(78)
        .height(78)
        .margin({ top: 70, bottom: 8 })

      Text(this.title)
        .fontSize($r("app.float.3xl"))
        .fontWeight(FontWeight.Medium)
        .fontColor("#182431")
      Text($r("app.string.login_desc"))
        .fontSize($r('app.float.xl'))
        .fontColor("#99182431")
        .margin({ bottom: 30, top: 8 })
    }

  }
}