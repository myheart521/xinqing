@Observed
class TestingModel {
  id: number=0
  title: string=""
  introductions: string=""
  src:string=""
  status:number=0
  userId:number=0
  type:string|null=null

  constructor(id: number, title: string, introductions: string,src:string) {
    this.id = id
    this.title = title
    this.introductions = introductions
    this.src = src
  }
}

export default TestingModel