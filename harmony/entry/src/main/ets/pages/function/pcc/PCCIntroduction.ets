import { DividerTmp } from '../../../model/function/pcc/DividerTmp'
import { DoctorItemModel } from '../../../model/function/pcc/DoctorItemModel'
import { PCCIntroModel } from '../../../model/function/pcc/PCCIntroModel'
import { router } from '@kit.ArkUI'
import { pccInfo } from '../../../api/function/PccApi'
import { PCCTopCard } from '../../../view/function/pcc/PCCTopCard'
import { DoctorItem } from '../../../view/function/pcc/DoctorItem'
import { Header } from '../../../components/Header'


class routerInfo {
  id: number = 0
}

@Entry
@Component
struct PCCIntroduction {
  @State pCCId: number = 0
  @State egDivider: DividerTmp = new DividerTmp(1, 60, 10, '#ffe9f0f0')
  @State introductionPCC: PCCIntroModel = new PCCIntroModel('0391-3987099',
    'xinlizixun@hup.edu.cn', '2号学生公寓楼一楼', "示例大学心理支持中心",
    "登录企业微信，在心理健康模块根据提示操作预约咨询。",
    '示例大学',
    ' 示例大学心理健康教育与咨询中心位于我校原理化楼一楼，内部环境幽雅、温馨、舒适、和谐，设有音乐放松室，个体沙盘室，团体沙盘室，个体咨询室，家庭会谈室，朋辈/学业辅导室、图书阅览室，网络/热线咨询室，会议室，团体辅导室等功能单元，房间均配备有专业先进的心理健康教育器材。\r\n经验丰富的专业心理咨询师团队，做您心灵的守护者。数位高学历兼经验丰富的老师，可以全方位的帮助大学生排除学习、生活、交往、择业以及成长等方面存在的困扰，提高大学生心理素质，促进成长成才。\r\n    长期以来，心理健康教育与咨询中心坚持“立德树人”根本任务，遵循青年大学生成长成才规律，宣传普及心理健康教育知识，不断增强学生心理健康意识，优化心理品质，全面服务学生健康快乐成长。',
    '河南', 'https://assets.example.invalid/placeholder.png',
    [],
    [],
  )
  @State doctorList: DoctorItemModel[] = []

  async aboutToAppear() {
    const getInfo: routerInfo = router.getParams() as routerInfo
    this.pCCId = getInfo.id
    this.getPccInfo(this.pCCId)
  }

  getPccInfo = async (pccId: number) => {
    const res = await pccInfo(pccId)
    this.introductionPCC = res.data
    this.doctorList = this.introductionPCC.doctorList
  }

  build() {
    Column({ space: 10 }) {


      Row() {
        // 心理咨询老师介绍
        List() {
          //返回
          ListItem(){
            Row() {
              //返回
              Header({title:"大学生心理咨询中心"})
            }
            .zIndex(999)
          }
          //顶部学校信息介绍
          ListItem() {
            Row() {
              PCCTopCard({ introductionPCC: this.introductionPCC })
            }
          }

          ForEach(this.doctorList, (item: DoctorItemModel, index: number) => {
            ListItem() {
              DoctorItem({ doctorItem: this.doctorList[index] })
            }
          })
        }
        .divider(this.egDivider)
        .margin({ top: 10 })
        .scrollBar(BarState.Auto)
      }
    }
    .backgroundColor('#f2f3f5')
  }
}