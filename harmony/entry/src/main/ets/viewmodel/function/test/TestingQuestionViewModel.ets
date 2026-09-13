import { TestQuestionItemModel } from '../../../model/function/test/TestQuestionItemModel'

class TestingQuestionViewModel{
  TestQuestionInfo:TestQuestionItemModel[] = []
  title:string=''
  getTestQuestionInfo(){
    return this.TestQuestionInfo
  }
  setTestQuestionInfo(data:TestQuestionItemModel[]){
    this.TestQuestionInfo = data
  }
  getTitleInfo(){
    return this.title
  }
  setTitleInfo(data:string){
    this.title = data
  }
}

let testingQuestionViewModel = new TestingQuestionViewModel();

export default testingQuestionViewModel;