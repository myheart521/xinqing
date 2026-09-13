import { testingInfoData } from './Testing'
import { Header } from '../../../components/Header'
import { router } from '@kit.ArkUI'
import testingQuestionViewModel from '../../../viewmodel/function/test/TestingQuestionViewModel'
import { ProgressComponent } from '../../../view/function/test/ProgressComponent'
import { MainAQ } from '../../../view/function/test/MainAQ'
import { testContent } from '../../../api/function/TestApi'
import { MyDataSource } from '../../../viewmodel/function/test/MyDataSource'


@Entry
@Component
struct TestQuestion {
  @State testingInfo: testingInfoData = { id: 0, title: '' }
  @State finishTopic: number = 0
  @State allTopic: number = 0
  @State data: MyDataSource = new MyDataSource([])

  aboutToAppear() {
    this.testingInfo = router.getParams() as testingInfoData
    testingQuestionViewModel.setTitleInfo(this.testingInfo.title)
    this.getTestContent()
  }

  //获取数据
  getTestContent = async () => {
    const res = await testContent(this.testingInfo.id)
    this.data.setData(res.data.questionVOList)
    testingQuestionViewModel.setTestQuestionInfo(res.data.questionVOList)
    this.allTopic = res.data.questionVOList.length
  }

  build() {
    Column({ space: 20 }) {
      //标题
      Header({ title: this.testingInfo?.title })
      //进度条
      ProgressComponent({ finishTopic: this.finishTopic, allTopic: this.allTopic })
      //答题界面
      MainAQ({
        data: this.data,
        finishTopic: this.finishTopic,
        allTopic: this.allTopic,
        testId: this.testingInfo.id
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor("#f0f0f0")
  }
}