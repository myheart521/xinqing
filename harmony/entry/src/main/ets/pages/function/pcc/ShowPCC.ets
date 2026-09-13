import { HmSkeleton } from '@harmonypass/ivskeleton'
import { DividerTmp } from '../../../model/function/pcc/DividerTmp'
import { PCCModel } from '../../../model/function/pcc/PCCModel'
import { PCCItem } from '../../../view/function/pcc/PCCItem'
import { router } from '@kit.ArkUI'
import { pccData } from '../../../api/function/PccApi'
import { Header } from '../../../components/Header'

@Entry
@Component
struct ShowPCC {
  @State AllPCC: PCCModel[] = []
  @State OriginalAllPCC: PCCModel[] = []
  //加载展示骨架屏效果
  @State showSkeleton: boolean = true
  @State egDivider: DividerTmp = new DividerTmp(1, 60, 10, '#ffe9f0f0')

  async aboutToAppear() {
    this.getPccData()
  }

  getPccData = async () => {
    const res = await pccData()
    this.OriginalAllPCC = res.data
    this.AllPCC = res.data
    this.showSkeleton = false
  }

  build() {
    if (this.showSkeleton) {
      Column({ space: 20 }) {
        HmSkeleton({ count: 6 }) // 设置6排
      }

    } else {
      Column() {
        Row() {
          //返回
          Header({title:"大学生心理咨询中心"})
        }
        .zIndex(999)
        Row() {
          List() {
            ForEach(this.OriginalAllPCC, (item: PCCModel, index: number) => {
              ListItem() {
                PCCItem({ pCCItem: this.OriginalAllPCC[index] })
              }
            })
          }
          .margin({ top: 10 })
          .padding(10)
          .divider(this.egDivider)
          .scrollBar(BarState.Auto)
        }
      }
      .backgroundColor("#f2f3f5")
      .height('100%')
    }
  }
}

