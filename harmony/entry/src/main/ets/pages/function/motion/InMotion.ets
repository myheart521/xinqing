import { InMotionInfoModel } from '../../../model/function/motion/InMotionInfoModel';
import { router } from '@kit.ArkUI';
import { Header } from '../../../components/Header';
import { MotionType } from '../../../view/function/motion/MotionType';
import { motionInfoRequest } from '../../../api/function/MotionApi';

interface paramInfo {
  id: number
}

@Entry
@Component
struct InMotion {
  scroller: Scroller = new Scroller;
  outerScroller: Scroller = new Scroller;
  @State InMotionInfo: InMotionInfoModel = new InMotionInfoModel(0,'','',0,'',[],'','','','',[])

  @State curRate: PlaybackSpeed = PlaybackSpeed.Speed_Forward_1_00_X
  @State isAutoPlay: boolean = false
  @State showControls: boolean = true
  @State controllerNum: number = 0
  controller: VideoController = new VideoController()
  @State previewUris: string = ''
  @State videoSrc: string = ''

  async aboutToAppear() {
    const getParamsInfo: paramInfo = router.getParams() as paramInfo
    const id = getParamsInfo.id
    console.log("InMotion页面做一个标记,id:", id)
    this.getMotionInfo(id)

  }

  getMotionInfo= async (id:number)=>{
    const res = await motionInfoRequest(id)
    this.InMotionInfo = res.data
    this.previewUris = this.InMotionInfo.imageList[0]
    this.videoSrc = this.InMotionInfo.video
    console.log("video地址：",res.data.video)
  }

  build() {
    Column() {
      //返回
      Row() {
        Header({title:"运动"})
      }
      //视频
      Column() {
        Video({
          src: this.videoSrc,
          previewUri: this.previewUris,
          currentProgressRate: this.curRate,
          controller: this.controller
        })
          .autoPlay(this.isAutoPlay)
          .controls(this.showControls)
          .height("100%")
      }.height(330)
      // 大标题,最多两行
      Row() {
        Text(this.InMotionInfo.title)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
          .maxLines(2)
          .fontSize(22)
          .fontWeight(500)
      }
      .margin({ top: 30 })
      .justifyContent(FlexAlign.Start)
      // 等级,时长,消耗热量,运动特点
      Row({ space: 25 }) {
        Column() {
          Text(this.InMotionInfo.league)
            .fontColor(Color.Black)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
          Text(this.InMotionInfo.leagueDescribe)
            .maxLines(1)
            .fontColor('#a9a9a9')
            .fontSize(15)
        }
        .justifyContent(FlexAlign.Start)

        .alignItems(HorizontalAlign.Start)
        .height(30)

        Column() {
          Text("" + this.InMotionInfo.time)
            .fontColor(Color.Black)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
          Text("分钟")
            .maxLines(1)
            .fontColor('#a9a9a9')
            .fontSize(15)
        }
        .justifyContent(FlexAlign.Start)

        .alignItems(HorizontalAlign.Start)
        .height(30)

        Column() {
          Text(this.InMotionInfo.cal)
            .fontColor(Color.Black)
            .fontSize(18)
            .fontWeight(FontWeight.Bold)
          Text("千卡")
            .maxLines(1)
            .fontColor('#a9a9a9')
            .fontSize(15)
        }
        .alignItems(HorizontalAlign.Start)
        .justifyContent(FlexAlign.Start)
        .height(30)

        Row() {
          Grid() {
            ForEach(this.InMotionInfo.motionFeatures, (item: string, index: number) => {
              GridItem() {
                MotionType({ content: this.InMotionInfo.motionFeatures[index] })
              }
            })
          }
          .rowsTemplate('1fr 1fr')
          .columnsTemplate('1fr 1fr 1fr')
        }
        .borderColor("#a9a9a9")
        .alignItems(VerticalAlign.Center)
        .width(155)
        .height(45)
      }
      .padding(10)
      .width("100%")
      .alignItems(VerticalAlign.Top)
      // 运动详细描述
      Row() {
        Scroll(this.scroller) {
          Text(this.InMotionInfo.text)
            .fontColor('#707070')
            .fontSize(13)
        }
        .height('100%')
      }
      .width("100%")
      .height(170)

      //GO!!!
      Row({ space: 10 }) {
        Button("暂停")
          .width(65)
          .height(40)
          .fontStyle(FontStyle.Italic)
          .fontSize(13)
          .fontWeight(FontWeight.Bolder)
          .backgroundColor("#24c68a")
          .onClick(() => {
            this.controller.pause()
          })
        Button("x0.75")
          .width(70)
          .height(40)
          .fontStyle(FontStyle.Italic)
          .fontSize(13)
          .fontWeight(FontWeight.Bolder)
          .backgroundColor("#24c68a")
          .onClick(() => {
            this.curRate = PlaybackSpeed.Speed_Forward_0_75_X
          })
        Button("G0")
          .width(75)
          .height(75)
          .fontStyle(FontStyle.Italic)
          .fontSize(30)
          .fontWeight(FontWeight.Bolder)
          .backgroundColor("#24c68a")
          .onClick(() => {
            this.controller.start()
            // this.controllerNum = 2
          })
        Button("X1")
          .width(65)
          .height(40)
          .fontStyle(FontStyle.Italic)
          .fontSize(13)
          .fontWeight(FontWeight.Bolder)
          .backgroundColor("#24c68a")
          .onClick(() => {
            this.curRate = PlaybackSpeed.Speed_Forward_1_00_X
          })
        Button("X2")
          .width(65)
          .height(40)
          .fontStyle(FontStyle.Italic)
          .fontSize(13)
          .fontWeight(FontWeight.Bolder)
          .backgroundColor("#24c68a")
          .onClick(() => {
            this.curRate = PlaybackSpeed.Speed_Forward_2_00_X
          })
      }
      .justifyContent(FlexAlign.Center)
      .margin({ top: 20 })
      .width("100%")
      .height(90)
    }
  }
}