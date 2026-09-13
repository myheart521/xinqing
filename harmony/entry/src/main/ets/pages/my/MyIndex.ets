import { IconAndDescription, Size } from '../../model/my/IconAndDescription';
import { promptAction, router, TipsDialog } from '@kit.ArkUI';

const Y_OFFSET130 = 130;
const Y_OFFSET_TOP = 76;
const Y_OFFSET60 = 60;
const Y_OFFSET50 = 50;
const Y_OFFSET40 = 40;
const Y_OFFSET20 = 20;
const FULL_SIZE = 100;
const RACE = 1.56;
const BORDER_RADIUS = 8;
const CARD_WIDTH = 150;
const CARD_HEIGHT = 200;
const SETTING_WIDTH = 24;
const HIT_TEST_BLOCK_THRESHOLD = 0.2;

@Component
export struct MyIndex {
  // 用户头像图片后方个人信息相关组件(用户名/选择身份/满意度调查)的透明度
  @State userRowOpacity: number = 1;
  // 返回文字后方的用户名的透明度(进入页面时处于隐藏状态)
  @State userNameOpacity: number = 0;
  // 用户头像图片高度
  @State userImageHeight: number = 50;
  // Scroll组件顶部与上方Row组件(个人信息栏)的上边距
  @State scrollMarginTop: number = 0;
  // 用户头像图片顶部与父组件Row的上边距
  @State userImageMarginTop: number = 0;
  // 用户头像图片左侧与父组件Row的左边距
  @State userImageMarginLeft: number = 0;
  // 订单相关的图标和描述的数据列表
  iconsAndDescriptions1: IconAndDescription[] = [
    new IconAndDescription($rawfile("my/forum.png"), "我的圈子"),
    new IconAndDescription($rawfile("my/collect.png"), "我的收藏"),
    new IconAndDescription($rawfile("my/motion-history.png"), "历史足迹"),
    new IconAndDescription($rawfile("my/interest.png"), "我的关注")
  ]
  // 粉丝/收藏/关注/历史相关的栏目的图标和描述的数据列表
  iconsAndDescriptions2: IconAndDescription[] = [
    new IconAndDescription($rawfile("my/information.png"), "消息通知"),
    new IconAndDescription($rawfile("my/setting.png"), "全局设置"),
    new IconAndDescription($rawfile("my/personal-data.png"), "个人资料"),
    new IconAndDescription($rawfile("my/motion-history.png"), "运动记录")
  ]
  //底部的用户协议部分等
  iconBottomBox: IconAndDescription[] = [
    new IconAndDescription($r("app.media.logo"), "关于心晴", $rawfile("my/right_about.png")),
    new IconAndDescription($rawfile("my/agreement.png"), "使用协议", $rawfile("my/right_agreement.png")),
    new IconAndDescription($rawfile("my/future.png"), "未来规划", $rawfile("my/right_planning.png")),
    new IconAndDescription($rawfile("my/feed_back.png"), "反馈", $rawfile("my/right_feed_back.png"))
  ]
  // 可滑动容器组件的控制器
  scroller: Scroller = new Scroller();

  // 自定义构建函数，将重复使用的UI元素抽象成一个方法。此处样式为：上方图标下方文字
  @Builder
  iconAndDescription(icon: Resource, description: string | Resource, iconSize?: Size, radius?: number) {
    Column() {
      Image(icon)
        .size(iconSize === undefined ? {
          height: 32,
          width: 32
        } : iconSize)
        .borderRadius(radius)
      Text(description)
        .margin({ top: $r('app.float.mp_sm') })
    }
    .onClick(() => {
      promptAction.showToast({ message: description });
    })
  }

  // 自定义构建函数。此处样式为：在Row组件中横向排列IconAndDescription
  @Builder
  customRow(iconsAndDescriptions: IconAndDescription[]) {
    Row() {
      // 性能知识点：此处在Row中横向排列组件，列表项确定、数量较少，且需要一次性加载，因此使用ForEach。在列表项多的情况下，推荐使用LazyForEach
      ForEach(iconsAndDescriptions, (item: IconAndDescription) => {
        Column() {
          this.iconAndDescription(item.icon, item.description)
        }
        .width((FULL_SIZE / iconsAndDescriptions.length).toString() + '%')
      })
    }
    .shadow(ShadowStyle.OUTER_DEFAULT_XS)
    .width("100%")
    .padding(10)
    .margin({ top: 15 })
    .backgroundColor("#ffffff")
    .borderRadius(8)
  }

  // 会员和权益中心栏目
  @Builder
  memberBanner() {
    Row() {
      Column() {
        Text("个人中心")
          .fontSize(20)
          .fontColor("#ed62a8")
        Text("喜欢我们就支持我们吧~")
          .margin({ top: 5 })
          .fontColor("#e73365")
      }
      .alignItems(HorizontalAlign.Start)
      .padding(10)

      Blank() // 在容器主轴方向上自动填充容器空余部分

      Column() {
        Image($rawfile("my/kiss.png"))
          .size({ width: 32, height: 32 })
        // Text("愿你内心永远晴朗")
        //   .margin({ top: 5 })
        //   .fontColor("#e73365")
      }
      .padding(10)
    }
    .height(80)
    .width("100%")
    .borderRadius(8)
    .alignItems(VerticalAlign.Center)
    .linearGradient({
      angle: 45, // 设置颜色渐变起始角度为顺时针方向45°
      colors: [[0x30fa908a, 0.0], [0x44aaaaa, 0.5], [0x30c2b1fa, 1.0]]
    })
    .onClick(() => {
      promptAction.showToast({ message: "会员服务" });
    })
  }

  //下部的框
  @Builder
  bottomBox(iconsAndDescriptions: IconAndDescription[]) {
    ForEach(iconsAndDescriptions, (item: IconAndDescription) => {
      Row() {
        Row() {
          Image(item.icon)
            .width(28)
            .height(28)
          Text(item.description)
            .margin({ left: $r("app.float.mp_xs") })
        }

        if (item.otherIcon) {
          Image(item.otherIcon)
            .width(25)
            .height(25)
        }
      }
      .justifyContent(FlexAlign.SpaceBetween)
      .alignItems(VerticalAlign.Center)
      .shadow(ShadowStyle.OUTER_DEFAULT_XS)
      .width("100%")
      .padding(20)
      .margin({ top: 15 })
      .backgroundColor("#ffffff")
      .borderRadius(8)
    })

  }

  //弹出框
  dialogControllerImage: CustomDialogController = new CustomDialogController({
    builder: TipsDialog({
      imageRes: $rawfile('my/quit.png'),
      content: '你确定退出登录吗?',
      primaryButton: {
        value: '取消',
        action: () => {
        },
      },
      secondaryButton: {
        value: '确认',
        role: ButtonRole.ERROR,
        action: () => {
          router.pushUrl({
            url:"pages/login/LoginPage"
          })
        }
      },
      onCheckedChange: () => {
      }
    })
  })
  /**
   * 创建一个Row组件，用来显示用户信息，然后在下面创建一个Scroll组件，用来显示其他内容，
   * 当Scroll滑动时，Row组件隐藏且里面的子组件渐隐
   */

  @StorageLink("userName") userName: string = "示例资料"
  @StorageLink("userAvatar") userAvatar: string | Resource = $rawfile("functionImage/music_picture5.jpg")
  @StorageLink("userProfile") userProfile: string = "这个人很懒什么都没留下..."

  build() {
    Column() {
      Row() {
        Text(this.userName)
          .margin({ left: 75 })
          .opacity(this.userNameOpacity) // userNameOpacity控制顶部用户名的透明度
        Blank()
        Text("设置")
          .opacity(this.userNameOpacity)// 设置的文字透明度与顶部用户名相同
          .onClick(() => {
            // 当组件的不透明度大于阈值时，响应点击事件，显示文本提示框
            if (this.userNameOpacity > HIT_TEST_BLOCK_THRESHOLD) {
              promptAction.showToast({ message: "设置" });
            }
          })
        Text("客服")
          .margin({
            left: 10,
            right: 10
          })
          .opacity(this.userNameOpacity)// 客服的文字透明度与顶部用户名相同
          .onClick(() => {
            // 当组件的不透明度大于阈值时，响应点击事件，显示文本提示框
            if (this.userNameOpacity > HIT_TEST_BLOCK_THRESHOLD) {
              promptAction.showToast({ message: "设置" });
            }
          })
      }
      .width('100%')
      .alignItems(VerticalAlign.Center)

      Row() {
        Image(this.userAvatar)
          .width(this.userImageHeight)
          .height(this.userImageHeight)// userImageHeight控制头像尺寸
            // userImageMarginTop和userImageMarginLeft控制头像在父容器内的位置
          .margin({ top: this.userImageMarginTop, left: this.userImageMarginLeft })
        Column() {
          Text(this.userName)
          Text(this.userProfile)
            .width("60%")
            .fontSize($r("app.float.xs"))
            .fontColor(Color.Gray)
            .maxLines(1)
        }
        .alignItems(HorizontalAlign.Start)
        .opacity(this.userRowOpacity) // 控制Row组件的透明度
        .padding(10)

        Blank() // 在容器主轴方向上自动填充容器空余部分

        Row() {
          this.iconAndDescription($rawfile("my/setting.png"), "设置", new Size(SETTING_WIDTH, SETTING_WIDTH))
          this.iconAndDescription($rawfile("my/my_customer_service.png"), "客服",
            new Size(SETTING_WIDTH, SETTING_WIDTH))
        }
        .width(80)
        .justifyContent(FlexAlign.SpaceBetween)
        .opacity(this.userRowOpacity)
      }
      .height(100)
      .width('100%')
      // 当组件的不透明度小于阈值时，阻塞子节点和兄弟节点的触摸测试
      .hitTestBehavior(this.userRowOpacity < HIT_TEST_BLOCK_THRESHOLD ? HitTestMode.Block : HitTestMode.Default)

      // Scroll组件中显示个人信息以外的内容
      Scroll(this.scroller) {
        Column() {
          Column() {
            // 会员和权益中心栏目
            this.memberBanner()
          }
          .margin({ top: $r("app.float.mp_sm") })

          // 订单信息栏目
          this.customRow(this.iconsAndDescriptions1)
          // 粉丝/收藏/关注/历史栏目
          this.customRow(this.iconsAndDescriptions2)
          //问题反馈
          //关于心晴
          //使用协议
          //未来规划
          Column() {
            this.bottomBox(this.iconBottomBox)
          }
          .margin({ top: $r("app.float.mp_xl") })

          Row() {
            Text("退出登录")
              .fontSize($r("app.float.3xl"))
              .fontColor("#f39c97")
          }
          .onClick(() => {
            this.dialogControllerImage.open()
          })
          .justifyContent(FlexAlign.Center)
          .alignItems(VerticalAlign.Center)
          .shadow(ShadowStyle.OUTER_DEFAULT_XS)
          .width("100%")
          .padding(20)
          .margin({ top: 15 })
          .backgroundColor("#ffffff")
          .borderRadius(8)

          Text("到底了~")
            .fontColor(Color.Grey)
            .margin({ top: $r("app.float.mp_xl"), bottom: $r("app.float.mp_xl") })
        }
        .width("100%")
      }
      .layoutWeight(1)
      .margin({ top: this.scrollMarginTop })
      .scrollBar(BarState.Off)
      .onDidScroll(() => {
        // TODO: 知识点: Scroll组件绑定onDidScroll事件，然后在此方法里改变该组件的margin和opacity属性值的大小实现组件移动和隐显
        // 性能知识点: onScroll属于频繁回调，不建议在onScroll做耗时和冗余操作
        const yOffset: number = this.scroller.currentOffset().yOffset;
        this.userRowOpacity = yOffset < Y_OFFSET60 ? 1 - yOffset / Y_OFFSET60 : 0;
        if (yOffset < Y_OFFSET_TOP) {
          this.scrollMarginTop = -yOffset;
          this.userImageHeight = Y_OFFSET50 * (1 - (yOffset / Y_OFFSET130));
          this.userImageMarginTop = -yOffset * RACE;
          this.userImageMarginLeft = Y_OFFSET20 * (yOffset / Y_OFFSET60) * RACE;
          if (yOffset > Y_OFFSET40) {
            this.userNameOpacity = (yOffset - Y_OFFSET40) / Y_OFFSET40;
          } else {
            this.userNameOpacity = 0;
          }
        } else {
          this.userImageHeight = Y_OFFSET50 * (1 - (Y_OFFSET_TOP / Y_OFFSET130));
          this.userImageMarginTop = -Y_OFFSET_TOP * RACE;
          this.userImageMarginLeft = Y_OFFSET20 * (Y_OFFSET_TOP / Y_OFFSET60) * RACE;
          this.userNameOpacity = 1;
        }
      })
    }
    .width("100%")
    .padding(12)

    // .linearGradient({
    //   angle: 180, // 渐变方向为从上到下
    //   colors: [[0xffffff, 0.0], [0xfff1f1, 0.2], [0xffdddd, 0.8], [0xffffff, 1.0]] // 优化底部视觉效果
    // })
  }
}