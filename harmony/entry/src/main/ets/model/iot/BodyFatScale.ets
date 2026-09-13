
//查询和更新
export class  bodyFatScaleModel{
  id:number|null=0
  height:number=0
  weight:number=0
  bmi:number=0
  createTime:string=""
  tiZhiLv:number=0
  userId:number=0
  equipId:string=""
}

//连续日期数据
export class bodyFatScaleChart{
  dateList:string=""
  heightList:string=""
  weightList:string=""
  bmiList:string=""
  tiZhiLvList:string=""
}

//真正使用的格式
export class useData{
  info:string=""
  data:string|number|null=""
  image:string|Resource=$r("app.media.load")
}

