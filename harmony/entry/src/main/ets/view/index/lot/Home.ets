import { CommonConstants } from '../../../common/constant/CommonConstants';
import { BlankCard } from '../../../components/BlankCard';
import { TabItem } from '../../../model/index/TabItem';
import { changeBodyFatScale, changeEquipment } from '../../../utils/iot/IOTUtils';
import { BodyFatScale } from './BodyFatScale';
import { Running } from './Running';
import { WristWatch } from './WristWatch';
import { getEquipment } from '../../../api/iot/Equipment';
import { equipmentType } from '../../../model/iot/Equipment';

@Component
export struct Home {
  // @State tabArray: Array<TabItem> = initTabData();
  @State tabArray: equipmentType[] = []
  //这个tab不能直接使用，此处是为了保留后端穿过来的数据
  @State tabOriginal: equipmentType[] = []
  @State focusIndex: number = 0;
  @State index: number = 0;
  @Consume show: boolean
  //是否用户新创建设备了
  @Consume @Watch("watchRequest") reRequest: boolean
  private controller: TabsController = new TabsController();
  private listScroller: Scroller = new Scroller();
  private barHeight: number = 0;

  watchRequest(changedPropertyName: boolean) {
    if (this.reRequest == true) {
      console.log("重新请求数据：")
      this.getEquipmentData()
      console.log("重新请求数据：",JSON.stringify(this.tabArray))
      this.reRequest = false
    }
  }

  @Builder
  tabBuilder(tabName: string | Resource, tabIndex: number) {
    Row() {
      Text(tabName)
        .fontSize(tabIndex === this.focusIndex ? $r("app.float.2xl") : $r("app.float.lg"))
        .fontWeight(tabIndex === this.focusIndex ? FontWeight.Bold : FontWeight.Normal)
        .fontColor(tabIndex === this.focusIndex ? $r("app.color.orangered_text") : Color.Black)
        .id(tabIndex.toString())
    }
    .justifyContent(FlexAlign.Center)
    .height(40)
    .margin({ left: $r("app.float.mp"), right: $r("app.float.mp") })
    .onClick(() => {
      this.controller.changeIndex(tabIndex);
      this.listScroller.scrollToIndex(tabIndex, true, ScrollAlign.CENTER);
    })
  }

  aboutToAppear(): void {
    this.getEquipmentData()
  }

  //获取tab数据
  getEquipmentData = async () => {
    const res = await getEquipment()
    this.tabOriginal = res.data
    //改变类型
    this.tabArray = changeEquipment(res.data)
  }

  build() {
    Column() {
      Row() {
        if (this.tabArray.length != 0) {
          // tab导航栏
          Row() {
            List({ scroller: this.listScroller }) {
              ForEach(this.tabArray,
                (item: TabItem, index: number) => {
                  this.tabBuilder(item.label, index);
                }, (item: TabItem, index: number) => JSON.stringify(item) + index)
            }
            .width(CommonConstants.FULL_PARENT)
            .height(CommonConstants.FULL_PARENT)
            .listDirection(Axis.Horizontal)
            .scrollBar(BarState.Off)
          }
          .width("85%")
        }
      }
      .width(CommonConstants.FULL_PARENT)
      .justifyContent(FlexAlign.End)
      .alignItems(VerticalAlign.Center)
      .alignItems(VerticalAlign.Center)
      .height(52)
      .margin({ top: 8 })

      if (this.tabArray.length != 0) {
        Tabs({ barPosition: BarPosition.Start, controller: this.controller }) {
          ForEach(this.tabOriginal, (item: equipmentType, index) => {
            if (item.label == "body") {
              // 智能体脂秤
              TabContent() {
                BodyFatScale()
              }
            } else if (item.label == "running") {
              // 跑步机
              TabContent() {
                Running()
              }
            } else if (item.label == "smartWatch") {
              // 智能手表
              TabContent() {
                WristWatch()
              }
            }

          })
        }
        .width(CommonConstants.FULL_PARENT)
        .barHeight(this.barHeight)
        .animationDuration(CommonConstants.ANIMATION_DURATION)
        .onAnimationStart((index: number, targetIndex: number) => {
          this.focusIndex = targetIndex;
          this.listScroller.scrollToIndex(targetIndex, true, ScrollAlign.CENTER);
        })
      } else {
        BlankCard()
          .height("100%")
      }
    }
    .height(CommonConstants.FULL_PARENT)
  }
}

