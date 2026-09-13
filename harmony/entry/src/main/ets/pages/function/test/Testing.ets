import { DividerTmp } from "../../../model/function/pcc/DividerTmp"
import TestingModel from "../../../model/function/test/TestingModel"
import { router } from "@kit.ArkUI"
import TestingItem from "../../../view/function/test/TestingItem"
import { Header } from "../../../components/Header"
import { HmSkeleton } from "@harmonypass/ivskeleton"
import { testList } from "../../../api/function/TestApi"


export class testingInfoData {
  id: number=0
  title: string = ''
}

@Component
@Entry
struct Testing {
  @State originalTestList: TestingModel[] = []
  @State testList: TestingModel[] = []
  @State testingInfo: testingInfoData= {
    id: 0,
    title: ""
  }
  onTestingClick =  (testing: TestingModel) => {
    this.testingInfo = {
      id: testing.id,
      title: testing.title,
    }
    //设置两个基础的测试题其他为之后可以自己设置是否让学生使用
    switch (testing.id) {
      case 1:
        router.pushUrl({
          url: 'pages/function/test/basic/CharacterTest',
        })
        break
      case 2:
        router.pushUrl({
          url: 'pages/function/test/basic/AdaptiveTest',
        })
        break
      case 3:
        router.pushUrl({
          url: 'pages/function/test/TestQuestion',
          params: this.testingInfo
        })
        break
    }
  }
  @State isShowLoad: boolean = true;
  @State egDivider: DividerTmp = new DividerTmp(1, 60, 10, '#ffe9f0f0')

  //获取除两个基础的测试外其他的测试
  getTestList = async ()=>{
    const res = await testList()
    this.originalTestList = res.data
    this.testList = res.data
    this.isShowLoad = false
  }
  aboutToAppear() {
    this.getTestList()
  }

  build() {
    Column() {
      Header({title:"精选测试题库"})
      Row() {
        if (this.isShowLoad) {
          Column({ space: 20 }) {
            HmSkeleton({ count: 6 }) // 设置6排
          }
        } else {
          List() {
            ForEach(this.testList, (item:TestingModel, index) => {
              ListItem() {
                TestingItem({ testing: this.testList[index], onTestingClick: this.onTestingClick })
              }
            })
          }
          .divider(this.egDivider)
          .margin({ top: 20 })
          .scrollBar(BarState.Auto)
        }
      }
    }
    .height('100%')
    .backgroundColor('#f1f5f8')
  }
}