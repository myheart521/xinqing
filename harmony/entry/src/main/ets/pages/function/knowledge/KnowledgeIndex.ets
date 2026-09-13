import { KnowledgeConstant } from '../../../common/constant/Function';
import KnowledgeItem from '../../../model/function/knowledge/KnowledgeItem';
import KnowledgeViewModel from '../../../viewmodel/function/knowledge/KnowledgeViewModel';
import { router } from '@kit.ArkUI';
import { Header } from '../../../components/Header';
import { SpinKit, SpinType } from '@pura/spinkit';

@Entry
@Component
export struct KnowledgeIndex {
  private changedIndex: boolean = true;
  private zonesList: KnowledgeItem[] = KnowledgeViewModel.getZonesList();
  @State aheadIndex: number = KnowledgeConstant.HALF_COUNT;
  @State marginBottom: number = 0;

  getImgCoefficients(index?: number): number {
    if (index === undefined) {
      return 0;
    }
    let coefficient = this.aheadIndex - index;
    let tempCoefficient = Math.abs(coefficient);
    if (tempCoefficient <= KnowledgeConstant.HALF_COUNT) {
      return coefficient;
    }
    let dataLength = this.zonesList.length;
    let tempOffset = dataLength - tempCoefficient;
    if (tempOffset <= KnowledgeConstant.HALF_COUNT) {
      if (coefficient > 0) {
        return -tempOffset;
      }
      return tempOffset;
    }
    return 0;
  }

  getOffSetY(index?: number): number {
    if (index === undefined) {
      return 0;
    }
    let offsetIndex = this.getImgCoefficients(index);
    let tempOffset = Math.abs(offsetIndex);
    let offsetY = this.marginBottom / (tempOffset + 1);
    if (tempOffset === 1) {
      offsetY += -offsetIndex * KnowledgeConstant.MAX_OFFSET_Y;
    } else if (tempOffset === KnowledgeConstant.HALF_COUNT) {
      offsetY += -offsetIndex * (KnowledgeConstant.MAX_OFFSET_Y - KnowledgeConstant.OFFSET_COEFFICIENTS);
    }
    return offsetY;
  }

  startAnimation(isUp: boolean): void {
    animateTo({
      duration: KnowledgeConstant.SWIPER_DURATION,
    }, () => {
      let dataLength = this.zonesList.length;
      let tempIndex = isUp ? this.aheadIndex + 1 : dataLength + this.aheadIndex - 1;
      this.aheadIndex = tempIndex % dataLength;
      this.marginBottom = 0;
    });
  }

  handlePanGesture(offsetY: number): void {
    if (Math.abs(offsetY) < KnowledgeConstant.MAX_MOVE_OFFSET) {
      this.marginBottom = offsetY;
    } else {
      if (this.changedIndex) {
        return;
      }
      this.changedIndex = true;
      this.startAnimation(offsetY < 0);
    }
  }

  build() {
    Column() {
      Header({ title: "返回" })
        .position({ top: 15 })
        .zIndex(999)
      Navigation() {
        Stack() {
          if (this.zonesList.length == 0) {
            SpinKit({
              spinType: SpinType.spinC,
              spinColor: "#dd5145",
              spinSize: 80
            })
          }else{
            ForEach(this.zonesList, (item: KnowledgeItem, index?: number) => {
              Row() {
                Image(item.thumbnail)
                  .objectFit(ImageFit.Cover)
                  .borderRadius(16)
                  .margin({ bottom: KnowledgeConstant.SHADOW_RADIUS })
                  .shadow({
                    radius: KnowledgeConstant.SHADOW_RADIUS,
                    color: `rgba(0,0,0,0.3)`,
                    offsetY: KnowledgeConstant.SHADOW_RADIUS / KnowledgeConstant.DOUBLE_NUM
                  })
                  .opacity(1 - Math.min(KnowledgeConstant.HALF_COUNT,
                    Math.abs(this.getImgCoefficients(index))) * KnowledgeConstant.OPACITY_COEFFICIENTS)
                  .blur(KnowledgeConstant.OFFSET_COEFFICIENTS * Math.abs(this.getImgCoefficients(index)))
              }
              .width(index !== this.aheadIndex && this.getImgCoefficients(index) === 0
                ? KnowledgeConstant.SWIPER_DEFAULT_WIDTH
                : `${KnowledgeConstant.ITEM_WIDTH -
                  KnowledgeConstant.OFFSET_COEFFICIENTS * Math.abs(this.getImgCoefficients(index))}%`)
              .aspectRatio(KnowledgeConstant.SWIPER_ASPECT_RATIO)
              .borderRadius(16)
              .offset({ x: 0, y: this.getOffSetY(index) })
              .zIndex(index !== this.aheadIndex && this.getImgCoefficients(index) === 0
                ? 0 : KnowledgeConstant.HALF_COUNT - Math.abs(this.getImgCoefficients(index)))
            }, (item: KnowledgeItem) => JSON.stringify(item))
          }
        }
        .width(KnowledgeConstant.FULL_PERCENT)
        .height(KnowledgeConstant.FULL_PERCENT)
        .margin({ top: KnowledgeConstant.SHADOW_RADIUS / KnowledgeConstant.DOUBLE_NUM })
        .alignContent(Alignment.Center)
        .onClick(() => {
          router.pushUrl({
            url: KnowledgeConstant.INTRODUCTION_URL,
            params: { id: this.zonesList[this.aheadIndex].id }
          });
        })
        .gesture(
          PanGesture({ direction: PanDirection.Vertical })
            .onActionStart((event: GestureEvent | undefined) => {
              if (!event) {
                return;
              }
              this.changedIndex = false;
              this.handlePanGesture(event.offsetY);
            })
            .onActionUpdate((event: GestureEvent | undefined) => {
              if (!event) {
                return;
              }
              this.handlePanGesture(event.offsetY);
            })
            .onActionEnd(() => {
              animateTo({
                duration: KnowledgeConstant.SWIPER_DURATION,
              }, () => {
                this.marginBottom = 0;
              });
            })
        )
      }
      .backgroundColor("#F1F3F5")
      .title(KnowledgeConstant.INTRODUCTION_TITLE)
      .titleMode(NavigationTitleMode.Full)
      .hideBackButton(true)
    }

  }
}