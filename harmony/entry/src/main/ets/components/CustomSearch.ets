import { CommonConstants } from "../common/constant/CommonConstants"
import { DiaryConstant } from "../common/constant/Function"

@Component
export default struct CustomSearch {
  @Link keywords: string
  cleanKeywords:()=>void=()=>{}

  build() {
    Row() {
      Grid() {
        //搜索icon
        GridItem() {
          Column(){
            Image($rawfile('functionImage/diary_search.png'))
              .width(DiaryConstant.DIARY_ICON_SIZE)
          }
          .height(CommonConstants.FULL_PARENT)
            .justifyContent(FlexAlign.Center)

        }
        //输入框内容
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
        //取消
        GridItem() {
          if (this.keywords) {
            Column(){
              Image($rawfile('functionImage/diary_cancel.png'))
                .width(DiaryConstant.DIARY_ICON_SIZE)
                .onClick(()=>{
                  this.cleanKeywords()
                })
            }.height(CommonConstants.FULL_PARENT)
            .justifyContent(FlexAlign.Center)
          }
        }
      }
      .width('100%')
      .height(36)

      .columnsTemplate('1fr 16fr 1fr')
      .backgroundColor(Color.White)
      .borderRadius(16)
      .padding({left:$r("app.float.mp_sm"),right:$r("app.float.mp_sm")})
    }
    .padding({ left: $r("app.float.mp_lg"), right: $r("app.float.mp_lg") })
    .margin({top:$r("app.float.mp_sm")})
  }
}

@Preview
@Component
struct CustomSearchPreview {
  @State keywords: string = '123'

  build() {
    CustomSearch({ keywords: $keywords })
  }
}