//Topic.ets
import { CharacterDataSource } from '../../../../viewmodel/function/test/basic/CharacterDataSource';
import { router } from '@kit.ArkUI';
import { Header } from '../../../../components/Header';
import { ProgressComponent } from '../../../../view/function/test/ProgressComponent';
import { CharacterAQ } from '../../../../view/function/test/Basic/CharacterAQ';

@Entry
@Component
struct CharacterTest {
  @State data: CharacterDataSource = new CharacterDataSource([]);
  @State paramsFromIndex: object = router.getParams()
  @State finishTopic: number = 0
  @State allTopic: number = 0

  build() {
    Column({ space: 20 }) {
      //标题
      Header({title:"性格测试"})
      //进度条
      ProgressComponent({ finishTopic: this.finishTopic, allTopic: this.allTopic })
      //答题界面
      CharacterAQ({finishTopic:this.finishTopic,allTopic:this.allTopic})
    }
    .width('100%')
    .height('100%')
    .backgroundColor("#f0f0f0")
  }
}


