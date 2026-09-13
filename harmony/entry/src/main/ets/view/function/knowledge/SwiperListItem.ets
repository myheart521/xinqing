import { KnowledgeConstant } from "../../../common/constant/Function";
import KnowledgeItem from "../../../model/function/knowledge/KnowledgeItem";
import KnowledgeViewModel from "../../../viewmodel/function/knowledge/KnowledgeViewModel";


@Component
export default struct SwiperListItem {
  private swiperController: SwiperController = new SwiperController();
  @State zoneList: KnowledgeItem[] = KnowledgeViewModel.getZonesList();
  @Prop imageHeight: number = 0;
  @Prop currentZoneId: number = 0;
  @Prop arrowIconOpacity: number = 0;
  @Consume('introductionData') introductionData: KnowledgeItem;

  build() {
    Stack({ alignContent: Alignment.Bottom }) {
      Swiper(this.swiperController) {
        ForEach(this.zoneList, (item: KnowledgeItem) => {
          Image(item.swiperPic)
            .width(KnowledgeConstant.FULL_SIZE)
            .height(this.imageHeight + '%')
            .objectFit(ImageFit.Cover)
            .sharedTransition(item.id.toString(), {
              duration: KnowledgeConstant.TRANSITION_DURATION,
              curve: Curve.Smooth,
              delay: 0
            })
        }, (item: KnowledgeItem) => JSON.stringify(item))
      }
      .index(this.currentZoneId)
      .indicator(this.imageHeight < KnowledgeConstant.FULL_PERCENT_NUMBER ? false : true)
      .loop(true)
      .cachedCount(KnowledgeConstant.IMAGE_SWIPER_CACHE_COUNT)
      .curve(Curve.EaseOut)
      .disableSwipe(this.imageHeight < KnowledgeConstant.FULL_PERCENT_NUMBER ? true : false)
      .indicatorStyle({
        bottom: 250
      })
      .onChange((index: number) => {
        this.currentZoneId = index;
        this.getIntroductionData();
      })
      .index(this.currentZoneId)
      .indicator(this.imageHeight < KnowledgeConstant.FULL_PERCENT_NUMBER ? false : true)
      .loop(true)
      .cachedCount(KnowledgeConstant.IMAGE_SWIPER_CACHE_COUNT)
      .curve(Curve.EaseOut)
      .disableSwipe(this.imageHeight < KnowledgeConstant.FULL_PERCENT_NUMBER ? true : false)
      .indicatorStyle({
        bottom: 250
      })
      .onChange((index: number) => {
        this.currentZoneId = index;
        this.getIntroductionData();
      })

      Text(this.introductionData.title)
        .fontSize(28)
        .fontWeight(FontWeight.Bold)
        .fontColor("#FFFFFF")
        .opacity(this.arrowIconOpacity)
        .width(KnowledgeConstant.FULL_SIZE)
        .textAlign(TextAlign.Center)
        .height(38)
        .margin({ bottom: 120 })
        .animation({
          duration: KnowledgeConstant.ANIMATION_DURATION_NORMAL,
          curve: Curve.EaseOut,
        })
      Image($r("app.media.arrow_down"))
        .height(10)
        .width(34)
        .margin({ bottom: 63 })
        .opacity(this.arrowIconOpacity)
        .animation({
          duration: KnowledgeConstant.ANIMATION_DURATION_NORMAL,
          curve: Curve.EaseOut,
        })
    }
  }

  // Get the introduction by currentZoneId.
  getIntroductionData() {
    this.introductionData = this.zoneList.filter((item) => item.id === this.currentZoneId)[0];
  }
}