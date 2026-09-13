import { AdaptiveQuestionModel } from "../../../../model/function/test/basic/AdaptiveQuestionModel";


export class AdaptiveDataSource implements IDataSource {
  private data: Array<AdaptiveQuestionModel> = [];

  constructor(topics: Array<AdaptiveQuestionModel>) {
    this.data = topics;
  }

  totalCount(): number {
    return this.data.length;
  }

  getData(index: number): AdaptiveQuestionModel {
    return this.data[index];
  }
  setData(data:AdaptiveQuestionModel[]){
    this.data = data
  }

  registerDataChangeListener(listener: DataChangeListener): void {
    // 在数据发生变化时，调用监听器的方法
    // 具体实现依赖于框架的具体要求和提供的API
  }

  unregisterDataChangeListener(listener: DataChangeListener): void {
    // 取消注册监听器
    // 具体实现依赖于框架的具体要求和提供的API
  }

  // 其他可能需要的方法，例如数据更新、插入、删除等
  // 同时需要在这些操作完成后调用对应的DataChangeListener方法
}
