import MemoModel from '../../../model/function/diary/MemoModel'
import CustomAddDialog from '../../../view/function/diary/CustomAddDialog'
import CustomEditDate from '../../../view/function/diary/CustomEditDate'
import CustomEditDialog from '../../../view/function/diary/CustomEditDialog'
import CustomSearch from '../../../components/CustomSearch'
import CustomTitle from '../../../view/function/diary/CustomTitle'
import MemoItem from '../../../view/function/diary/MemoItem'

@Entry
@Component
struct MyMemo {
  //升序与降序
  @State @Watch('onOrderChange') order: boolean = true
  //笔记列表
  @State list: string = '升序排列'
  //是否展示新增笔记弹窗
  @State isAddDialogShow: boolean = false
  //是否展示编辑笔记弹窗
  @State isEditDialogShow: boolean = false
  //新增笔记内容内容
  @State addContent: string = ''
  //笔记列表
  @State memoList: MemoModel[] = []
  //缓存的原始笔记列表
  originMemoList: MemoModel[] = []
  //搜索框关键词
  @State @Watch('onKeywordsChange') keywords: string = ''
  //编辑笔记内容
  @State editContent: string = ''
  //当order改变时执行该函数||排序状态变化执行的监听函数
  onOrderChange() {
    let orderList: MemoModel[] = this.memoList
    const length = orderList.length
    for (let i = 0; i < length - 1; i++) {
      for (let j = 0; j < length - i - 1; j++) {
        const timeA = orderList[j].updateTime
        const timeB = orderList[j+1].updateTime
        if ((timeA < timeB && this.order) || (timeA > timeB && !this.order)) {
          const temp = orderList[j]
          orderList[j] = orderList[j+1]
          orderList[j+1] = temp
        }

      }

    }
    this.memoList = orderList
  }

  onKeywordsChange() {
    if (this.keywords.length === 0) {
      this.memoList = this.originMemoList
    } else {
      let resultList: MemoModel[] = []
      for (const item of this.originMemoList) {
        if (item.content.includes(this.keywords)) {
          resultList.push(item)
        }
      }
      this.memoList = resultList
    }
  }
  //点击返回，此处使用箭头函数使this指向index页面
  onClose = () => {
    this.isAddDialogShow = false
    this.isEditDialogShow = false
    this.addContent = ''
    this.editContent = ''
    this.editMemoIndex = -1
  }
  //清空搜索框关键词
  cleanKeywords = () => {
    this.keywords = ''
  }
  //新增心情笔记
  createMemo = () => {
    const date: Date = new Date()
    const newMemo: MemoModel = new MemoModel(date.getTime(), date.getTime(), this.addContent)
    this.originMemoList.push(newMemo)
    this.memoList = this.originMemoList
    this.onOrderChange()
    this.onClose()
  }
  //点击笔记
  editMemoIndex: number = -1
  onMemoClick = (memo: MemoModel) => {
    for (let index = 0; index < this.originMemoList.length; index++) {
      if (memo.id === this.originMemoList[index].id) {
        this.editMemoIndex = index
      }
    }
    this.isEditDialogShow = true
    this.editContent = memo.content
  }
  //编辑笔记
  onEdit = () => {
    this.originMemoList[this.editMemoIndex].content = this.editContent
    this.memoList = this.originMemoList
    this.onOrderChange()
    this.onClose()
  }
  //删除笔记
  onDelete = () => {
    this.originMemoList.splice(this.editMemoIndex, 1)
    this.memoList = this.originMemoList
    this.onOrderChange()
    this.onClose()
  }
  //初始化使用for循环刷入数据
  //生命周期aboutToAppear在执行build函数前执行，允许修改状态变量

  aboutToAppear() {
    const initialMemo:string[] =  ['昨天我叫我室友写作业但是他不想写,今天老师检查作业他没写完被老师教训了，看着他的样子我有点想笑','今天天气很热我穿短裤，心情也很好','今天我们班测试2400米了我敢说我们班全校第一个测试的班级，但是我满分很开心。','打球不小心磕到了膝盖要抹药很难受',
      '今天买了双球鞋一直想要的那双之后要吃土了，但是我很开心','今天去公园转了一圈，看到都是情侣心里难受','今天和好朋友去吃饭啦，心情很好','老师布置的作业好难啊，心里有点乱','今天吉他又学会了一首歌，哈哈哈','今天去南校区找高中同学了，很高兴。']
    for (let index = 0; index < 10; index++) {
      let item = new MemoModel(index, new Date().getTime() - 100000000 * (index + 1),initialMemo[index])
      this.originMemoList.push(item)
    }
    this.memoList = this.originMemoList
    this.onOrderChange()
  }

  build() {
    Stack() {
      Column() {
        CustomTitle({
          order: this.order,
          isAddDialogShow: this.isAddDialogShow
        })
        //搜索框
        CustomSearch({ keywords: this.keywords, cleanKeywords: this.cleanKeywords })
        CustomEditDate()
        //笔记列表
        Scroll() {
          Column() {
            ForEach(
              this.memoList,
              (item:MemoModel, index) => {
                MemoItem({ memo: this.memoList[index], onMemoClick: this.onMemoClick })
              }
            )
          }

        }
        .margin({bottom:80})
      }
      .width('100%')
      .height('100%')
      //新增弹窗
      if (this.isAddDialogShow) {
        CustomAddDialog({ addContent: this.addContent, onClose: this.onClose, onAdd: this.createMemo })
      }
      //编辑弹窗
      if (this.isEditDialogShow) {
        CustomEditDialog({
          editContent: this.editContent,
          onClose: this.onClose,
          onEdit: this.onEdit,
          onDelete: this.onDelete
        })
      }

    }
    .backgroundColor('#f1f5f8')
  }
}