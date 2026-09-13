import { DialogUtil } from "@pura/harmony-utils"
import { SpinKit, SpinType } from "@pura/spinkit"
import { addDiscuss, getDiscuss, getDiscussType } from "../../api/group/DiscussApi"
import CustomSearch from "../../components/CustomSearch"
import DiscussAddModel from "../../model/group/DiscussAddModel"
import DiscussModel, { DiscussType } from "../../model/group/DiscussModel"
import DiscussAdd from "../../view/group/DiscussAdd"
import DiscussItem from "../../view/group/DiscussItem"

@Entry
@Component
export default struct Discuss {
  private controller: TabsController = new TabsController()
  @State fontColor: string = ''
  @State selectFontColor: string = ''
  @State currentSelectTab: string = ''
  @State currentIndex: number = 0
  @State API_URL_GET: number = 1
  @State API_URL_POST: string = 'http://localhost:8080/luntan/add'
  @State inDiscussId: number = -1
  @State @Watch('onKeyWordsChange') keyWords: string = ''
  @State searchData: DiscussModel[] = []
  //获取的初始数据
  @State originalData: DiscussModel[] = []
  //不同类别的数据
  @State careerList: DiscussModel[] = []
  @State loveList: DiscussModel[] = []
  @State examList: DiscussModel[] = []
  @State studyList: DiscussModel[] = []
  //用于渲染的数据
  @State discussList: DiscussModel[] = []
  @State addContext: string = ''
  @State type: number = 0
  @State params: DiscussAddModel = new DiscussAddModel()
  @StorageLink("userName") userName: string = "示例资料"
  //导航栏内容
  @State isAddDialogShow: boolean = false
  @State code: number = 0
  @State tabArr: DiscussType[] = []
  @State isShow:boolean = false
  //新增评论
  onAdd = async () => {
    if (this.addContext) {
      this.isShow = false
      this.params.typeId = this.type
      this.params.context = this.addContext
      this.params.userName = this.userName
      console.log("发表的用户名称：",this.userName)
      this.params.title = ""
      console.log('请求时的username', this.params.userName)
      const res = await addDiscuss(this.params)
      if (res.code == 1) {
        this.getDiscussData(this.API_URL_GET)
        this.isShow = true
      } else {
        this.isShow = true
        DialogUtil.showConfirmDialog({
          title: "新增帖子失败",
          message: res.msg,
          confirm: "确定",
          onAction: () => {
          }
        })
      }
      this.onClose()
    }
  }
  //关闭评论
  onClose = () => {
    this.addContext = ''
    this.isAddDialogShow = false
  }

  //对获取的数据按时间排序
  onOrderChange() {
    let currentList: DiscussModel[] = []
    if (this.currentIndex === 0) {
      currentList = this.careerList
    } else if (this.currentIndex === 1) {
      currentList = this.loveList
    } else if (this.currentIndex === 2) {
      currentList = this.examList
    } else {
      currentList = this.studyList
    }
    const length = currentList.length
    for (let i = 0; i < length - 1; i++) {
      for (let j = 0; j < length - i - 1; j++) {
        const timeA = currentList[j].createTime
        const timeB = currentList[j+1].createTime
        if (timeA < timeB) {
          const temp = currentList[j]
          currentList[j] = currentList[j+1]
          currentList[j+1] = temp
        }
      }
    }
    this.discussList = currentList
  }

  //搜索框数据变化时搜索到的数据
  onKeyWordsChange() {
    this.isShow = false
    setTimeout(()=>{
      let currentList: DiscussModel[] = []
      if (this.currentIndex === 0) {
        currentList = this.careerList
      } else if (this.currentIndex === 1) {
        currentList = this.loveList
      } else if (this.currentIndex === 2) {
        currentList = this.examList
      } else {
        currentList = this.studyList
      }
      if (this.keyWords.length === 0) {
        this.discussList = currentList
      } else {
        let resultList: DiscussModel[] = []
        for (const item of currentList) {
          if (item.context.includes(this.keyWords)) {
            resultList.push(item)
          }
        }
        this.discussList = resultList
      }
      this.isShow = true
    },1000)
  }

  @Builder
  TabBuilder(index: number, select: string) {
    Column() {
      Text(select)
        .fontColor(this.currentIndex === index ? this.fontColor : this.selectFontColor)
        .fontSize(this.currentIndex === index ? 20 : 16)
        .fontWeight(this.currentIndex === index ? FontWeight.Bold : FontWeight.Normal)
        .lineHeight(24)
      Divider()
        .strokeWidth(3)
        .color('#007DFF')
        .opacity(this.currentIndex === index ? 1 : 0)
        .animation({
          duration: 400
        })
        .width(100)
    }
    .width(100)
  }

  //请求获取数据
  getDiscussData = async (id: number) => {
    try {
      this.isShow = false
      const res = await getDiscuss(id)
      if (res.code == 1) {
        let data: DiscussModel[] = res.data.records
        this.judge(data)
      } else {
        DialogUtil.showConfirmDialog({
          title: "获取数据失败",
          message: res.msg,
          confirm: "确定",
          onAction: () => {
          }
        })
      }
      this.isShow = true
    } catch (e) {
      AlertDialog.show({ message: '获取数据失败了' + JSON.stringify(e) })
    }
  }
  getTabData = async () => {
    const res = await getDiscussType()
    this.tabArr = res.data
  }

  //判断赋值
  judge(data: DiscussModel[]) {
    if (this.currentIndex === 0) {
      this.careerList = data
      this.discussList = this.careerList
    } else if (this.currentIndex === 1) {
      this.loveList = data
      this.discussList = this.loveList
    } else if (this.currentIndex === 2) {
      this.examList = data
      this.discussList = this.examList
    } else {
      this.studyList = data
      this.discussList = this.studyList
    }
  }

  //清空搜索框关键词
  cleanKeywords = () => {
    this.keyWords = ''
    let currentList: DiscussModel[] = []
    if (this.currentIndex === 0) {
      currentList = this.careerList
    } else if (this.currentIndex === 1) {
      currentList = this.loveList
    } else if (this.currentIndex === 2) {
      currentList = this.examList
    } else {
      currentList = this.studyList
    }
    this.discussList = currentList
  }

  aboutToAppear() {
    this.getDiscussData(this.API_URL_GET)
    this.getTabData()
  }

  build() {
    Stack() {
      Column() {
        CustomSearch({ keywords: this.keyWords, cleanKeywords: this.cleanKeywords })
          .margin({top:10})
        Tabs({ barPosition: BarPosition.Start, controller: this.controller }) {
          ForEach(this.tabArr, (item1: DiscussType, index1) => {
            TabContent() {
              if (this.currentIndex === index1) {
                if(this.isShow){
                  Column() {
                    List({ space: 5, initialIndex: 0 }) {
                      ForEach(this.discussList, (item: DiscussModel, index) => {
                        ListItem() {
                          DiscussItem({ discuss: item })
                        }
                      })
                    }
                    .listDirection(Axis.Vertical)
                  }
                  .width('100%')
                  .height("100%")
                }else{
                  SpinKit({
                    spinType: SpinType.spinC,
                    spinColor: "#dd5145",
                    spinSize: 40
                  })
                }
              }
            }.tabBar(this.TabBuilder(index1, item1.type))
          })
        }
        .barMode(BarMode.Scrollable)
        .onChange((index: number) => {
          this.currentIndex = index
          this.keyWords = ''
          console.log('index:' + index)
          switch (index) {
            case 0:
              this.API_URL_GET = 1
              this.getDiscussData(1)
              break
            case 1:
              this.API_URL_GET = 2
              this.getDiscussData(2)
              break
            case 2:
              this.API_URL_GET = 3
              this.getDiscussData(3)
              break
            case 3:
              this.API_URL_GET = 4
              this.getDiscussData(4)
              break
          }
        })
        .barHeight(56)
      }.backgroundColor('#f0f0f0')
      .width('100%')
      .height('100%')
      .margin({ bottom: 20 })
      //发表评论
      Row() {
        Grid() {
          GridItem() {
            Image($rawfile('functionImage/diary_search.png'))
              .width(16)
          }
          .height('100%')

          GridItem() {
            Text('快来发表你的评论吧')
          }
          .height('100%')
        }
        .width('100%')
        .height(36)
        .columnsTemplate('1fr 16fr 1fr')
        .backgroundColor('#f5f6f7')
        .borderRadius(16)
        .padding({ left: 12, right: 12 })
      }
      .position({bottom:10})
      .padding({ left: 20, right: 20 })
      .onClick(() => {
        this.isAddDialogShow = true
        this.tabArr.forEach((item: DiscussType, index) => {
          if (index === this.currentIndex) {
            this.type = item.id
          }
        })
      })
      if (this.isAddDialogShow) {
        DiscussAdd({ addContext: this.addContext, onClose: this.onClose, onAdd: this.onAdd })
      }
    }
    .width('100%')
    .alignContent(Alignment.Bottom)
  }
}