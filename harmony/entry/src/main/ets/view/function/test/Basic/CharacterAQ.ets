import { CharacterDataSource } from "../../../../viewmodel/function/test/basic/CharacterDataSource";
import { router } from "@kit.ArkUI";
import { characterTestContent } from "../../../../api/function/TestApi";
import { characterModel } from "../../../../model/function/test/basic/CharacterModel";
import { SpinKit, SpinType } from "@pura/spinkit";

@Component
export struct CharacterAQ {
  // 定义数据源
  @State data: CharacterDataSource = new CharacterDataSource([]);
  // 总题目数
  @Link allTopic: number
  // 已完成题目数
  @Link finishTopic: number
  // 存储答案
  @State res: string[] = [];
  //设置是否加载显示
  // @State
  // Swiper 控制器
  private swiperController: SwiperController = new SwiperController();

  //渲染之前进行网络请求
  aboutToAppear(): void {
    this.getCharacter()
  }

  //获取测试题内容
  getCharacter = async () => {
    const res = await characterTestContent()
    this.data.setData(res.data)
    this.allTopic = res.data.length
  }

  build() {
    Column() {
      if (this.data.totalCount() == 0) {
        SpinKit({
          spinType: SpinType.spinC,
          spinColor: "#dd5145",
          spinSize: 100
        })
      } else {
        //答题区
        Swiper(this.swiperController) {
          LazyForEach(this.data, (item: characterModel) => {
            //嵌套组件 显示题目
            Column() {
              //题干
              Text(item.id + ". " + item.name)
              Blank().height(10)
              //选项A
              Button() {
                Row() {
                  Text(item.optionA)
                }
              }
              .type(ButtonType.Normal)
              .height(50) // 设置 Button 的高度为 50
              .width(200) // 设置 Button 的宽度为 200 像素
              .backgroundColor("#f0f0f0") // 设置背景颜色为浅灰色
              .onClick(() => {
                let index = Number(item.id)
                this.finishTopic = index
                if (index === this.data.totalCount()) {
                  //1-保存值
                  this.res.push(item.valA)
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/CharacterShowResult',
                    params: {
                      ans: ans,
                    }
                  })
                } else {
                  //1-保存值
                  this.res.push(item.valA)
                  //2-换到下一题
                  this.swiperController.showNext()
                }
              })

              //选项B
              Button() {
                Row() {
                  Text(item.optionB)
                }
              }
              .type(ButtonType.Normal)
              .height(50) // 设置 Button 的高度为 50
              .backgroundColor("#f0f0f0") // 设置背景颜色为浅灰色
              .width(200) // 设置 Button 的宽度为 200 像素
              .margin({ top: 10 })
              .onClick(() => {
                let index = Number(item.id)
                this.finishTopic = index
                if (index === this.data.totalCount()) {
                  //如果到最后一题了 显示提交按钮
                  //1-保存值
                  this.res.push(item.valB)
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面
                  router.replaceUrl({
                    url: 'pages/function/test/basic/CharacterShowResult',
                    params: {
                      ans: ans,
                    }
                  })
                } else {
                  //1-保存值
                  this.res.push(item.valB)
                  //2-换到下一题
                  this.swiperController.showNext()
                }

              })
            }.width('90%')
            .height(180)
            .justifyContent(FlexAlign.SpaceEvenly)
          })

        }
        .cardStyle() //自定义卡片样式
        .cachedCount(2)
        .index(0)
        .interval(4000)
        .indicator(false)
        .loop(false)
        .duration(1000)
        .itemSpace(0)
        .disableSwipe(true)
        .curve(Curve.Linear)
        .onChange((index: number) => {
          console.info(index.toString() + this.res.join(''))
        })
      }

    }
  }
}

//自定义卡片样式
@Styles
function cardStyle() {
  .width("95%")
  .padding(20)
  .backgroundColor(Color.White)
  .borderRadius(15)
  .shadow({
    radius: 6,
    color: '#1F000000',
    offsetX: 2,
    offsetY: 4
  })
}
