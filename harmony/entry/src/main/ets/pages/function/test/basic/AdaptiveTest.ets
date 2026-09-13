import { router } from '@kit.ArkUI'
import { ProgressComponent } from '../../../../view/function/test/ProgressComponent'
import { Header } from '../../../../components/Header'
import { AdaptiveDataSource } from '../../../../viewmodel/function/test/basic/AdaptiveDataSource'
import { AdaptiveAQ } from '../../../../view/function/test/Basic/AdaptiveAQ'

@Entry
@Component
struct AdaptiveTest {
  @State data: AdaptiveDataSource = new AdaptiveDataSource([]);
  @State paramsFromIndex: object = router.getParams()
  @State finishTopic: number = 0
  @State allTopic: number = 0

  build() {
    Column({ space: 20 }) {
      Header({title:"自适应测试"})
      //进度条
      ProgressComponent({finishTopic: this.finishTopic, allTopic: this.allTopic})
      //答题界面
      AdaptiveAQ({ finishTopic: this.finishTopic, allTopic: this.allTopic })
    }
    .width('100%')
    .height('100%')
    .backgroundColor("#f0f0f0")
  }
}