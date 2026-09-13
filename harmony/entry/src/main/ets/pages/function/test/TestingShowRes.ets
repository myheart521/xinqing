import { SpinKit, SpinType } from "@pura/spinkit"
import { testResult } from "../../../api/function/TestApi"
import { Header } from "../../../components/Header"
import { MyStyleCard } from "../../../components/MyStyleCard"
import { DividerTmp } from "../../../model/function/pcc/DividerTmp"
import { MyDataSource } from "../../../viewmodel/function/test/MyDataSource"
import TestingQuestionViewModel from "../../../viewmodel/function/test/TestingQuestionViewModel"
import { router } from "@kit.ArkUI"

interface routerInfo {
  data: MyDataSource,
  ans: string
}

export class TestResultShow {
  total: number=0
  level: string=""
  advice: string=""
  description: string=""
}

@Entry
@Component
struct TestingShowRes {
  @State testResult: TestResultShow = {
    total: 0,
    level: '',
    advice: '',
    description: ''
  }
  @State resultTotal: string = ''
  @State resultLevel: string = ''
  @State resultAdvice: string = ''
  @State resultDescription: string = ''
  @State isShow: boolean = false
  @State egDivider: DividerTmp = new DividerTmp(1, 60, 10, '#ffe9f0f0')

  async aboutToAppear() {
    const routerInfo: routerInfo = router.getParams() as routerInfo
    this.getResult(routerInfo.ans)
  }

  getResult = async (ans:string)=>{
    const res = await testResult(ans)
    this.testResult = res.data
    this.resultTotal = this.testResult.total.toString()
    this.resultLevel = this.testResult.level
    this.resultAdvice = this.testResult.advice
    this.resultDescription = this.testResult.description
    this.isShow = true
  }

  build() {
    if (this.isShow) {
      Column({ space: 10 }) {
        Header({ title: TestingQuestionViewModel.getTitleInfo() })
        List() {

          ListItem() {
            //得分
            MyStyleCard({ resultType: "得分", content: this.resultTotal })
          }

          ListItem() {
            //结果
            MyStyleCard({ resultType: "结果", content: this.resultLevel })
          }

          ListItem() {
            //建议
            MyStyleCard({ resultType: "建议", content: this.resultAdvice })
          }

          ListItem() {
            //症状
            MyStyleCard({ resultType: "症状", content: this.resultDescription })
          }
        }
        .divider(this.egDivider)
        .margin({ top: 10 })
        .scrollBar(BarState.Auto)
      }
      .height('100%')
      .backgroundColor("#f2f3f5")
      .width('100%')

    } else {
      SpinKit({
        spinType: SpinType.spinC,
        spinColor: "#dd5145",
        spinSize: 80
      })
    }
  }
}