import {
  Banner,
  BannerOptions,
  AnimatedEnum,
  IndicatorConfig,
  CircleIndicator,
  IData,
  BannerMargin
} from '@ohos/banner'
import { FunctionBannerModel } from '../../model/function/FunctionBannerModel';
import { BannerViewModel } from '../../viewmodel/function/BannerViewModel';
import { ComponentContent, router } from '@kit.ArkUI';
import { CommonConstants } from '../../common/constant/CommonConstants';
import { FunctionConstant } from '../../common/constant/Function';

export class Data implements IData {
  title: string = '';
  subTitle: string = ''
}

@ComponentV2
export struct FunctionBanner {
  // 控制器及数据
  private swiperController: SwiperController = new SwiperController()
  @Local data: IData[] = []
  @Local indicatorConfig: IndicatorConfig = new IndicatorConfig(10, 0, '#5CB85C', '#FFFFFF')
  @Local bannerSize: number = 0
  // banner 配置
  @Local autoPlay?: boolean = true
  @Local indicator?: boolean = true
  @Local loop?: boolean = true
  @Local vertical?: boolean = false
  @Local interval?: number = 3000
  @Local duration?: number = 800
  @Local bannerMargin: BannerMargin = { left: 40, right: 40 }
  @Local animatedEnum?: AnimatedEnum = AnimatedEnum.ZoomOutPage // 缩放加渐变动效
  @Local currentPos1: number = 0
  @Local curve?: Curve = Curve.Linear
  // 设置 banner 选项
  @Local bannerOptions: BannerOptions = {
    swiperController: this.swiperController,
    autoPlay: this.autoPlay,
    interval: this.interval,
    loop: this.loop,
    duration: this.duration,
    vertical: this.vertical,
    curve: this.curve,
    bannerMargin: { left: this.bannerMargin.left, right: this.bannerMargin.right },
    animatedEnum: AnimatedEnum.ZoomOutPage, // 缩放渐变效果
    onChange: (index: number) => {
      // console.log(`当前页面index=${index}`)
      this.indicatorConfig.setCurrentPosition(index)
      this.currentPos1 = index
    }
  }
  // 页面内容数据
  BACKGROUND_COLORS = [Color.Red, Color.Orange, Color.Pink, Color.Grey]
  IMAGE_RESOURCES: Resource[] = []

  @Builder
  bannerContent(data: IData[], index: number) {
    Stack({ alignContent: Alignment.Center }) {
      // 设置图片内容
      Image(this.IMAGE_RESOURCES[index])
        .alt($r("app.media.imageLoad"))
        .objectFit(ImageFit.Cover)
        .width(CommonConstants.FULL_PARENT)
        .height(CommonConstants.FULL_PARENT)
      // 动态展示标题和副标题
      Flex({ direction: FlexDirection.Column, justifyContent: FlexAlign.Start, alignItems: ItemAlign.Center }) {
        Text(` ${(data[index] as Record<string, Object>)['title']}`)
          .fontColor(Color.White)
          .fontSize($r("app.float.2xl"))
          .margin({ bottom: $r("app.float.mp_xs") })

        Text(` ${(data[index] as Record<string, Object>)['subTitle']}`)
          .fontColor(Color.White)
          .fontSize($r("app.float.sm"))
      }
      .position({ x: 0, y: 180 })
      .width(CommonConstants.FULL_PARENT)
      .backgroundColor('#44000000')
    }
    .onClick(()=>{
      if(index == 0){
        router.pushUrl({
          url:"pages/function/test/Testing"
        })
      }
      else if(index == 1){
        router.pushUrl({
          url:"pages/function/music/MusicPage"
        })
      }else if(index == 2){
        router.pushUrl({
          url:"pages/function/motion/Motion"
        })
      }
      else if(index == 3){
        router.pushUrl({
          url:"pages/function/diary/MyMemo"
        })
      }
    })
    .width(CommonConstants.FULL_PARENT)
    .height(FunctionConstant.BANNER_HEIGHT)
  }

  //首页传入的数据
  // @Link bannerData:FunctionBannerModel[]

  // 构建页面
  build() {
    Column() {
      // banner组件
      Stack() {
        Banner({
          bannerOptions: this.bannerOptions,
          currentPosition: this.currentPos1,
          data: this.data,
          bannerContent: (item, index) => {
            this.bannerContent(item, index)
          }
        })
          .width(CommonConstants.FULL_PARENT)
          .height(FunctionConstant.BANNER_HEIGHT)
        // 指示器
        // Flex({ direction: FlexDirection.RowReverse, justifyContent: FlexAlign.Start, alignItems: ItemAlign.Center }) {
        //   Text(`${(this.currentPos1 + 1)}/${this.data.length}`).fontColor(Color.White).margin({ left: 20, right: 40 })
        //   CircleIndicator({ indicatorConfig: this.indicatorConfig })
        // }
        // .width('100%')
        // .height(40)
        // .position({ x: 0, y: 200 })
      }
      .width(CommonConstants.FULL_PARENT)
      .height(FunctionConstant.BANNER_HEIGHT)
    }
    .width(CommonConstants.FULL_PARENT)
    .height(FunctionConstant.BANNER_HEIGHT)

  }

  aboutToAppear(): void {
    this.createNewDataList();
  }

  // 创建页面数据
  createNewDataList() {
    let list = new Array<IData>();
    const bannerViewModel = new BannerViewModel
    const bannerData = bannerViewModel.getBannerData()
    //设置轮播图的数目
    this.bannerSize = bannerData.length

    for (let i = 1; i <= this.bannerSize; i++) {
      let data = new Data();
      // 设置轮播图的标题和描述
      data.title = bannerData[i-1].title;
      data.subTitle = bannerData[i-1].subTitle
      // 设置轮播图的图片
      this.IMAGE_RESOURCES.push(bannerData[i-1].url)
      list.push(data);
    }
    this.data = list;
    this.indicatorConfig.setIndicatorSize(this.data.length);
  }
}
