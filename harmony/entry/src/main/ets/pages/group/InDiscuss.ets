import InDiscussAddModel from '../../model/group/InDiscussModel'
import { router } from '@kit.ArkUI'
import { replay } from '../../model/group/Replay'
import { addInDiscuss, getReplay } from '../../api/group/DiscussApi'
import { DialogUtil } from '@pura/harmony-utils'
import DiscussTop from '../../view/group/DiscussTop'
import DiscussAdd from '../../view/group/DiscussAdd'
import InDiscussItem from '../../view/group/InDiscussItem'

interface params {
  _id: number
}

@Entry
@Component
struct InDiscuss {
  @StorageLink("range") range: string = ''
  @StorageLink("discussName") discussName: string = ''
  // //初始数据
  // @State originalData: DiscussModel[] = []
  //渲染数据
  @State discussList: replay[] = []
  @State _id: number = 0
  //获取数据的请求
  @State isAddDialogShow: boolean = false
  @State addContext: string = ''
  @State params: InDiscussAddModel = new InDiscussAddModel()
  @State status: number = 0
  //新增评论
  onAdd = async () => {
    console.log('论坛id：', this._id);
    this.params.lunTanId = this._id
    this.params.context = this.addContext
    let userName: SubscribedAbstractProperty<string> | undefined = AppStorage.link('userName');
    this.params.userName = userName.get()
    console.log('请求时的username', this.params.userName)
    const res = await addInDiscuss(this.params)
    if (res.code == 1) {
      this.getReplayData()
    }
  }
  //关闭评论
  onClose = () => {
    this.addContext = ''
    this.isAddDialogShow = false
  }
  //获取评论数据
  getReplayData = async () => {
    const res = await getReplay(this._id)
    if (res.code == 1) {
      this.discussList = res.data.pingLuns
      this.onOrderChange()
      this.onClose()
    } else {
      DialogUtil.showConfirmDialog({
        title: "获取数据失败",
        message: res.msg,
        confirm: "确定",
        onAction: () => {
        }
      })
      this.onClose()
    }
  }

  onOrderChange() {
    let orderList: replay[] = this.discussList
    const length = orderList.length
    for (let i = 0; i < length - 1; i++) {
      for (let j = 0; j < length - i - 1; j++) {
        const timeA = orderList[j].comTime
        const timeB = orderList[j+1].comTime
        if (timeA < timeB) {
          const temp = orderList[j]
          orderList[j] = orderList[j+1]
          orderList[j+1] = temp
        }
      }
    }
    // this.originalData = orderList
    this.discussList = orderList
  }

  aboutToAppear() {
    const routerParams = router.getParams() as params
    this._id = routerParams._id
    console.log("进入评论得id",this._id)
    this.getReplayData()
  }

  build() {
    Stack() {
      Column() {
        //返回
        Row({ space: 20 }) {
          Image($r('app.media.back'))
            .width(20)
            .height(20)
            .onClick(() => {
              router.back()
            })
          Row() {
            Text(this.discussName + ' 的讨论')
          }
        }
        .width('100%')
        .padding(10)
        .backgroundColor('#f1f5f8')
        .justifyContent(FlexAlign.Start)
        Column() {
          List({ space: 5, initialIndex: 0 }) {
            ListItem() {
              DiscussTop()
            }

            ForEach(this.discussList, (item: replay, index) => {
              ListItem() {
                InDiscussItem({ discuss: item })
                  .enabled(false)
              }
            })
          }
          .listDirection(Axis.Vertical)
        }
        .width('100%')
        .height('100%')
      }
      .backgroundColor('#f0f0f0')

      //发表评论
      Row() {
        Grid() {
          GridItem() {
            Image($rawfile('functionImage/diary_search.png'))
              .width(16)
          }
          .height('100%')

          GridItem() {
            Text('快来发表你的评论吧')
          }
        }
        .width('100%')
        .height(36)
        .columnsTemplate('1fr 16fr 1fr')
        .backgroundColor('#f5f6f7')
        .borderRadius(16)
        .padding({ left: 12, right: 12 })
      }
      .padding({ left: 20, right: 20 })
      .margin({ top: 12, bottom: 20 })
      .onClick(() => {
        this.isAddDialogShow = true
      })

      if (this.isAddDialogShow) {
        DiscussAdd({ addContext: this.addContext, onClose: this.onClose, onAdd: this.onAdd })
      }
    }
    .alignContent(Alignment.Bottom)
  }
}