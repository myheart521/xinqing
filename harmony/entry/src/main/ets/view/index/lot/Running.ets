import { RefreshController, RefreshLayout } from "@abner/refresh"
import { DateUtil, ToastUtil } from "@pura/harmony-utils"
import { SpinKit, SpinType } from "@pura/spinkit"
import { Title } from "../../../components/Title"
import { useData } from "../../../model/iot/BodyFatScale"
import { changeBodyFatScale, changeRunning } from "../../../utils/iot/IOTUtils"
import { router } from "@kit.ArkUI"
import { MyLine } from "../../../utils/charts/MyLine"
import { runningChartData, runningGet, runningUpdate } from "../../../api/iot/RunningApi"

@Extend(Text)
function title() {
  .fontSize($r("app.float.2xl"))
  .fontColor(Color.Black)
}
@Component
export struct Running {
  controller: RefreshController = new RefreshController() //刷新控制器
  @State useData: useData[] = []
  //---BEGIN  体脂秤开始
  //体脂秤格式化的数据
  @State useBodyData: useData[] = []
  //选择开始和结束时间
  @StorageLink("IOTBegin") @Watch("beginChange") IOTBegin: string = ""
  @StorageLink("IOTEnd") @Watch("endChange") IOTEnd: string = ""
  @State runningXData: string = ""
  @State longTimeYData: string = ""
  @State distanceYData: string = ""
  @State isRefresh: boolean = true
  @Consume show: boolean
  @State isPull:boolean = false

  beginChange() {
    this.getRunningData()
  }

  endChange() {
    this.getRunningData()
  }

  aboutToAppear(): void {
    this.getRunningData()
    //数据
    if (!this.IOTBegin || !this.IOTEnd) {
      const today = DateUtil.getToday()
      const todayTime = DateUtil.getTodayTime()
      const beforeSevenTime = todayTime - 86400000 * 6
      this.IOTBegin = DateUtil.getFormatDateStr(beforeSevenTime, "yyyy-MM-dd")
      this.IOTEnd = DateUtil.getFormatDateStr(today, "yyyy-MM-dd")
      console.log("日期为", this.IOTBegin, this.IOTEnd)
    }
  }

  //跑步数据获取并处理
  getRunningData = async () => {
    if (!this.isPull) {
      this.isRefresh = true
    }
    const res = await runningGet()
    this.useData = changeRunning(res.data)
    const res1 = await runningChartData(this.IOTBegin, this.IOTEnd)
    this.runningXData = res1.data.dateList
    this.longTimeYData = res1.data.longTimeList
    console.log("跑步的总时间：",this.longTimeYData)
    this.distanceYData = res1.data.distanceCountList
    this.isRefresh = false
    this.isPull = false
  }
  //跑步下拉刷新
  bodyFatRefresh = async () => {
    this.isPull = true
    const res = await runningUpdate()
    this.useData = changeRunning(res.data)
    this.getRunningData()
    if (res.code == 1) {
      this.controller.finishRefresh()
    } else {
      ToastUtil.showShort("刷新失败");
      this.controller.finishRefresh()
    }
  }

  @Styles
  images(){
    .width(60)
    .height(60)
  }

  @Builder
  content(useItem: useData) {
    Column() {
      //图片
      Row() {
        Image(useItem.image)
          .images()
      }
      // 文字
      Row() {
        Text(useItem.info + " : ")
          .title()
        Text(useItem.data?useItem.data+"":"无数据")
          .fontSize(useItem.data?$r("app.float.3xl"):15)
          .fontColor("#00d886")
          .fontWeight(FontWeight.Bold)
      }
      .margin({ top: $r("app.float.mp_xl") })
    }
  }

  @Builder
  layout() {
    Column() {
      Grid() {
        if (!this.isRefresh) {
          ForEach(this.useData, (item: useData) => {
            GridItem() {
              this.content(item)
            }
            .shadow(ShadowStyle.OUTER_DEFAULT_SM)
            .backgroundColor(this.show ? "#c1c2c4" : Color.White)
            .height(180)
            .borderRadius(20)
            .padding($r("app.float.mp"))
          })
        }
      }
      .height(190)
      .columnsTemplate("1fr 1fr")
      .margin({ bottom: 25})
      .columnsGap($r("app.float.mp"))
      .rowsGap($r("app.float.mp"))
    }

    Row() {
      Image($rawfile("iotImage/calendar.svg"))
        .width(60)
        .height(60)
      Text("选择日期范围")
        .fontSize($r("app.float.2xl"))
        .fontWeight(FontWeight.Bold)
        .fontColor($r("app.color.purple_gray"))
        .opacity(0.9)
    }
    .onClick(() => {
      router.pushUrl({
        url: "pages/index/MyDate"
      })
    })
    if (this.IOTBegin && this.IOTEnd) {
      Row() {
        Text(this.IOTBegin)
          .fontSize($r("app.float.3xl"))
          .fontWeight(FontWeight.Bold)
          .fontColor($r("app.color.purple_gray"))
          .opacity(0.9)
        Text(this.IOTEnd)
          .fontSize($r("app.float.3xl"))
          .fontWeight(FontWeight.Bold)
          .fontColor($r("app.color.purple_gray"))
          .opacity(0.9)
          .margin({ left: $r("app.float.mp_xl") })
          .height(30)
      }
      .margin({ top: $r("app.float.mp_xl") })
    }

    Title({ title: "总时长" })
    Column() {
      MyLine({ xData: this.runningXData, yData: this.longTimeYData })
    }
    .margin({ bottom: $r("app.float.mp_sm") })

    Title({ title: "总距离" })
    Column() {
      MyLine({ xData: this.runningXData, yData: this.distanceYData })
    }
    .margin({ bottom: 60 })

  }

  build() {
    if (this.isRefresh) {
      SpinKit({
        spinType: SpinType.spinC,
        spinColor: "#dd5145",
        spinSize: 40
      })
    } else {
      RefreshLayout({
        itemLayout: () => {
          this.layout()
        },
        controller: this.controller,
        onRefresh: () => {
          //传递控制刷新的参数
          this.bodyFatRefresh()
        },
      })
    }
  }
}