/***
 * 题目列表中的进度模块
 */
@Component
export struct ProgressComponent {
  @Link finishTopic: number
  @Link allTopic: number

  build() {
    Column() {
      Row() {
        Text('答题进度：')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
        Stack() {//堆叠组件，将一个进度条和两个文本框进行堆叠，展示出比较好看的效果
          Progress(
            {
              value: this.finishTopic,
              total: this.allTopic,
              type: ProgressType.Ring

            }).width(80)
          Row() {
            Text(this.finishTopic.toString())
              .fontWeight(18)
              .fontColor("#ff245fd3")
            Text(' / ' + this.allTopic)
              .fontWeight(18)
              .fontColor(Color.Black)
          }
        }

      }.cardStyle()
      .margin({ top: 15, left: 10, right: 10 })
      .justifyContent(FlexAlign.SpaceEvenly)
      .backgroundColor("#FAEBD7")

    }

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