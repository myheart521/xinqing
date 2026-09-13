import { CommonConstants } from '../../../common/constant/CommonConstants'
import { MotionConstant } from '../../../common/constant/Function'
import { MotionSearchModel } from '../../../model/function/motion/MotionSearchModel'

@Component
export default struct SearchComponent {
  @ObjectLink searchContent: MotionSearchModel
  onSearchClick: () => void = () => {
  }

  build() {
    Row() {
      Grid() {
        GridItem() {
          Column() {
            Image($rawfile('functionImage/diary_search.png'))
              .width(MotionConstant.MOTION_ICON_SIZE)
          }
          .height(CommonConstants.FULL_PARENT)
          .justifyContent(FlexAlign.Center)
        }

        GridItem() {
          TextInput({
            placeholder: '请输入要搜索的内容',
            text: this.searchContent.title
          })
            .backgroundColor("#f5f5f5")
            .onChange((value: string) => {
              this.searchContent.title = value
            })
        }

        GridItem() {
          if (this.searchContent.title) {
            Column() {
              Image($rawfile('functionImage/diary_cancel.png'))
                .width(16)
                .onClick(() => {
                  this.searchContent.title = ''
                })
            }
            .justifyContent(FlexAlign.Center)
            .width('100%')
            .padding(2)
            .borderRadius(14)
            .backgroundColor("#c1c1c1")
            .height(CommonConstants.FULL_PARENT)
          }

        }

      }
      .width('83%')
      .height(36)
      .columnsTemplate('2fr 16fr 2fr')
      .backgroundColor("#f5f5f5")
      .borderRadius(16)
      .padding({ left: 12, right: 12 })

      Button("搜索")
        .onClick(() => {
          this.onSearchClick()
        })

    }
    .justifyContent(FlexAlign.Center)
    .padding({ left: 20, right: 20 })
    .margin({ top: 12 })
  }
}
