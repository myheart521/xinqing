
@Entry
@Component
export struct AdaptiveTheory {
  build() {
    Column() {
      Text('本问卷基于：中国大学生适应量表（China College Student Adjustment Scale, CCSAS）')
        .fontSize(18)
        .fontWeight(FontWeight.Bold)

      Text('中国大学生适应量表经过严格的编制过程，最终确定了人际关系适应、学习适应、校园生活适应、择业适应、情绪适应、自我适应、满意度7个维度，60个项目。问卷适合中国所有在校专科生、本科生和研究生使用，简单易测')
        .padding({ top: 16 })
        .lineHeight(24)

      Text('各分量表及总分平均分均以常模51分（按照满分标准分为100分计算）为界，自评分越高说明大学生适应能力越强，低于51分则有待提高。')
        .padding({ top: 16 })
        .lineHeight(24)
    }
    .height("30%")
    .cardStyle()
    .margin({ top: 15 })
    .justifyContent(FlexAlign.Center)
  }
}
//自定义卡片样式
@Styles function cardStyle() {
  .width("95%")
  .height("auto")
  .padding(20)
  .backgroundColor(Color.White)
  .borderRadius(15)
  .shadow({ radius: 6, color: '#1F000000', offsetX: 2, offsetY: 4 })
}