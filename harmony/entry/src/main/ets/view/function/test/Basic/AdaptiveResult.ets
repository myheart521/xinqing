import { AdaptiveRes } from "../../../../model/function/test/basic/AdaptiveRes"

//属性面板
@Component
export struct AdaptiveResult {
  @ObjectLink resReturn: AdaptiveRes
  build() {
    Column() {
      Column() {
        Row() {
          Text('relationship').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.relationship),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.relationship + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('study').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.study),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.study + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('campus').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.campus),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.campus + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('job').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.job),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.job + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('emotion').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.emotion),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.emotion + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('self').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.self),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.self + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('satisfaction').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.satisfaction),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.satisfaction + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('total').fontSize(12)
          Blank()
          Slider({
            value: Number(this.resReturn.total),
            min: 0,
            max: 100,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.total + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)
      }.cardStyle()
      .margin({ top: 15 })
    }.width("100%")
    .height('auto')
    .alignItems(HorizontalAlign.Center)
  }
}

@Extend(Slider) function panelStyle() { //自定义滑动条样式
  .width("50%")
  .blockColor('#ffefeff5')
  .trackColor('#ADD8E6')
  .selectedColor('#ff168a86')
  .showTips(true)
  .enabled(false)

}
//自定义卡片样式
@Styles function cardStyle() {
  .width("95%")
  .padding(20)
  .backgroundColor(Color.White)
  .borderRadius(15)
  .shadow({ radius: 6, color: '#1F000000', offsetX: 2, offsetY: 4 })
}
