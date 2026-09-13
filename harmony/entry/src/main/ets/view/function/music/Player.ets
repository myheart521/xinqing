import { SongItem } from "../../../model/function/music/SongData";
import {songList} from "../../../viewmodel/function/music/SongListData"
import { displaySync } from "@kit.ArkGraphics2D";
import { MediaService } from "../../../utils/music/MediaService";
import { MusicConstant } from "../../../common/constant/Function";
import { CommonConstants } from "../../../common/constant/CommonConstants";
import { MusicControlComponent } from "./MusicControlComponent";
import { curves, window } from "@kit.ArkUI";
import { Logger } from "../../../utils/Logger";
import { BusinessError } from "@kit.BasicServicesKit";

@Component
export struct Player{
  @StorageLink('topArea') topArea: number = 0;
  @StorageLink('bottomArea') bottomArea: number = 0;
  @StorageLink('deviceHeight') deviceHeight: number = 0;


  @State componentHeight: number=0;
  @StorageProp('selectIndex') selectIndex: number = 0;
  @State songList:SongItem[] = songList
  @State imageRotate: number = 0;
  @StorageLink('isPlay') @Watch('animationFun') isPlay: boolean = false;
  @StorageLink('isShowPlay') isShowPlay: boolean = false;
  private backDisplaySyncSlow: displaySync.DisplaySync | undefined = undefined;
  private panOption: PanGestureOptions = new PanGestureOptions({ direction: PanDirection.Vertical });
  private drawFrame: (value: displaySync.IntervalInfo) => void = (value: displaySync.IntervalInfo) => {
    if (this.imageRotate >= 360 ) {
      this.imageRotate = 0;
    }
    this.imageRotate += 1;
  };
  aboutToAppear(): void {
    let range : ExpectedFrameRateRange = {
      expected: 30,
      min: 0,
      max: 30
    };
    this.backDisplaySyncSlow = displaySync.create();
    this.backDisplaySyncSlow.setExpectedFrameRateRange(range);
    this.backDisplaySyncSlow.on('frame', this.drawFrame);
  }

  aboutToDisappear(): void {
    this.backDisplaySyncSlow?.off('frame', this.drawFrame);
  }

  animationFun() {
    if (this.isPlay) {
      this.backDisplaySyncSlow?.start();
    } else {
      this.backDisplaySyncSlow?.stop();
    }
  }

  build() {
    //底部控制栏
    Row() {
      Row() {
        //播放音乐的图片
        Image(this.songList[this.selectIndex]?.label)
          .height(32)
          .width(32)
          .borderRadius(16)
          .margin({ right: $r('app.float.mp_sm') })
          .rotate({ angle: this.imageRotate })
          .onAppear(() => {
            this.animationFun();
          })
        //音乐的名字和作者
        Column() {
          Text(this.songList[this.selectIndex].title)
            .fontColor("#000000")
            .fontSize($r("app.float.sm"))
          Row() {
            Image($rawfile('functionImage/music_vip.svg'))
              .height(16)
              .width(16)
              .margin({ right: $r('app.float.xs') })
            Text(this.songList[this.selectIndex].singer)
              .fontColor("#000000")
              .fontSize($r("app.float.xs"))
              .opacity(0.38)
          }
        }
        .alignItems(HorizontalAlign.Start)
      }
      .layoutWeight(1)
      .onClick(() => {
        this.isShowPlay = true;
      })
      //右边是播放的控制器
      Blank()
        .onClick(() => {
          this.isShowPlay = true;
        })
      //播放的控制器
      Row() {
        //上一首
        Image($r('app.media.music_previous'))
          .height(MusicConstant.MUSIC_ICON_SIZE)
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .margin({ right: $r('app.float.mp') })
          .displayPriority(2)
          .onClick(() => MediaService.getInstance().playPrevious())
        //暂停与播放
        Image(this.isPlay ? $r('app.media.music_play') : $r('app.media.music_pause'))
          .height(MusicConstant.MUSIC_ICON_SIZE)
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .displayPriority(3)
          .onClick(() => {
            if (MediaService.getInstance().getFirst()) {
              MediaService.getInstance().loadAssent(0);
            } else {
              this.isPlay ? MediaService.getInstance().pause() : MediaService.getInstance().play();
            }
          })
        //下一首
        Image($r('app.media.music_next'))
          .height(MusicConstant.MUSIC_ICON_SIZE)
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .margin({
            right: $r('app.float.mp'),
            left: $r('app.float.mp')
          })
          .displayPriority(2)
          .onClick(() => MediaService.getInstance().playNextAuto(true))
        //音乐列表
        Image($r('app.media.music_music_list'))
          .height(MusicConstant.MUSIC_ICON_SIZE)
          .width(MusicConstant.MUSIC_ICON_SIZE)
          .displayPriority(1)
      }
      .width(120)
      .justifyContent(FlexAlign.End)
    }
    .width(CommonConstants.FULL_PARENT)
    .height(48)
    .backgroundColor("#F6F9FC")
    // 配置全屏模态转换：是否显示播放页面、播放页面内容、可选项（此处使用了上下切换的动画）
    .bindContentCover($$this.isShowPlay, this.musicPlayBuilder(), ModalTransition.DEFAULT)
    .padding({
      left: $r('app.float.mp'),
      right: $r('app.float.mp')
    })
    .position({
      x: 0,
      y: CommonConstants.FULL_PARENT
    })
    .translate({
      x: 0,
      y: "-48vp"
    })
    .gesture(
      PanGesture(this.panOption)
        .onActionEnd((event?: GestureEvent) => {
          if (event && event.offsetY < -10) {
            this.isShowPlay = true;
          }
        })
    )
  }
  @Builder
  musicPlayBuilder() {
    //MusicControlComponent即为音乐播放页面内容
    Column() {
      Column() {
        MusicControlComponent({ isShowPlay: this.isShowPlay })
      }
      .height((100 - this.componentHeight) + '%')
    }
    .height(CommonConstants.FULL_PARENT)
    .width(CommonConstants.FULL_PARENT)
    .justifyContent(FlexAlign.End)
    .transition(TransitionEffect.translate({ y: 1000 }).animation({ curve: curves.springMotion(0.6, 0.8) }))
    .onAppear(() => {
      window.getLastWindow(getContext(this)).then((windowStage: window.Window) => {
        let area = windowStage.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
        this.topArea = px2vp(area.topRect.height);
        let bottomArea = windowStage.getWindowAvoidArea(window.AvoidAreaType.TYPE_NAVIGATION_INDICATOR);
        this.bottomArea = px2vp(bottomArea.bottomRect.height);
        Logger.info('bottomArea ' + this.bottomArea)
        if (this.topArea > 0) {
          windowStage.setWindowLayoutFullScreen(true);
        }
        let sysBarProps: window.SystemBarProperties = {
          statusBarContentColor: '#FFFFFF'
        };
        windowStage.setWindowSystemBarProperties(sysBarProps);
      }).catch((error: BusinessError) => {
        Logger.error(`${error.code} + ${error.message}`)
      });
    })
    .onDisAppear(() => {
      this.componentHeight = 0;
      this.isShowPlay = false;
      window.getLastWindow(getContext(this)).then((windowStage: window.Window) => {
        let area = windowStage.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
        let topHeight = px2vp(area.topRect.height);
        if (topHeight > 0) {
          windowStage.setWindowLayoutFullScreen(false);
        }
        let sysBarProps: window.SystemBarProperties = {
          statusBarContentColor: '#000000'
        };
        windowStage.setWindowSystemBarProperties(sysBarProps);
      }).catch((error: BusinessError) => {
        Logger.error(`${error.code} + ${error.message}`)
      });
    })
    .gesture(
      PanGesture(this.panOption)
        .onActionUpdate((event?: GestureEvent) => {
          if (event) {
            let height = event.offsetY / this.deviceHeight * 100;
            this.componentHeight = height;
            if (this.componentHeight < 0) {
              this.componentHeight = 0;
            }
          }
        })
        .onActionEnd(() => {
          if (this.componentHeight > 40) {
            this.isShowPlay = false;
          } else {
            this.componentHeight = 0;
          }
        })
    )
  }
}