@Component
export default struct CustomSearch {
  @Link keywords: string
  cleanKeywords:()=>void=()=>{}

  build() {
    Row() {
      Grid() {
        GridItem() {
          Image($rawfile('functionImage/diary_search.png'))
            .width(16)
        }

        GridItem() {
          TextInput({
            placeholder: '请输入要搜索的内容',
            text: this.keywords
          })
            .backgroundColor(Color.White)
            .onChange((value:string)=>{
              this.keywords = value
            })
        }

        GridItem() {
          if (this.keywords) {
            Image($rawfile('functionImage/diary_cancel.png'))
              .width(16)
              .onClick(()=>{
                this.cleanKeywords()
              })
          }

        }
      }
      .width('100%')
      .height(36)
      .columnsTemplate('1fr 16fr 1fr')
      .backgroundColor(Color.White)
      .borderRadius(16)
      .padding({left:12,right:12})
    }
    .padding({ left: 20, right: 20 })
    .margin({top:12})
  }
}

@Preview
@Component
struct CustomSearchPreview {
  @State keywords: string = '1'

  build() {
    CustomSearch({ keywords: $keywords })
  }
}