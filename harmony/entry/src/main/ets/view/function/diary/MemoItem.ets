import MemoModel from "../../../model/function/diary/MemoModel"

//定义时间的接口
interface TimeFormat {
  range: string
  formatDate: string
  formatTime: string
}

//传入时间戳之后返回格式化的日期对象（字符串形式）
function getFormatTime(timeStamp:number): TimeFormat {
  //timeStamp为传入函数的时间戳
  const today = new Date()
  const date = new Date(timeStamp)
  //计算时间戳的差值
  const diffTime = today.getTime() - date.getTime()
  //向下取整
  const diffDays =  Math.floor(diffTime / (24 * 60 * 60 * 1000))
  let range: string = ''
  if (diffDays === 0) {
    range = '今天'
  } else if (diffDays === 1) {
    range = '昨天'
  } else if (diffDays === 2) {
    range = '前天'
  } else {
    const year:string = date.getFullYear().toString()
    range = year
  }
  const month:string = (date.getMonth() + 1).toString().padStart(2, '0')
  const day:string = date.getDate().toString().padStart(2, '0')
  const hours:string = date.getHours().toString().padStart(2, '0')
  const minute:string = date.getMinutes().toString().padStart(2, '0')

  //格式化的日期
  const formatDate: string = `${month}月${day}日`
  const formatTime: string = `${hours}:${minute}`
  //格式化的日期对象
  return { range, formatDate, formatTime }
}

@Component
export default struct MemoItem {
  //此处memo为类，此处不需要赋值，需要父组件传递值给它
  //memo含有id：唯一标识，updateTime：写心情的时候的时间戳，content：写的内容
  @ObjectLink memo: MemoModel
  onMemoClick: (memo: MemoModel) => void = () => {};
  build() {
    Row() {
      Stack() {
        Row() {
          //左侧
          Column({space:10}) {
            Row(){
              Text(getFormatTime(this.memo.updateTime).range)
                .fontSize($r("app.float.lg")).fontColor(Color.Black).fontWeight(FontWeight.Bold)
            }
            Row(){
              Text(getFormatTime(this.memo.updateTime).formatDate)
                .fontSize($r("app.float.sm")).fontColor(Color.Black)
            }
            Row(){
              Text(getFormatTime(this.memo.updateTime).formatTime)
                .fontSize($r("app.float.sm")).fontColor(Color.Black)
            }
          }
          .justifyContent(FlexAlign.Center)
          .width(80)
          .height(90)
          // .linearGradient({
          //   direction: GradientDirection.Bottom,
          //   colors: [['#cfc8c8', 0.5], ['#cec9c9', 1]]
          // })
          .backgroundColor(Color.White)
          .borderRadius({ topLeft: 8, bottomLeft: 8 })
          //纵向分割线
          Divider()
            .vertical(true)
            .height(90)
            .color('#f8f8f8')
          //右侧
          Column() {
            Row() {
              Text(this.memo.content)
                .fontColor(Color.Black)
                .maxLines(2)
                .margin({ right: $r("app.float.mp_lg") })
            }
            .width('100%')
            .height(64)
            .margin({ left: $r("app.float.mp_sm") })
            .justifyContent(FlexAlign.Start)
          }
          .height(90)
          .backgroundColor(Color.White)
          .borderRadius({ bottomRight: 8, topRight: 8 })
          .margin({ right: 80 })
          .justifyContent(FlexAlign.Center)
        }

      }
      .width('100%')
      .alignContent(Alignment.Start)
      .onClick(()=>{
        this.onMemoClick(this.memo)
      })
    }
    .padding({ left: 10, right: 10 })
    .margin({top:10})
  }
}

@Preview
@Component
struct MemoModelPreview {
  @State memo: MemoModel = new MemoModel(1, 1713180895057, '注意时间戳使用毫秒时间戳11111111111111115555')

  build() {
    MemoItem({ memo: this.memo })
  }
}