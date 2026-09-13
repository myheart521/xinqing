import { RefreshController, RefreshLayout } from "@abner/refresh"
import { DateUtil, ToastUtil } from "@pura/harmony-utils"
import { SpinKit, SpinType } from "@pura/spinkit"
import { bodyFatChartData, bodyFatScaleGet, bodyFatScaleUpdate } from "../../../api/iot/BodyFatScaleApi"
import { Title } from "../../../components/Title"
import { useData } from "../../../model/iot/BodyFatScale"
import { BarCharts } from "../../../utils/charts/BarCharts"
import { changeBodyFatScale } from "../../../utils/iot/IOTUtils"
import { router } from "@kit.ArkUI"
import { MyLine } from "../../../utils/charts/MyLine"

@Extend(Text)
function title() {
  .fontSize($r("app.float.2xl"))
  .fontColor(Color.Black)
}

@Component
export struct BodyFatScale {
  controller: RefreshController = new RefreshController() //刷新控制器
  @State useData: useData[] = []
  //---BEGIN  体脂秤开始
  //体脂秤格式化的数据
  @State useBodyData: useData[] = []
  //选择开始和结束时间
  @StorageLink("IOTBegin") @Watch("beginChange") IOTBegin: string = ""
  @StorageLink("IOTEnd") @Watch("endChange") IOTEnd: string = ""
  @State bodyFatXData: string = ""
  @State heightYData: string = ""
  @State weightYData: string = ""
  @State bmiYData: string = ""
  @State bodyFatLevelYData: string = ""
  @State isRefresh: boolean = true
  @Consume show: boolean
  //设置是否是下拉状态
  @State isPull: boolean = false

  beginChange() {
    this.getBodyData()
  }

  endChange() {
    this.getBodyData()
  }

  //---END  体脂秤结束
  aboutToAppear(): void {
    this.getBodyData()
    //体脂秤数据
    if (!this.IOTBegin || !this.IOTEnd) {
      const today = DateUtil.getToday()
      const todayTime = DateUtil.getTodayTime()
      const beforeSevenTime = todayTime - 86400000 * 7
      this.IOTBegin = DateUtil.getFormatDateStr(beforeSevenTime, "yyyy-MM-dd")
      this.IOTEnd = DateUtil.getFormatDateStr(today, "yyyy-MM-dd")
      console.log("日期为", this.IOTBegin, this.IOTEnd)
    }
  }

  //体脂秤数据获取并处理
  getBodyData = async () => {
    if (!this.isPull) {
      this.isRefresh = true
    }
    const res = await bodyFatScaleGet()
    this.useData = changeBodyFatScale(res.data)
    const res1 = await bodyFatChartData(this.IOTBegin, this.IOTEnd)
    this.bodyFatXData = res1.data.dateList
    this.heightYData = res1.data.heightList
    this.weightYData = res1.data.weightList
    this.bmiYData = res1.data.bmiList
    this.bodyFatLevelYData = res1.data.tiZhiLvList
    this.isRefresh = false
    this.isPull = false
  }
  //体脂秤下拉刷新
  bodyFatRefresh = async () => {
    this.isPull = true
    const res = await bodyFatScaleUpdate()
    this.useData = changeBodyFatScale(res.data)
    this.getBodyData()
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
        Text(useItem.data ? useItem.data + "" : "没有数据")
          .fontSize(useItem.data ? $r("app.float.3xl") : 15)
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
      .height(380)
      .columnsTemplate("1fr 1fr")
      .margin({ bottom: $r("app.float.mp_xl") })
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

    Title({ title: "身高" })
    Column() {
      MyLine({ xData: this.bodyFatXData, yData: this.heightYData })
    }
    .margin({ bottom: $r("app.float.mp_sm") })

    Title({ title: "体重" })
    Column() {
      MyLine({ xData: this.bodyFatXData, yData: this.weightYData })
    }
    .margin({ bottom: $r("app.float.mp_sm") })

    Title({ title: "BMI" })
    Column() {
      MyLine({ xData: this.bodyFatXData, yData: this.bmiYData })
    }
    .margin({ bottom: $r("app.float.mp_sm") })

    Title({ title: "体脂率" })
    Column() {
      BarCharts({ xData: this.bodyFatXData, yData: this.bodyFatLevelYData })
    }
    .margin({ bottom: 50 })

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