import { Res } from "../../../../model/function/test/basic/CharacterTypeRatio"

//属性面板
@Component
export struct CharacterTypeRatio {
  @ObjectLink resReturn: Res
  build() {
    Column() {
      Column() {
        Row() {
          Text('E比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.eRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.eRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('I比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.iRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.iRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('S比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.sRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.sRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('N比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.nRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.nRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('T比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.tRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.tRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('F比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.fRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.fRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('J比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.jRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.jRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)

        Row() {
          Text('P比率').fontSize(12)
          Slider({
            value: Number(this.resReturn.pRate),
            min: 0,
            max: 40,
            style: SliderStyle.InSet
          }).panelStyle()

          Text(this.resReturn.pRate + " %").fontSize(12)
        }.width('80%')
        .justifyContent(FlexAlign.SpaceAround)
      }.cardStyle()
      .margin({ top: 15 })
      Column() {
        Text('描述：').fontSize(12).margin({ bottom: 15 })
        Text(this.resReturn.disc)
          .fontSize(14)
          .maxLines(15)
          .lineHeight(20)
      }.cardStyle()
      .margin({ top: 15 })
      .justifyContent(FlexAlign.SpaceAround)
    }.width("100%")
    .height("80%")
    .alignItems(HorizontalAlign.Center)
  }
}

@Extend(Slider) function panelStyle() {	//自定义滑动条样式
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