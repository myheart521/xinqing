export class TestQuestionItemModel {
  questionNumber:number=1
  question:string=''
  options:string[]=[]
  val:string[]=[]

  constructor(questionNumber:number,question:string,options:string[],val:string[]) {
    this.questionNumber = questionNumber
    this.question = question
    this.options = options
    this.val = val
  }
}