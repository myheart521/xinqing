@Component
export default struct DiscussTop {
  @StorageLink("range") range: string = ''
  @StorageLink("content") content: string = ''
  @StorageLink("discussName") discussName: string = ''

  build() {
    Row({ space: 5 }) {
      Column() {
        Image($r('app.media.account'))
          .width(50)
        Text(this.discussName)
          .maxLines(1)
      }

      Column() {
        Row() {
          TextArea({
            text: this.content
          })
            .enabled(false)
            .fontSize(16)
            .margin({ right: 50 })
            .backgroundColor('#ffffff')
            .height('95%')
        }
        .width('100%')

        Row() {
          Text('讨论于 ' + this.range)
            .fontSize(12)
            .fontWeight(FontWeight.Regular)
            .fontColor(Color.Black)
            .margin({ left: 10 })
        }
        .width('100%')
        .margin({ top: 10 })
      }
    }
    .padding(20)
    .justifyContent(FlexAlign.Start)
    .alignItems(VerticalAlign.Top)
    .backgroundColor('#f1f5f8')
    .height(150)
    .width('100%')
  }
}


@Preview
@Component
struct PreviewDiscussTop {
  build() {
    DiscussTop()
  }
}
