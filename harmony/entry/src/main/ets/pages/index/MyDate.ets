import { DateUtil } from '@pura/harmony-utils/src/main/ets/utils/DateUtil';
import { CJDateItem, CJCalendar, OptMode } from 'cjcalendar';
import { Header } from '../../components/Header';
import { router } from '@kit.ArkUI';
import { setIOTData } from '../../store/IOTStore';

interface routerParams{
  begin:string
  end:string
}
@Entry
@Component
export struct MyData {
  @State begin: string=''
  @State end: string=''


  build() {
    Column() {
      Header({ title: "选择日期" })
      Scroll() {
        Column() {
          CJCalendar({
            optMode: OptMode.RANGE, // 启用范围选择模式
            onSelectedChanged: (items: Array<CJDateItem>) => {
              if (items.length >= 2) {
                this.begin = DateUtil.getFormatDateStr(items[0].time, "yyyy-MM-dd")
                this.end = DateUtil.getFormatDateStr(items[1].time, "yyyy-MM-dd")
                console.log("选择的时间范围：", this.begin, "到", this.end);
              }
            }
          })
            .margin({ top: $r("app.float.mp_sm") })
          Text("选择的日期范围为：")
            .fontSize($r("app.float.2xl"))
            .fontColor($r("app.color.purple_gray"))
            .opacity(0.9)
            .margin({ top: $r("app.float.mp_lg") })
          Row() {
            Text(this.begin)
              .fontSize($r("app.float.3xl"))
              .fontWeight(FontWeight.Bold)
              .fontColor($r("app.color.purple_gray"))
              .opacity(0.9)
            Text(this.end)
              .fontSize($r("app.float.3xl"))
              .fontWeight(FontWeight.Bold)
              .fontColor($r("app.color.purple_gray"))
              .opacity(0.9)
              .margin({ left: $r("app.float.mp_xl") })
              .height(30)
          }
          .margin({ top: $r("app.float.mp_xl") })

          Row() {
            Button("确认")
              .backgroundColor('#f39c97')
              .width("60%")
              .height(50)
              .onClick(() => {
                setIOTData(this.begin,this.end)
                router.back()
              })
          }
          .margin({ top: 60})
        }
        .justifyContent(FlexAlign.Center)
      }
      .align(Alignment.Top)
      .layoutWeight(1)
    }
    .height("100%")
  }
}