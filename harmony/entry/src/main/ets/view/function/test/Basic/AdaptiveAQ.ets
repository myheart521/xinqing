import { router } from "@kit.ArkUI";
import { SpinKit, SpinType } from "@pura/spinkit";
import { adaptiveTestContent } from "../../../../api/function/TestApi";
import { AdaptiveDataSource } from "../../../../viewmodel/function/test/basic/AdaptiveDataSource";

// 定义 Topic 类型
interface Topic {
  id: number;
  question: string;
  optionsA: string;
  valA: number;
  optionsB: string;
  valB: number;
  optionsC: string;
  valC: number;
  optionsD: string;
  valD: number;
  optionsE: string;
  valE: number;
}

@Component
export struct AdaptiveAQ {
  // 定义数据源
  @State data: AdaptiveDataSource = new AdaptiveDataSource([]);
  // 总题目数
  @Link allTopic: number
  // 已完成题目数
  @Link finishTopic: number
  // 存储答案
  @State res: string[] = [];
  // Swiper 控制器
  private swiperController: SwiperController = new SwiperController();

  //渲染之前进行网络请求
  aboutToAppear(): void {
    this.getQuestionList()
  }

  //获取问题数据
  getQuestionList = async () => {
    const res = await adaptiveTestContent()
    this.data.setData(res.data)
    this.allTopic = res.data.length
  }

  build() {
    Column() {
      //答题区
      if (this.data.totalCount() == 0) {
        SpinKit({
          spinType: SpinType.spinC,
          spinColor: "#dd5145",
          spinSize: 80
        })
      } else {
        Swiper(this.swiperController) {
          LazyForEach(this.data, (item: Topic) => {
            //嵌套组件 显示题目
            Column() {
              //题干
              Text(item.id + ". " + item.question)
              Blank().height(10)
              //选项A
              Button() {
                Row() {
                  Text(item.optionsA)
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
                  if (item.valA !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valA.toString()); // 将数字转换为字符串
                  }
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/AdaptiveShowResult',
                    params: {
                      ans: ans,
                    }
                  })

                } else {
                  //1-保存值
                  if (item.valA !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valA.toString())
                  }
                  //2-换到下一题
                  this.swiperController.showNext()
                }

              })

              //选项B
              Button() {
                Row() {
                  Text(item.optionsB)
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
                  if (item.valB !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valB.toString()); // 将数字转换为字符串
                  }
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/AdaptiveShowResult',
                    params: {
                      ans: ans,
                    }
                  })

                } else {
                  //1-保存值
                  if (item.valB !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valB.toString())
                  }
                  //2-换到下一题
                  this.swiperController.showNext()
                }

              })

              //选项C
              Button() {
                Row() {
                  Text(item.optionsC)
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
                  if (item.valC !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valC.toString()); // 将数字转换为字符串
                  }
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/AdaptiveShowResult',
                    params: {
                      ans: ans,
                    }
                  })

                } else {
                  //1-保存值
                  if (item.valC !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valC.toString())
                  }
                  //2-换到下一题
                  this.swiperController.showNext()
                }
              })

              //选项D
              Button() {
                Row() {
                  Text(item.optionsD)
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
                  if (item.valD !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valD.toString()); // 将数字转换为字符串
                  }
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/AdaptiveShowResult',
                    params: {
                      ans: ans,
                    }
                  })

                } else {
                  //1-保存值
                  if (item.valD !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valD.toString())
                  }
                  //2-换到下一题
                  this.swiperController.showNext()
                }
              })

              //选项E
              Button() {
                Row() {
                  Text(item.optionsE)
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
                  if (item.valE !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valE.toString()); // 将数字转换为字符串
                  }
                  let ans = this.res.join('')
                  console.log("ans:" + ans)
                  //跳转界面（携带拼接好的选项字符串）
                  router.replaceUrl({
                    url: 'pages/function/test/basic/AdaptiveShowResult',
                    params: {
                      ans: ans,
                    }
                  })

                } else {
                  //1-保存值
                  if (item.valE !== undefined) { // 添加了对 undefined 的检查
                    this.res.push(item.valE.toString())
                  }
                  //2-换到下一题
                  this.swiperController.showNext()
                }
              })

            }.width('90%')
            .height(300)
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
