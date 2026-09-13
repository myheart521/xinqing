import { SpinKit, SpinType } from '@pura/spinkit';
import { getKnowledgeData } from '../api/function/KnowledgeApi';
import { CommonConstants } from '../common/constant/CommonConstants';
import { IndexConstants } from '../common/constant/Index';
import knowledgeViewModel from '../viewmodel/function/knowledge/KnowledgeViewModel';
import { songList } from '../viewmodel/function/music/SongListData';
import { ChatIndex } from './chat/ChatIndex';
import { FunctionIndex } from './function/FunctionIndex';
import Discuss from './group/Discuss';
import { SliderBar } from './index/SliderBar';
import { MyIndex } from './my/MyIndex';


@Entry
@Component
struct Index {
  navPathStack: NavPathStack = new NavPathStack()

  @Builder
  TabBuilder(title: Resource, index: number, selectedImg: Resource, normalImg: Resource) {
    Column() {
      Image(this.currentIndex === index ? selectedImg : normalImg)
        .width($r('app.float.mainPage_baseTab_size'))
        .height($r('app.float.mainPage_baseTab_size'))
        .offset({
          y: (this.currentIndex === index)
            ? this.iconOffset : this.initNumber
        })
        .objectFit(ImageFit.Contain)
        .animation({
          duration: IndexConstants.ANIMATION_DURATION,
          curve: Curve.Ease,
          playMode: PlayMode.Normal
        })
      // Image(this.currentIndex === index ? selectedImg : normalImg)//如果 index 为 当前currentIndex，则 selectedImg 否则 normalImg
      //   .width($r('app.float.mainPage_baseTab_size'))
      //   .height($r('app.float.mainPage_baseTab_size'))
      Text(title)
        .margin({ top: $r('app.float.mainPage_baseTab_top') })
        .fontSize($r('app.float.xs'))
        .fontColor(this.currentIndex === index ? $r('app.color.mainPage_selected') : $r('app.color.mainPage_normal'))
    }
    .justifyContent(FlexAlign.Center)
    .height($r('app.float.mainPage_barHeight'))
    .width(CommonConstants.FULL_PARENT)
    .onClick(() => {
      this.currentIndex = index;
      this.tabsController.changeIndex(this.currentIndex);
      // 告诉 tabsController 对象，根据 this.currentIndex 的值来切换或改变当前活动的标签页索引
      this.iconOffset = IndexConstants.ICON_Offset;
    })
  }

  private tabsController: TabsController = new TabsController();
  @State currentIndex: number = 0; //值为 0
  @State iconOffset: number = 0;
  @State initNumber: number = 0;

  aboutToAppear(): void {
    //设置音乐的数据
    AppStorage.setOrCreate("songList", songList)
    this.getData()
  }

  //请求数据
  getData = async () => {
    const res = await getKnowledgeData()
    knowledgeViewModel.setZonesList(res.data)
  }

  build() {

    Tabs({
      barPosition: BarPosition.End,
      controller: this.tabsController
    }) {
      TabContent() {
        // 物联网模块
        SliderBar()
      }
      .backgroundColor($r('app.color.bg_gray'))
      .tabBar(this.TabBuilder($r("app.string.home_title"), 0,
        $r('app.media.tab_home_selected'), $r("app.media.tab_home_normal")))
      TabContent() {
        //   功能模块
        FunctionIndex()
      }
      .padding({ left: $r('app.float.mp_sm'), right: $r('app.float.mp_sm') })
      .backgroundColor($r('app.color.bg_gray'))
      .tabBar(this.TabBuilder($r("app.string.function"), 1,
        $r("app.media.tab_function_selected"), $r("app.media.tab_function_normal")))

      TabContent() {
        //论坛模块
        Discuss()
      }
      .backgroundColor($r('app.color.bg_gray'))
      .tabBar(this.TabBuilder($r("app.string.group"), 2,
        $r("app.media.tab_chat_selected"), $r("app.media.tab_chat_normal")))

      TabContent() {
        ChatIndex()
        //   AI聊天模块
      }
      .padding({ left: $r('app.float.mp_sm'), right: $r('app.float.mp_sm') })
      .backgroundColor($r('app.color.bg_gray'))
      .tabBar(this.TabBuilder($r("app.string.chat"), 3,
        $r("app.media.tab_group_select"), $r("app.media.tab_group_normal")))

      TabContent() {
        //   我的页面
        MyIndex()
      }
      .padding({ left: $r('app.float.mp_sm'), right: $r('app.float.mp_sm') })
      .backgroundColor($r('app.color.bg_gray'))
      .tabBar(this.TabBuilder($r("app.string.mine"), 4,
        $r("app.media.tab_mine_selected"), $r("app.media.tab_mine_normal")))
    }
    .width(CommonConstants.FULL_PARENT)
    .backgroundColor(Color.White)
    .barHeight($r('app.float.mainPage_barHeight'))
    .barMode(BarMode.Fixed)
    .onChange((index: number) => {
      this.currentIndex = index;
    })
  }
}