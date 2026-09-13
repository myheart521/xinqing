import { TestQuestionItemModel } from "../../../model/function/test/TestQuestionItemModel"

export class MyDataSource implements IDataSource {
  private data:TestQuestionItemModel[] = []

  constructor(data:TestQuestionItemModel[]) {
    this.data = data
  }
  setData(data:TestQuestionItemModel[]){
    this.data = data
  }

  unregisterDataChangeListener(listener: DataChangeListener): void {
    // 取消注册方法，不理解什么意思
  }

  registerDataChangeListener(listener: DataChangeListener): void {
    // 在数据发生变化时，调用监听器的方法

  }

  getData(index: number) {
    return this.data[index]
  }

  totalCount(): number {
    return this.data.length
  }
}