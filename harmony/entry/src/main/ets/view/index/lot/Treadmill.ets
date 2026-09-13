import { AnimateView, Animator, AnimatorState, createFlipInXAnimator, createHeartBeatAnimator } from "@animate/style"
import LineChart from "@ohos/mpchart/src/main/ets/components/chartcomponents/LineChart"
import { TreadmillItem } from "../../../model/index/TreadmillItem"
import { MyLine } from "../../../utils/charts/MyLine"
import { TreadmillViewModel } from "../../../viewmodel/index/iot/TreadmillViewModel"


@Extend(Text)
function title() {
  .fontSize($r("app.float.2xl"))
  .fontColor(Color.Black)
  .fontWeight(FontWeight.Bold)
}

@Extend(Text)
function connected() {
  .fontSize($r("app.float.2xl"))
  .fontColor(Color.Green)
  .margin({ left: $r("app.float.mp_xs") })
}

@Extend(Text)
function unConnected() {
  .fontSize($r("app.float.2xl"))
  .fontColor(Color.Gray)
  .margin({ left: $r("app.float.mp_xs") })
}

@Component
export struct Treadmill {
  @Styles
  card(){
    .shadow(ShadowStyle.OUTER_DEFAULT_SM)
    .backgroundColor(this.show ? "#c1c2c4" : Color.White)
    .height(180)
    .borderRadius(20)
    .padding($r("app.float.mp"))
  }

  @Styles
  images(){
    .width(30)
    .height(30)
  }

  @State treadmillData: TreadmillViewModel = new TreadmillViewModel()
  @Consume show: boolean
  // 动画相关 start
  animator: Animator<AnimatorState> = createHeartBeatAnimator()
  ctx?: UIContext
  //动画相关end
  scroller: Scroller = new Scroller()

  aboutToAppear(): void {
    // this.treadmillData.setUptime(true)
    this.ctx = this.getUIContext()
  }

  build() {
    Column() {
      Grid() {
        // 蓝牙
        GridItem() {
          AnimateView({ animator: this.animator }) {
            Column() {
              // 文字
              Row() {
                Text("蓝牙状态")
                  .title()
              }

              Row() {
                if (this.treadmillData.getTreadmillData().bluetooth) {
                  Image($rawfile("iotImage/bluetoothConnected.png"))
                    .images()
                  Text("蓝牙已连接")
                    .connected()
                } else {
                  Image($rawfile("iotImage/bluetoothUnconnected.png"))
                    .images()
                  Text("蓝牙未连接")
                    .unConnected()
                }
              }
              .margin({ top: $r("app.float.mp_xl") })
            }
          }
        }
        .card()
        //   是否开机
        GridItem() {
          Column() {
            // 文字
            Row() {
              Text("机器状态")
                .title()
            }

            Row() {
              if (this.treadmillData.getTreadmillData().isUptime) {
                Image($rawfile("iotImage/start.png"))
                  .images()
                Text("已开机")
                  .connected()
              } else {
                Image($rawfile("iotImage/unStart.png"))
                  .images()
                Text("未开机")
                  .unConnected()
              }
            }
            .margin({ top: $r("app.float.mp_xl") })

          }
        }
        .card()

        //模式
        GridItem() {
          Column() {
            // 文字
            Row() {
              Text("机器模式")
                .title()
            }

            Row() {

              Image($rawfile("iotImage/programs.png"))
                .images()
              Text(this.treadmillData.getTreadmillData().programs)
                .connected()

            }
            .margin({ top: $r("app.float.mp_xl") })
          }
        }
        .card()

        //目前承受重量
        GridItem() {
          Column() {
            // 文字
            Row() {
              Text("重量(当前/最多)")
                .title()
            }

            Row() {
              Image($rawfile("iotImage/weight.png"))
                .images()
              Text(this.treadmillData.getTreadmillData().weightCapacity)
                .connected()
            }
            .margin({ top: $r("app.float.mp_xl") })
          }
        }
        .card()

        //折线图
        GridItem() {
          Column() {
            Row() {
              Image($r("app.media.functionTitleStar"))
                .width(30)
                .height(30)
                .margin({ right: $r("app.float.mp_sm") })
              Text("近七周运动时间")
                .padding($r("app.float.mp_xs"))
                .fontSize($r("app.float.3xl"))
                .fontColor("#229EFD")
                .fontWeight(FontWeight.Bold)
              Image($r("app.media.functionTitleStar"))
                .width(30)
                .height(30)
                .margin({ right: $r("app.float.mp_sm") })
            }
            MyLine({xData:"2024-10-28,2024-10-29,2024-10-30,2024-10-31",yData:"5,20,22,23"})
          }
        }
        .rowStart(2).rowEnd(4).columnStart(0).columnEnd(1)
      }
      .columnsTemplate("1fr 1fr")
      .margin({ bottom: 63 })
      .columnsGap($r("app.float.mp"))
      .rowsGap($r("app.float.mp"))
    }
  }
}