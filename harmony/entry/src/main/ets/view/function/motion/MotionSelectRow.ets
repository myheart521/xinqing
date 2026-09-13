import { MotionSearchModel } from "../../../model/function/motion/MotionSearchModel"

@Component
export struct MotionSelectRow {
  @Link @Watch("onDegreeChange") degreeOfDifficulty: string
  @Link @Watch("onTimeChange") timeRange: string
  @ObjectLink searchContent: MotionSearchModel
  @State degreeIndex: number = 0
  @State timeIndex: number = 0

  onDegreeChange() {
    if (this.degreeOfDifficulty === 'K1零基础') {
      this.searchContent.league = "K1"
    } else if (this.degreeOfDifficulty === 'K2初学') {
      this.searchContent.league = 'K2'
    } else if (this.degreeOfDifficulty === 'K3进阶') {
      this.searchContent.league = 'K3'
    } else if (this.degreeOfDifficulty === 'K4强化') {
      this.searchContent.league = 'K4'
    } else if (this.degreeOfDifficulty === 'K5挑战') {
      this.searchContent.league = 'K5'
    } else {
      this.searchContent.league = ""
    }
    console.log("MotionSelectRow页面做一个标记:searchContent.league", this.searchContent.league)
  }

  onTimeChange() {
    if (this.timeRange === ">=31min") {
      this.searchContent.startTime = 31
      this.searchContent.endTime =null
    } else if (this.timeRange === "21-30min") {
      this.searchContent.startTime = 21
      this.searchContent.endTime = 30
    } else if (this.timeRange === "16-20min") {
      this.searchContent.startTime = 16
      this.searchContent.endTime = 20
    } else if (this.timeRange === "11-15min") {
      this.searchContent.startTime = 11
      this.searchContent.endTime = 15
    } else if (this.timeRange === "6-10min") {
      this.searchContent.startTime = 6
      this.searchContent.endTime = 10
    } else if (this.timeRange === "<=5min") {
      this.searchContent.startTime = 0
      this.searchContent.endTime = 5
    } else {
      this.searchContent.startTime = null
      this.searchContent.endTime = null
    }
    console.log("MotionSelectRow页面做一个标记:searchContent.startTime-searchContent.endTime:", this.searchContent.startTime + "-" + this.searchContent.endTime)
  }

  onResettingClick: () => void =()=>{}
  onSelectSearchClick: () => void=()=>{}

  build() {
    Row({ space: 5 }) {
      Select([
        { value: 'K1零基础' },
        { value: 'K2初学' },
        { value: 'K3进阶' },
        { value: 'K4强化' },
        { value: 'K5挑战' }
      ])
        .width(100)
        .selected(this.degreeIndex)
        .value(this.degreeOfDifficulty)
        .font({ size: 16, weight: 500 })
        .fontColor('#182431')
        .selectedOptionFont({ size: 16, weight: 400 })
        .optionFont({ size: 16, weight: 400 })
        .onSelect((index: number, text?: string | undefined) => {
          console.info('难度value:' + text)
          console.info('难度Index:' + index)
          this.degreeIndex = index;
          if (text) {
            this.degreeOfDifficulty = text;
          }
        })

      Select([{ value: '>=31min' },
        { value: '21-30min' },
        { value: '16-20min' },
        { value: '11-15min' },
        { value: '6-10min' },
        { value: '<=5min' }
      ])
        .width(100)
        .selected(this.timeIndex)
        .value(this.timeRange)
        .font({ size: 16, weight: 500 })
        .fontColor('#182431')
        .selectedOptionFont({ size: 16, weight: 400 })
        .optionFont({ size: 16, weight: 400 })
        .onSelect((index: number, text?: string | undefined) => {
          console.info('时间value:' + text)
          console.info('时间Index:' + index)
          this.timeIndex = index;
          if (text) {
            this.timeRange = text;
          }
        })
      Button("重置")
        .fontColor('#24c68a')
        .borderWidth(1)
        .borderColor('#24c68a')
        .backgroundColor(Color.White)
        .onClick(async () => {
          this.timeIndex = 0
          this.degreeIndex = 0
          this.onResettingClick()
        })
      Button("确认")
        .borderWidth(1)
        .borderColor('#24c68a')
        .backgroundColor("#24c68a")
        .onClick(async () => {
          this.onSelectSearchClick()
        })
    }
    .height(25)
    .margin(5)
  }
}

