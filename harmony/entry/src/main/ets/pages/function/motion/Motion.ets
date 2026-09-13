import axios, { Axios, AxiosResponse } from "@ohos/axios"
import { MotionItemModel } from "../../../model/function/motion/MotionItemMoel"
import { MotionSearchModel } from "../../../model/function/motion/MotionSearchModel"
import { MotionItem } from "../../../view/function/motion/MotionItem"
import { MotionSelectRow } from "../../../view/function/motion/MotionSelectRow"
import SearchComponent from "../../../view/function/motion/SearchComponent"
import { router } from "@kit.ArkUI"
import { HmSkeleton, IvSkeleton } from "@harmonypass/ivskeleton"
import { motionListRequest } from "../../../api/function/MotionApi"
import { Header } from "../../../components/Header"

// 定义响应数据类型
interface ResponseData<T> {
  // 根据 API 返回的数据结构定义属性
  message: string;
  code: number;
  data: Array<T>; // 或者根据实际数据定义更具体的类型
}

@Entry
@Component
export struct Motion {

  @State arr: number[] = [1, 2, 3, 4]
  //难度
  @State degreeOfDifficulty: string = "难度"
  @State timeRange: string = "时长"
  @State searchContent: MotionSearchModel = new MotionSearchModel('', '', null, null)
  @State motionList: MotionItemModel[] = []
  @State isShow: boolean = false

  aboutToAppear() {
    this.getMotionData()
  }

  onSearchClick = () => {

    this.searchContent.league = ''
    this.searchContent.startTime = null
    this.searchContent.endTime = null
    this.degreeOfDifficulty = "难度"
    this.timeRange = "时长"
    this.getMotionData()
  }
  onResettingClick = async () => {
    this.timeRange = "时长"
    this.degreeOfDifficulty = "难度"
    this.getMotionData()
  }
  onSelectSearchClick = async () => {
    this.getMotionData()
  }
  //获取运动数据
  getMotionData = async () => {
    this.isShow = false
    const res = await motionListRequest(this.searchContent)
    this.isShow = true
    if (res.data) {
      this.motionList = res.data
      this.isShow = true
    } else {
      console.log("数据不存在")
    }
  }

  build() {
    Column() {
      //返回
      Header({ title: "运动" })
      // 搜索框
      Row() {
        SearchComponent({ searchContent: this.searchContent, onSearchClick: this.onSearchClick })
      }
      .margin({ bottom: $r("app.float.mp") })

      // 下拉菜单搜索
      MotionSelectRow({
        degreeOfDifficulty: this.degreeOfDifficulty,
        timeRange: this.timeRange,
        searchContent: this.searchContent,
        onResettingClick: this.onResettingClick,
        onSelectSearchClick: this.onSelectSearchClick
      })
      Column() {
        if (this.isShow) {
          ForEach(this.motionList, (item: MotionItemModel, index: number) => {
            MotionItem({ MotionItem: this.motionList[index] })
              .onClick(() => {
                router.pushUrl({
                  url: "pages/function/motion/InMotion",
                  params: { id: this.motionList[index].id }
                })
              })
          })
        } else {
          Column({ space: 20 }) {
            HmSkeleton({ count: 6 }) // 设置6排
          }
        }
      }
    }
    .backgroundColor(Color.White)
    .height('100%')
  }
}