import { all } from '@ohos/axios';
import promptAction from '@ohos.promptAction'
import { router } from '@kit.ArkUI';

@Entry
@Component
struct StartPage {
  @State duration: number = 2;
  private intervalId: number = -1;

  goToHomePage() {
    clearInterval(this.intervalId);
    router.replaceUrl({ url: 'pages/login/LoginPage' });
  }

  aboutToAppear() {
    this.intervalId = setInterval(() => {
      if (this.duration > 0) {
        this.duration -= 1;
      } else {
        this.goToHomePage();
      }
    }, 1000)
  }

  build() {
    Stack() {
      //logo
      Column() {
        Row() {
          Image($r('app.media.logo'))
            .width(100)
            .height(100)
            .objectFit(ImageFit.Contain)
          Column({ space: 4 }) {
            Text('心晴')
              .fontFamily('HarmonyHeiTi_Bold')
              .fontSize(26)
              .fontColor("#182431")
              .fontWeight(700)
              .letterSpacing(0.1)

            Text('AI聊天APP')
              .fontFamily('HarmonyHeiTi')
              .fontColor("#182431")
              .fontWeight(400)
              .letterSpacing(3.4)
              .opacity(0.4)
              .fontSize(16)
          }
          .alignItems(HorizontalAlign.Start)

        }
        .height(100)
        .width('100%')
        .justifyContent(FlexAlign.Center)

      }
      .height('100%')
      .justifyContent(FlexAlign.Center)
    }
    .width('100%')
    .height('100%')

  }
}