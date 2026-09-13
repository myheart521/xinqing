export class PCCTimeModel{
  id:number=1
  timeDesc:string='夏季时间（五一之后）'
  startTime:string='08:00'
  endTime:string='12:00'
  pccId:number=1
  approximateTime:string = "上午"

  constructor(id:number,timeDesc:string,starTime:string,endTime:string,pccId:number,approximateTime:string) {
    this.id = id
    this.timeDesc = timeDesc
    this.startTime = starTime
    this.endTime = endTime
    this.pccId = pccId
    this.approximateTime = approximateTime
  }
}