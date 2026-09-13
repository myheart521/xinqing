import { CommonConstants } from '../../common/constant/CommonConstants';
import { navItem } from '../../model/index/navItem';
import { navListData, navListSource } from '../../viewmodel/index/SliderBarViewModel';
import { Home } from '../../view/index/lot/Home';
import { addEquipment, getEquipment } from '../../api/iot/Equipment';
import { changeAddEquipment, changeSlider, judgeExistEquipment } from '../../utils/iot/IOTUtils';
import { DialogUtil } from '@pura/harmony-utils';


@Component
export struct SliderBar {
  @State navList: navItem[] = [];
  @State active: number = 0;
  @Provide show: boolean = false;
  //所有可添加设备设备
  @State allEquipment: navItem[] = []
  //为子组件提供是否要重新请求
  @Provide reRequest: boolean = false

  aboutToAppear(): void {
    this.allEquipment = navListData()
    this.getEquipmentData()
  }

  //获取tab数据并处理
  getEquipmentData = async () => {
    const res = await getEquipment()
    this.navList = changeSlider(res.data)
  }
  //请求创建新的设备
  createEquipment = async (equipment: string) => {
    const res = await addEquipment(equipment)
    if (res.code == 1) {
      this.reRequest = true
      this.getEquipmentData()
    }
  }

  @Builder
  MyMenu() {
    Menu() {
      ForEach(this.allEquipment, (item: navItem, index) => {
        MenuItem({ startIcon: item.image, content: item.name })
          .enabled(judgeExistEquipment(item.name, this.navList))
          .onClick(() => {
            DialogUtil.showPrimaryDialog({
              title: "添加设备",
              message: `确定要添加设备${item.name}吗？`,
              primaryButton: {
                value: "取消",
                backgroundColor: Color.Gray,
                fontColor: Color.Black,
                action: () => {
                }
              },
              secondaryButton: {
                value: "确认",
                backgroundColor: "#00d886",
                fontColor: Color.Black,
                action: () => {
                  this.createEquipment(changeAddEquipment(item.name))
                }
              }
            })
          })
      })
    }
  }

  build() {
    SideBarContainer(SideBarContainerType.Overlay) {
      // 侧边栏内容
      Column() {
        ForEach(this.navList, (item: navItem, index: number) => {
          Column() {
            Row() {
              Image(item.image)
                .width(24)
                .height(24)
              Text(item.name)
                .fontSize($r("app.float.lg"))
                .fontColor(Color.Black)
                .fontWeight(FontWeight.Medium)
                .margin({ left: $r("app.float.mp") })
            }
            .height(48)
            .width(CommonConstants.FULL_PARENT)

            if (this.navList.length - 1 !== index) {
              Row()
                .height(0.5)
                .backgroundColor('#0D000000')
                .width('90%')
            }
          }
          .onClick(() => {
            this.active = index;
          })
          .margin({
            top: $r("app.float.xs"),
            left: $r("app.float.xs"),
            right: $r("app.float.xs"),
            bottom: $r("app.float.xs")
          })
          .justifyContent(FlexAlign.Center)
          .width(264)
          .height(48)
          .padding({ left: $r("app.float.mp") })
          .borderRadius(16)
          .backgroundColor(this.active === index ? '#1A0A59F7' : '')
        }, (item: number, index: number) => JSON.stringify(item) + index)
        Row() {
          Image($r('app.media.add'))
            .width(40)
            .height(40)
          Text($r('app.string.add_device')).margin({ left: $r("app.float.sm") })
        }
        .width('100%')
        .margin({ top: 10, left: $r("app.float.mp") })
        .bindMenu(this.MyMenu())
      }
      .width(272)
      .height(344)
      .backgroundColor(Color.White)
      .height(CommonConstants.FULL_PARENT)
      .padding({ top: 104 })
      .backgroundColor('#E9EAEC')

      //容器内容
      Column() {
        Home()
      }
      .padding(10)
      .onClick(() => {
        animateTo({
          duration: CommonConstants.ANIMATION_DURATION,
          curve: Curve.EaseOut,
          playMode: PlayMode.Normal,
        }, () => {
          this.show = false;
        })
      })
      .width('100%')
      .height('110%')
      .backgroundColor(this.show ? '#c1c2c4' : '')
    }
    .showSideBar(this.show)
    .controlButton({
      left: 5,
      top: 10,
      height: 40,
      width: 40,
      icons: {
        shown: $r('app.media.changeBack'),
        hidden: $r('app.media.change'),
        switching: $r('app.media.change')
      }
    })
    .onChange((value: boolean) => {
      this.show = value;
    })
    .sideBarWidth('85%')
    .minSideBarWidth('85%')
    .width('100%')

    // .height('110%')
    // .translate({ y: -40 })
  }
}