import DiscussModel from "../../model/group/DiscussModel"
import { router } from "@kit.ArkUI"
import { setDiscussItem } from "../../store/DiscussStore"
import { replay } from "../../model/group/Replay"

//传入时间戳之后返回格式化的日期对象（字符串形式）
function getFormatTime(timeStamp: number): string {
  //timeStamp为传入函数的时间戳
  let range: string = ''
  const today = new Date()
  const date = new Date(timeStamp)
  //计算时间戳的差值
  const diffTime = today.getTime() - date.getTime()
  //单位换算
  const varyMin = 60 * 1000
  const varyHour = varyMin * 60
  const varyDay = varyHour * 24
  const varyWeek = varyDay * 7
  //计算发布时间距离当前时间的周、天、时、分
  const exceedWeek = Math.floor(diffTime / varyWeek)
  const exceedDay = Math.floor(diffTime / varyDay)
  const exceedHour = Math.floor(diffTime / varyHour)
  const exceedMin = Math.floor(diffTime / varyMin)
  const year: string = date.getFullYear().toString()
  const month: string = (date.getMonth() + 1).toString().padStart(2, '0')
  const day: string = date.getDate().toString().padStart(2, '0')
  const hours: string = date.getHours().toString().padStart(2, '0')
  const minute: string = date.getMinutes().toString().padStart(2, '0')
  //判断时间区间
  if (exceedWeek > 0) {
    range = `${year}年${month}月${day}日${hours}:${minute}`
  } else if (exceedDay < 7 && exceedDay > 0) {
    range = `${exceedDay}天前`
  } else if (exceedHour < 24 && exceedHour > 0) {
    range = `${exceedHour}小时前`
  } else if (exceedMin < 60 && exceedMin >= 10) {
    range = `${exceedMin}分钟前`
  } else {
    range = '刚刚'
  }
  //格式化的日期对象
  return range
}

@Component
export default struct InDiscussItem {
  @ObjectLink discuss: replay
  @State username: string = '示例资料'
  @State _id: number = 0
  onDiscussClick: (discussItem: DiscussModel) => void = () => {
  }

  aboutToAppear() {
    this.username = this.discuss.userName
    this._id = this.discuss.id
  }

  build() {
    Row() {
      Column() {
        //上侧
        Row() {
          Row({ space: 10 }) {
            //头像
            Column() {
              Image($r('app.media.account'))
                .width(50)
            }

            Column({ space: 10 }) {
              //用户名
              Row() {
                Text(this.username)
                  .fontSize(20)
                  .fontWeight(FontWeight.Bold)
              }

              //时间范围
              Row() {
                Text(getFormatTime(this.discuss.comTime))
                  .fontSize(14).fontColor(Color.Black)
              }
            }
            .alignItems(HorizontalAlign.Start)
          }
          .width('100%')
          .margin({ left: 10 })
        }
        .justifyContent(FlexAlign.Start)
        .width('100%')
        .height(70)
        .backgroundColor(Color.White)
        .borderRadius({ topLeft: 8, bottomLeft: 8 })

        //下侧
        Row() {
          Row() {
            Text(this.discuss.context)
              .fontColor(Color.Black)
              .maxLines(3)
          }
          .width('100%')
          .justifyContent(FlexAlign.Start)
          .alignItems(VerticalAlign.Top)
        }
        .padding(10)
        .height(90)
        .width('100%')
        .backgroundColor(Color.White)
      }
      .width('100%')
      .onClick(() => {
        setDiscussItem(getFormatTime(this.discuss.comTime), this.discuss.context, this.username)
        let rangeStorage: SubscribedAbstractProperty<string> | undefined = AppStorage.link('range');
        let contentStorage: SubscribedAbstractProperty<string> | undefined = AppStorage.link('content');
        let discussNameStorage: SubscribedAbstractProperty<string> | undefined = AppStorage.link('discussName');
        console.log('点击之后range和content和discussName分别为:', rangeStorage.get(), contentStorage.get(),
          discussNameStorage.get())
        router.pushUrl({
          url: 'pages/InDiscuss',
          //todo 跳转传参需要该评论的评论的列表
          params: {
            _id: this._id
          }
        })
      })
    }
  }
}

// @Preview
// @Component
// struct PreviewDiscussItem {
//   // @State accountName:string = 'xu'
//   @State discuss: DiscussModel = new DiscussModel(1, 1713180895057, '注意时间戳使用毫秒时间戳啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊')
//   build() {
//     // DiscussItem({ accountName:$accountName,discuss: this.discuss })
//     DiscussItem({discuss: this.discuss })
//   }
// }