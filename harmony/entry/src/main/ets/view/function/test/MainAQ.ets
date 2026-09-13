import { MyDataSource } from "../../../viewmodel/function/test/MyDataSource";
import { TestQuestionItemModel } from "../../../model/function/test/TestQuestionItemModel";
import { router } from "@kit.ArkUI";
import { SpinKit, SpinType } from "@pura/spinkit";
import testingQuestionViewModel from "../../../viewmodel/function/test/TestingQuestionViewModel";
import { testContent } from "../../../api/function/TestApi";


@Component
export struct MainAQ {
  // 定义数据源
  @Link data: MyDataSource;
  // 总题目数
  @Link allTopic: number
  // 已完成题目数
  @Link finishTopic: number
  // 存储答案
  @Prop testId: number
  @State res: string[] = [];
  // Swiper 控制器
  @State isShow: boolean = false
  private swiperController: SwiperController = new SwiperController();

  //渲染之前进行网络请求
  aboutToAppear() {
    //初始化数据
    // this.getTestContent()
  }

  build() {
    Column() {
      //答题区
      if (this.data.totalCount() != 0) {
        Swiper(this.swiperController) {
          LazyForEach(this.data, (item: TestQuestionItemModel, index: number) => {
            //嵌套组件 显示题目
            Column() {
              //题干
              Text(this.data.getData(index).questionNumber + ". " + this.data.getData(index).question)
              //选项
              ForEach(this.data.getData(index).options, (optionItem: string, optionIndex: number) => {
                Blank().height(10)
                Button() {
                  Row() {
                    Text(optionItem)
                  }
                }
                .type(ButtonType.Normal)
                .height(50) // 设置 Button 的高度为 50
                .width(200) // 设置 Button 的宽度为 200 像素
                .backgroundColor("#f0f0f0") // 设置背景颜色为浅灰色
                .onClick(() => {
                  this.finishTopic = item.questionNumber
                  if (index + 1 === this.data.totalCount()) {
                    //1-保存值
                    this.res.push(this.data.getData(index).val[optionIndex])
                    let ans = this.res.join('')
                    console.log("ans:" + ans)
                    //跳转界面（携带拼接好的选项字符串）
                    //请求数据
                    router.replaceUrl({
                      url: 'pages/function/test/TestingShowRes',
                      params: {
                        data: this.data,
                        ans: ans
                      }
                    })
                  } else {
                    //1-保存值
                    this.res.push(this.data.getData(index).val[optionIndex])
                    //2-换到下一题
                    this.swiperController.showNext()
                  }
                })
              })
            }
            .width('90%')
            .height(280)
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
      } else {
        SpinKit({
          spinType: SpinType.spinC,
          spinColor: "#dd5145",
          spinSize: 80
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
