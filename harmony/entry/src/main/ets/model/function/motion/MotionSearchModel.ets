@Observed
export class MotionSearchModel{
  title:string=''
  league:string=''
  startTime:number|null=null
  endTime:number|null=null

  constructor(title:string,league:string,startTime:number|null,endTime:number|null) {
    this.title = title
    this.league = league
    this.startTime = startTime
    this.endTime = endTime
  }
}